package com.missionsky.scp.dataanalysis;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.classifier.df.split.Split;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.text.SequenceFilesFromDirectory;
import org.apache.mahout.utils.SplitInputJob;
import org.apache.mahout.vectorizer.SparseVectorsFromSequenceFiles;




import com.missionsky.scp.dataanalysis.mahout.TestNaiveBayesDriver;
import com.missionsky.scp.dataanalysis.utils.RemoteHadoopUtil;
public class  BayesTest{
	public static void main(String args[]){
		RemoteHadoopUtil.setConf(BayesTest.class, Thread.currentThread(), "/hadoop");
		
		String arg[]=new String[]{"-i","/Bayes/20news-bydate-test","-o","/Bayes/20news-SequenceFile"};
		String arg1[]=new String[]{"-i","/Bayes/20news-SequenceFile","-o","/Bayes/news_Test_Vector"};
		String arg2[]=new String[]{"-i","/Bayes/news_Train_Vector/tfidf-vectors","-el","-o","/Bayes/news_mode","-li","/Bayes/news_Train_Vector/label","-ow"};
		String arg3[]=new String[]{"-i","/Bayes/news_Train_Vector/tfidf-vectors","-o","/Bayes/news_Test_Result","-m","/Bayes/news_mode","-l","/Bayes/news_Train_Vector/label","-ow"};
		try {
			//HadoopUtil.delete(new Configuration(),new Path("/Bayes/20news-SequenceFile"));
			
	        //Text to SequenFiles
			//SequenceFilesFromDirectory.main(arg);
			
			//SequenFiles to Vectors
			//SparseVectorsFromSequenceFiles.main(arg1);
			
			//TrainNaiveBayesJob.main(arg2);
			
			TestNaiveBayesDriver.main(arg3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
