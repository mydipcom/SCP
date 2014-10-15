package com.missionsky.scp.dao;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;




import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sun.tools.javac.util.List;
@Component
public class HadoopFileUtil {
	private static final Logger logger = LoggerFactory.getLogger(HadoopFileUtil.class);
	
	//private HadoopFileUtil(){}
	public HadoopFileUtil(){}
	/**
	 * 创建HDFS文件
	 * @param fileName 创建文件的文件名
	 * @param buff 写入的字节数据
	 */
	public static void createFile(String fileName,byte[] buff){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path dfs = new Path(fileName);
			FSDataOutputStream outputStream = hdfs.create(dfs);
			outputStream.write(buff,0,buff.length);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * 上传本地文件
	 * @param srcFile 本地文件路径
	 * @param dstFile 上传后文件路径
	 */
	public static void uploadFile(String srcFile,String dstFile){
		Configuration conf = new Configuration();
		
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path src = new Path(srcFile);
			Path dst = new Path(dstFile);
			hdfs.copyFromLocalFile(src, dst);
			FileStatus status[] = hdfs.listStatus(dst);
			for(FileStatus statu:status){
				System.out.println(statu.getPath());
			}
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * 下载服务器文件
	 * @param srcFile 本地文件路径
	 * @param dstFile 服务端文件路径
	 */
	@SuppressWarnings("deprecation")
	public  String downFile(String srcFile,String dstFile){
		Configuration conf = new Configuration();
		String filevalue = null;
		try {
			FileSystem fs = FileSystem.get(URI.create("/"+"Source"+"/"+dstFile), conf);
			FSDataInputStream in = fs.open(new Path("/"+"Source"+"/"+dstFile+".txt"));
		
			//OutputStream out = new FileOutputStream(srcFile);
			//IOUtils.copyBytes(in, out, 1024, true);
			//System.out.println(in.readLine());
			StringBuffer sb=new StringBuffer();
			String data=null;
					while((data=in.readLine())!=null){
					sb.append(data);	
					}
			filevalue=sb.toString();
			System.out.println("++++++++++++++++"+filevalue);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
		return filevalue;
	}
	@SuppressWarnings("deprecation")
	public  String viewFile(String srcFile,String dstFile){
		Configuration conf = new Configuration();
		String filevalue = null;
		try {
			FileSystem fs = FileSystem.get(URI.create("/"+"Source"+"/"+dstFile), conf);
			FSDataInputStream in = fs.open(new Path("/"+"Source"+"/"+dstFile));
		
			//OutputStream out = new FileOutputStream(srcFile);
			//IOUtils.copyBytes(in, out, 1024, true);
			//System.out.println(in.readLine());
			StringBuffer sb=new StringBuffer();
			String data=null;
					while((data=in.readLine())!=null){
					sb.append(data);	
					}
			filevalue=sb.toString();
			System.out.println("++++++++++++++++"+filevalue);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
		return filevalue;
	}
	
	/**
	 * 创建HDFS目录
	 * @param dir 完整目录路径
	 */
	public static void createDir(String dir) {
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path dfs = new Path("/" + dir);
			hdfs.mkdirs(dfs);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * 重命名HDFS文件
	 * @param oldFile 旧的文件名
	 * @param newFile 新的文件名
	 */
	public static boolean rename(String oldFile,String newFile){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path frpath = new Path(oldFile);
			Path topath = new Path(newFile);
			return hdfs.rename(frpath, topath);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
		return false;
	}
	
	/**
	 * 删除HDFS上的文件
	 * @param fileName 需要删除的文件
	 */
	public  boolean deleteFile(String fileName){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path delef = new Path("/"+"Source"+"/"+fileName);
			boolean bl=hdfs.delete(delef, true);
			System.out.println(bl);
			return bl;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return false;
	}
	
	/**
	 * 查看某个HDFS文件是否存在
	 * @param fileName 检测文件的路径
	 */
	public static boolean checkFile(String fileName){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path findf = new Path(fileName);
			return hdfs.exists(findf);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return false;
	}
	
	/**
	 * 查看HDFS文件的最后修改时间
	 * @param fileName 查看的文件路径
	 * @return
	 */
	public static long getModifyTime(String fileName){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path fpath = new Path(fileName);
			FileStatus fileStatus = hdfs.getFileStatus(fpath);
			return fileStatus.getModificationTime();
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
		return 0;
	}
	
	/**
	 * 读取HDFS某个目录下的所有文件并返回最小的文件
	 * @param dir
	 */
	public ArrayList<Map<String, String>> listAllFile(String dir){
		Configuration conf = new Configuration();
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
	//	ArrayList list=new ArrayList();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Path listf = new Path("/"+dir);
			if(hdfs.exists(listf)){
				FileStatus stats[] = hdfs.listStatus(listf);
				int j = 0;
				for(int i=0;i<stats.length;i++){
					Map<String, String> map = new HashMap<String, String>();
					//System.out.println(stats[i].getPath().toString());
					//System.out.println(stats[i].getModificationTime());
					//System.out.println(getDateStr(stats[i].getModificationTime()));
					String str=stats[i].getPath().toString().substring(27);
					map.put("path", stats[i].getPath().toString().substring(34));
					map.put("time", getDateStr(stats[i].getModificationTime()));
					list.add(map);
					/*if(stats[i].getLen()<stats[j].getLen()){
						j = i;
					}
*/				}
				hdfs.close();
				return list;
				/*if(stats.length>0){
					Map<String, String> map = new HashMap<String, String>();
					map.put("path", stats[j].getPath().toString());
					map.put("size", String.valueOf(stats[j].getLen()));
					return list;
				}*/
			}else {
				return null;
			}
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * 查找某个文件在HDFS集群的位置
	 * @param fileName
	 */
	public ArrayList<Map<String, String>> getFileLoc(String fileName){
		Configuration conf = new Configuration();
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		try {
			FileSystem hdfs = FileSystem.get(conf);
			Map<String, String> map=new HashMap<String, String>();
			Path fpath = new Path("/"+"Source"+"/"+fileName);
			FileStatus status = hdfs.getFileStatus(fpath);
			map.put("path", status.getPath().toString().substring(34));
			map.put("time", getDateStr(status.getModificationTime()));
			list.add(map);
		/*	
		 * BlockLocation[] blkLocations = hdfs.getFileBlockLocations(status, 0, status.getLen());
			for(int i=0;i<blkLocations.length;i++){
				String[] hosts = blkLocations[i].getHosts();
				System.out.println("block_"+i+"_location:"+hosts[0]); 
			}
		
		*/
	
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
		return list;
	}
	
	/**
	 * 获取HDFS集群上所有节点名称信息
	 */
	public void getList(){
		Configuration conf = new Configuration();
		try {
			FileSystem hdfs = FileSystem.get(conf);
			DistributedFileSystem df = (DistributedFileSystem) hdfs;
			DatanodeInfo[] dataNodeStats = df.getDataNodeStats();
			for (int i = 0; i < dataNodeStats.length; i++) {
				logger.info("DataNode_{}_Name:{}",i,dataNodeStats[i].getHostName());
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/**
	 * 追加数据
	 * @param localFile
	 * @param hdfsPath
	 */
	public static void appendFile(String localFile,String hdfsPath){
		Configuration conf = new Configuration();
		
		try {
			FileSystem hdfs = FileSystem.get(conf);
			InputStream in = new FileInputStream(localFile);
			OutputStream out = hdfs.append(new Path(hdfsPath));
			IOUtils.copyBytes(in, out, conf);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void appendFile(byte[] bytes,String hdfsPath){
		
		Configuration conf = new Configuration();
		
		try {
			FileSystem hdfs = FileSystem.get(URI.create(hdfsPath),conf);
			
			
			FSDataOutputStream out = hdfs.append(new Path(hdfsPath));
			out.write(bytes);
			out.flush();
			out.close();
			hdfs.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	public static String getDateStr(long millis) {
	    Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(millis);
                Formatter ft=new Formatter(Locale.CHINA);
                return ft.format("%1$tY-%1$tm-%1$td  %1$tT", cal).toString();
}
	public static void main(String arg[]){
		HadoopFileUtil hdfs=new HadoopFileUtil();
	hdfs.downFile("test1.txt","test1.txt" );
	
	}
}
