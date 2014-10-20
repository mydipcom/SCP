package com.missionsky.scp.dataanalysis.algorithm.dataaggregate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.cassandra.cli.CliParser.newColumnFamily_return;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.missionsky.scp.dataanalysis.Constants;
import com.missionsky.scp.dataanalysis.algorithm.BasicAlgorithm;
import com.missionsky.scp.dataanalysis.entity.AlgorithmInfoAnnotation;
import com.missionsky.scp.dataanalysis.entity.AlgorithmType;
import com.missionsky.scp.dataanalysis.entity.StandardFile;
import com.missionsky.scp.dataanalysis.entity.StandardTask;
import com.missionsky.scp.dataanalysis.utils.HbaseUtil;
import com.missionsky.scp.dataanalysis.utils.PropertiesUtil;
import com.missionsky.scp.dataanalysis.utils.RemoteHadoopUtil;


@AlgorithmInfoAnnotation(name="StoreToHBaseAlgorithm", algorithmType=AlgorithmType.BasicAlgothm, description="StoreToHBase aggregate algorithm")

public class StoreToHBaseAlgorithm  extends BasicAlgorithm{
	
	private static Logger logger = LoggerFactory.getLogger(StoreToHBaseAlgorithm.class);
	
	public static class Mapper01 extends Mapper<Object, Text, Text, Text>{
		
		@Override
		public void map(Object key,Text value, Context context) throws IOException, InterruptedException{
			context.write(new Text(UUID.randomUUID().toString()), value);
		}
	}
	
	
	public static class Reducer01 extends TableReducer<Text, Text, NullWritable>{
		
		private byte[] tableNameBytes = null;
		private ArrayList<byte[]> columnNames = new ArrayList<byte[]>();
		
		
		@Override
		public void setup(Context context){
			
			tableNameBytes = Bytes.toBytes(context.getConfiguration().get("TABLE_NAME"));
			
			String [] tempColumnNames = context.getConfiguration().getStrings("COLUMN_NAMES");
			
			for (int i = 0; i < tempColumnNames.length; i++) {
				columnNames.add(Bytes.toBytes(tempColumnNames[i]));
			   
			}
			
			
		
		}
		
		@Override
		public void reduce(Text key, Iterable<Text> value, Context context) throws IOException, InterruptedException{
			
			Put put = new Put(Bytes.toBytes(key.toString()));
			
			String[] values = pattern.split(key.toString());
			
			for (int i = 0; i < columnNames.size(); i++) {
				put.add(columnNames.get(i),null , Bytes.toBytes(values[i]));
			}
			
			
			
			
			context.write(NullWritable.get(), put);
		}
	}

	@Override
	public int run(Configuration conf, List<String> inputPaths, String outputPath, StandardTask task, String taskName) {
		
		int result = Constants.ALGORITHM_RESULT_SUCCESS;
		Path jarPath = null;
		
		try {
			Path[] input =new Path[inputPaths.size()] ;
			int i = 0;
			
			for (String in : inputPaths) {
				  input[i++] = new Path(in);
			    }
			    
				Job job = Job.getInstance(conf, taskName + "store_to_hbase_job");
				
				jarPath = storeTempJarToHDFS(conf, StoreToHBaseAlgorithm.class);
				
				job.addArchiveToClassPath(jarPath);
				job.addArchiveToClassPath(storeTempJarToHDFS(conf, BasicAlgorithm.class));
				
				job.setJarByClass(StoreToHBaseAlgorithm.class);
				
				job.setMapperClass(Mapper01.class);
				job.setReducerClass(Reducer01.class);
				job.setMapOutputKeyClass(Text.class);
				job.setMapOutputValueClass(Text.class);
				
				FileInputFormat.setInputPaths(job, input);
				
				TableMapReduceUtil.initTableReducerJob(taskName, Reducer01.class, job);
				
				if (!job.waitForCompletion(true)) {
					throw new InterruptedException(job.getJobName());
				} else {
					if (jarPath != null) {
						deleteTempJarFromHDFS(conf, jarPath);
						jarPath = null;
					}
				}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = Constants.ALGORITHM_RESULT_FAIL;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = Constants.ALGORITHM_RESULT_FAIL;
		} catch (InterruptedException e) {
			logger.error("the job {} unexpected interrupted",e.getMessage());
			result = Constants.ALGORITHM_RESULT_FAIL;
		}
		
		if (jarPath != null) {
			deleteTempJarFromHDFS(conf, jarPath);
		}
		
		return result;
	}
	
}
