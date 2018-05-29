package Service;

import java.io.File;
import java.io.FileWriter;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class AppContext {
	public static AppContext getInstance(){
		if(unique_instance==null)
			unique_instance=new AppContext();
		return unique_instance;
	}
	
	public void init(String appPath){
		this.appPath=appPath;
		//this.load();
	}
	
	private String workDir;
	public String getWorkDir(){
		   if(AppUtils.isNullOrEmpty(this.workDir))
			   this.workDir=AppUtils.formatPath(this.appPath)+File.separator+"workDir";
		   return this.workDir;
	   }
		
		public String getAppDir(){
			return this.appPath;
		}
	
	private  static int DefaultPortNo=8096;
	private int servicePort;
	private  boolean runFlag=true;
	
	public boolean runable(){
		if(this.runFlag)
			this.load();
		return runFlag;
	}
	
	public int getServicePort() {
		return servicePort;
	}
	
	private void load(){
		SAXBuilder builder = new SAXBuilder();
		org.jdom.Document doc;
		try {
			String initPath=AppUtils.formatPath(this.appPath)+File.separator+"config"+File.separator+"init.xml";
			doc = builder.build(initPath);
			Element sysParams=doc.getRootElement();
			try{
				String serverPort=sysParams.getChildText("portNo");
				int portNo=Integer.parseInt(serverPort);
				servicePort=portNo;
			}
			catch(Exception exp){
				servicePort=DefaultPortNo;
			}
			try{
				String runFlag=sysParams.getChildText("runFlag");
				if("YES".equalsIgnoreCase(runFlag)||("Y").equalsIgnoreCase(runFlag)){
					  this.runFlag=true;
				}else{
					this.runFlag=false;
				}
			}
			catch(Exception exp){
				this.runFlag=false;
			}
		} catch (Exception e) {
			 Logger.getInstance().logError("AppContext.load()加载启动配置文件ini/init.xml发生异常："+e.toString());
		}
	}
	
	public void setRunFlag(String runflag){
		SAXBuilder builder = new SAXBuilder();
		org.jdom.Document doc;
		try {
			String initPath=AppUtils.formatPath(this.appPath)+File.separator+"config"+File.separator+"init.xml";
			doc = builder.build(initPath);
			Element sysParams=doc.getRootElement();
			Element runFlag=sysParams.getChild("runFlag");
			runFlag.setText(runflag);
			XMLOutputter xmlopt = new XMLOutputter();
			FileWriter writer = new FileWriter(initPath);
			Format fm = Format.getPrettyFormat();
			fm.setEncoding("GB2312");
			xmlopt.setFormat(fm);
			xmlopt.output(doc, writer);
		    writer.close();
		    Logger.getInstance().log(AppContext.ServiceNetworkManager, "AppContext.setRunFlag()成功设置服务开关为:"+runflag);
		}catch (Exception e) {
			Logger.getInstance().logError("AppContext.setRunFlag()设置启动配置文件ini/init.xml发生异常："+e.toString());
		}
	}
		
	private static  AppContext unique_instance;
	private String appPath="";
	private AppContext(){}
	
	 public static final String   ServiceNetwork="Networker";//网络服务线程
     public static final String  ServiceNetworkManager="NetworkerManager";//网络管理服务线程
     public static final int   MsgHeaderLen=10;//协议头部长度
     public static final String GFT_CHECKER="10101010101010101010";
     public static final String DEFAULT_PATH="$BASEDIR";
     public static final int BufferSize=1024;
}
