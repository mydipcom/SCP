package com.missionsky.scp.dataanalysis.algorithm.dataminer;

import java.util.List;
import java.util.Map;

import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.clustering.canopy.CanopyDriver;
import org.apache.mahout.clustering.conversion.InputDriver;
import org.apache.mahout.clustering.fuzzykmeans.FuzzyKMeansDriver;
import org.apache.mahout.clustering.syntheticcontrol.fuzzykmeans.Job;
import org.apache.mahout.common.ClassUtils;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.commandline.DefaultOptionCreator;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.common.distance.SquaredEuclideanDistanceMeasure;
import org.apache.mahout.utils.clustering.ClusterDumper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataanalysis.algorithm.MinerAlgorithm;
import com.missionsky.scp.dataanalysis.facadeinterface.BasicTaskAssemblyLine;
import com.missionsky.scp.dataanalysis.utils.RemoteHadoopUtil;

public class fuzzykmeansAlgorithm extends MinerAlgorithm{

	 private static final Logger log = LoggerFactory.getLogger(fuzzykmeansAlgorithm.class);
	  
	  private static final String DIRECTORY_CONTAINING_CONVERTED_INPUT = "data";
	  
	  private static final String M_OPTION = FuzzyKMeansDriver.M_OPTION;
	  
	  public static void main(String[] args) throws Exception {
		RemoteHadoopUtil.setConf(BasicTaskAssemblyLine.class, Thread.currentThread(), "/hadoop");
	    if (args.length > 0) {
	      log.info("Running with only user-supplied arguments");
	      ToolRunner.run(new Configuration(), new fuzzykmeansAlgorithm(), args);
	    } else {
	      log.info("Running with default arguments");
	      Path output = new Path("/Kmeans");
	      Configuration conf = new Configuration();
	      HadoopUtil.delete(conf, output);
	      run(conf, new Path("/Data"), output, new EuclideanDistanceMeasure(), 80, 55, 5, 2.0f, 0.5);
	    }
	  }
	  
	  @Override
	  public int run(String[] args) throws Exception {
	    addInputOption();
	    addOutputOption();
	    addOption(DefaultOptionCreator.distanceMeasureOption().create());
	    addOption(DefaultOptionCreator.convergenceOption().create());
	    addOption(DefaultOptionCreator.maxIterationsOption().create());
	    addOption(DefaultOptionCreator.overwriteOption().create());
	    addOption(DefaultOptionCreator.t1Option().create());
	    addOption(DefaultOptionCreator.t2Option().create());
	    addOption(M_OPTION, M_OPTION, "coefficient normalization factor, must be greater than 1", true);
	    
	    Map<String,List<String>> argMap = parseArguments(args);
	    if (argMap == null) {
	      return -1;
	    }
	    
	    Path input = getInputPath();
	    Path output = getOutputPath();
	    String measureClass = getOption(DefaultOptionCreator.DISTANCE_MEASURE_OPTION);
	    if (measureClass == null) {
	      measureClass = SquaredEuclideanDistanceMeasure.class.getName();
	    }
	    double convergenceDelta = Double.parseDouble(getOption(DefaultOptionCreator.CONVERGENCE_DELTA_OPTION));
	    int maxIterations = Integer.parseInt(getOption(DefaultOptionCreator.MAX_ITERATIONS_OPTION));
	    float fuzziness = Float.parseFloat(getOption(M_OPTION));
	    
	    addOption(new DefaultOptionBuilder().withLongName(M_OPTION).withRequired(true)
	        .withArgument(new ArgumentBuilder().withName(M_OPTION).withMinimum(1).withMaximum(1).create())
	        .withDescription("coefficient normalization factor, must be greater than 1").withShortName(M_OPTION).create());
	    if (hasOption(DefaultOptionCreator.OVERWRITE_OPTION)) {
	      HadoopUtil.delete(getConf(), output);
	    }
	    DistanceMeasure measure = ClassUtils.instantiateAs(measureClass, DistanceMeasure.class);
	    double t1 = Double.parseDouble(getOption(DefaultOptionCreator.T1_OPTION));
	    double t2 = Double.parseDouble(getOption(DefaultOptionCreator.T2_OPTION));
	    run(getConf(), input, output, measure, t1, t2, maxIterations, fuzziness, convergenceDelta);
	    return 0;
	  }
	  
	  /**
	   * Run the kmeans clustering job on an input dataset using the given distance measure, t1, t2 and iteration
	   * parameters. All output data will be written to the output directory, which will be initially deleted if it exists.
	   * The clustered points will reside in the path <output>/clustered-points. By default, the job expects the a file
	   * containing synthetic_control.data as obtained from
	   * http://archive.ics.uci.edu/ml/datasets/Synthetic+Control+Chart+Time+Series resides in a directory named "testdata",
	   * and writes output to a directory named "output".
	   * 
	   * @param input
	   *          the String denoting the input directory path
	   * @param output
	   *          the String denoting the output directory path
	   * @param t1
	   *          the canopy T1 threshold
	   * @param t2
	   *          the canopy T2 threshold
	   * @param maxIterations
	   *          the int maximum number of iterations
	   * @param fuzziness
	   *          the float "m" fuzziness coefficient
	   * @param convergenceDelta
	   *          the double convergence criteria for iterations
	   */
	  public static void run(Configuration conf, Path input, Path output, DistanceMeasure measure, double t1, double t2,
	      int maxIterations, float fuzziness, double convergenceDelta) throws Exception {
	    Path directoryContainingConvertedInput = new Path(output, DIRECTORY_CONTAINING_CONVERTED_INPUT);
	    log.info("Preparing Input");
	    InputDriver.runJob(input, directoryContainingConvertedInput, "org.apache.mahout.math.RandomAccessSparseVector");
	    log.info("Running Canopy to get initial clusters");
	    Path canopyOutput = new Path(output, "canopies");
	    CanopyDriver.run(new Configuration(), directoryContainingConvertedInput, canopyOutput, measure, t1, t2, false, 0.0, false);
	    log.info("Running FuzzyKMeans");
	    FuzzyKMeansDriver.run(directoryContainingConvertedInput, new Path(canopyOutput, "clusters-0-final"), output,
	        convergenceDelta, maxIterations, fuzziness, true, true, 0.0, false);
	    // run ClusterDumper
	    ClusterDumper clusterDumper = new ClusterDumper(new Path(output, "clusters-*-final"), new Path(output, "clusteredPoints"));
	    clusterDumper.printClusters(null);
	  }

}
