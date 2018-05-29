package Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Logger {
	 public static Logger getInstance(){
		 if(unique_instance==null)
			 unique_instance=new Logger();
		 return unique_instance;
	 }
	 
	 
	 public  void logError(String msg){
		 try{
			 String logDir=AppUtils.formatPath(AppContext.getInstance().getAppDir())+File.separator+"log";
			 File dir=new File(logDir);
			    if(!dir.exists())
			    	dir.mkdirs();
			   String logPath= logDir+File.separator+AppUtils.getCurrentDate("yyyyMMdd")+".err";
			   String currentTime=AppUtils.getCurrentDate("yyyyMMdd-HH:mm:ss.SSS");
			   File file=new File(logPath);
			   FileWriter fw = new FileWriter(file,true);
			   BufferedWriter bw = new BufferedWriter(fw);
			   bw.write(currentTime);
			   bw.write(" : ");
			   bw.write(msg);
			   bw.write("\r\n");
			   bw.close();
			   fw.close();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	 }
	 
	 public  void log(String service,String msg){
		 try{
			 String logDir=AppUtils.formatPath(AppContext.getInstance().getAppDir())+File.separator+"log"+File.separator+AppUtils.getCurrentDate("yyyyMMdd");
			 File dir=new File(logDir);
			    if(!dir.exists())
			    	dir.mkdirs();
			   String logPath= logDir+File.separator+service+".log";
			   String currentTime=AppUtils.getCurrentDate("yyyyMMdd-HH:mm:ss.SSS");
			   File file=new File(logPath);
			   FileWriter fw = new FileWriter(file,true);
			   BufferedWriter bw = new BufferedWriter(fw);
			   bw.write(currentTime);
			   bw.write(" : ");
			   bw.write(msg);
			   bw.write("\r\n");
			   bw.close();
			   fw.close();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	 }
	 
	 private static Logger unique_instance;
	 private Logger(){}
}
