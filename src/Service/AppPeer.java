package Service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AppPeer extends Thread {

	public AppPeer(Socket sock,int no){
		this.session=sock;
		this.threadNo=no;
		this.runFlag=true;
		AppService.getInstance().addPeer(sock,this);
		Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo,"AppPeer初始化网络线程服务于ip:"+sock.getInetAddress().getHostAddress());
	}
	
	public void run(){
		try{
			in = this.session.getInputStream();
			out  = this.session.getOutputStream();
			dis=new DataInputStream(in);
			 Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo,"启动并开始处理来自["+this.session.getInetAddress().getHostAddress()+"]指令");
			while(this.runFlag){
				if(this.canLoop){
					this.processNetwork();
					Thread.sleep(100L);
				}else{
					this.runFlag=false;
				}
				this.exit();
			}
			
		}catch(Exception e){
			  Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo,"AppPeer.run()异常："+e.toString());
			  Logger.getInstance().logError("AppPeer.run()异常："+e.toString());
		}
	}
	
	public boolean canLoop=true;
	private void  processNetwork(){
		DataOutputStream dos=null;
		try{
    		 if(this.dis!=null&&this.dis.available()>0){
    			 	this.canLoop=false;
    			 	 String checksum=this.dis.readUTF();//校验头部
    				if(AppContext.GFT_CHECKER.equalsIgnoreCase(checksum)){
    					  long msgLen=this.dis.readLong();//消息长度
    					  String msg=this.dis.readUTF();//消息内容
    					  String appID=msg.split("\\|")[0];
    					  appID=appID.replace(".exe", "");
    					  String versionID=msg.split("\\|")[1];
    					  List<VFile> fileList=new ArrayList<VFile>();
    					  String startPath=AppUtils.formatPath(AppContext.getInstance().getWorkDir())+File.separator+appID+File.separator+versionID;
    					  AppUtils.getFileList(startPath, fileList);
    					  if(fileList.size()>0){
    						  	dos=new DataOutputStream(out);
    						  	dos.writeUTF(AppContext.GFT_CHECKER);
    							dos.flush();
    							dos.writeLong((long)fileList.size());
    			                dos.flush();
    						    for(VFile vfile:fileList){
    						    	Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo, "开始传输文件："+vfile.getLocalPath()+","+vfile.getRemotePath()+","+vfile.getMd5());
    						    	FileInputStream fis=null;
    						    	try{
    						    		File file=new File(vfile.getLocalPath());
    						    		fis =new FileInputStream(file);
    						    		dos.writeUTF(file.getName());//文件名
						                dos.flush();
						                dos.writeUTF(vfile.getRemotePath());//文件在接收端的相对路径
						                dos.flush();
						                dos.writeUTF(vfile.getMd5());//文件在发送端计算得到的md5码
						                dos.flush();
						                dos.writeLong(vfile.getLastModified());//文件最后修改时间
						                dos.flush();
						                dos.writeLong(vfile.getSize());//文件最后修改时间
						                dos.flush();
						               
						                byte[] sendBytes =new byte[AppContext.BufferSize];
						                int length =0;
						                while((length = fis.read(sendBytes,0, sendBytes.length)) >0){
						                    dos.write(sendBytes,0, length);
						                    dos.flush();
						                }
    						    	}catch(Exception e){
    						    		Logger.getInstance().logError("版本文件["+vfile.getLocalPath()+"]传输发生异常："+e.toString());
    						    	}finally{
    			                		if(fis!=null)
    			                			fis.close();
    			                	}
    						    }
    						    Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo,"应用"+appID+"的版本包"+versionID+"推送完成");  
    					  }else{
    						  Logger.getInstance().logError("版本服务器路径["+startPath+"]无法获取到文件");
    					  }
    				}
    		 }else{
    			 this.canLoop=true;
    		 }
		}catch(Exception e){
			Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo,"AppPeer.processNetwork()信号接收发生异常："+e.toString());
			Logger.getInstance().logError("AppPeer.processNetwork()信号接收发生异常："+e.toString());
		}
		finally{
			try{
				if(dos !=null)
	                dos.close();
			}catch(Exception e){
				Logger.getInstance().logError("AppPeer.processRecvBytes()文件发送资源是否发生异常："+e.toString());
			}
		}
	}
	
	
	public void exit(){
		try{
			Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo,"版本推送完成，服务线程结束！");
			if(this.dis!=null)
				this.dis.close();
			if(this.in!=null)
				this.in.close();
			if(this.out!=null)
				this.out.close();
			if(this.session!=null)
				this.session.close();
		}catch(Exception e){
			 Logger.getInstance().logError("AppPeer.exit()释放网络资源异常："+e.toString());
		}
	}
	
	private Socket session;
	private InputStream in = null;
	private OutputStream out = null;
	private DataInputStream dis=null;
	private int threadNo;
	private boolean runFlag=false;
}
