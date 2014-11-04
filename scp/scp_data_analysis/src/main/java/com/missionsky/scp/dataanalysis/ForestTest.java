package com.missionsky.scp.dataanalysis;
import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.classifier.df.mapreduce.BuildForest;
import org.apache.mahout.classifier.df.mapreduce.TestForest;
import org.apache.mahout.classifier.df.tools.Describe;
import org.apache.mahout.common.HadoopUtil;

import com.missionsky.scp.dataanalysis.utils.RemoteHadoopUtil;

public class  ForestTest{
	public static void main(String args[]){
		RemoteHadoopUtil.setConf(ForestTest.class, Thread.currentThread(), "/hadoop");
		String[] arg=new String[]{"-p","/Forest/KDDTrain+.arff",  
                "-f","/Forest/KDDTrain+.info","-d","N", "3", "C", "2","N","C" ,"4" ,"N","C","8","N","2" ,"C","19","N","L"};
		String[] arg1=new String[]{  
                "-d","/Forest/KDDTrain+.arff",  
                "-ds","/Forest/KDDTrain+.info",  
                "-sl","5",  
                "-t","10",  
                "-o","/Forest/output-forest"  
        }; 
		String[] arg2=new String[]{"-i","/Forest/KDDTest+.arff",  
                "-ds","/Forest/KDDTrain+.info",  
                "-m","/Forest/output-forest/forest.seq",  
                "-a","-mr",  
                "-o","/Forest/out-testforest0"};  
    try {
			HadoopUtil.delete(new Configuration(), new Path(arg[Arrays.asList(arg).indexOf("-f")+1]));
			HadoopUtil.delete(new Configuration(), new Path("/Forest/output-forest"));
			HadoopUtil.delete(new Configuration(), new Path("/Forest/out-testforest0"));
			Describe.main(arg); 
    	    BuildForest.main(arg1);
    	    TestForest.main(arg2);
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		//} catch (DescriptorException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
       
	}

}
