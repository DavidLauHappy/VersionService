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
		Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo,"AppPeer��ʼ�������̷߳�����ip:"+sock.getInetAddress().getHostAddress());
	}
	
	public void run(){
		try{
			in = this.session.getInputStream();
			out  = this.session.getOutputStream();
			dis=new DataInputStream(in);
			 Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo,"��������ʼ��������["+this.session.getInetAddress().getHostAddress()+"]ָ��");
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
			  Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo,"AppPeer.run()�쳣��"+e.toString());
			  Logger.getInstance().logError("AppPeer.run()�쳣��"+e.toString());
		}
	}
	
	public boolean canLoop=true;
	private void  processNetwork(){
		DataOutputStream dos=null;
		try{
    		 if(this.dis!=null&&this.dis.available()>0){
    			 	this.canLoop=false;
    			 	 String checksum=this.dis.readUTF();//У��ͷ��
    				if(AppContext.GFT_CHECKER.equalsIgnoreCase(checksum)){
    					  long msgLen=this.dis.readLong();//��Ϣ����
    					  String msg=this.dis.readUTF();//��Ϣ����
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
    						    	Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo, "��ʼ�����ļ���"+vfile.getLocalPath()+","+vfile.getRemotePath()+","+vfile.getMd5());
    						    	FileInputStream fis=null;
    						    	try{
    						    		File file=new File(vfile.getLocalPath());
    						    		fis =new FileInputStream(file);
    						    		dos.writeUTF(file.getName());//�ļ���
						                dos.flush();
						                dos.writeUTF(vfile.getRemotePath());//�ļ��ڽ��ն˵����·��
						                dos.flush();
						                dos.writeUTF(vfile.getMd5());//�ļ��ڷ��Ͷ˼���õ���md5��
						                dos.flush();
						                dos.writeLong(vfile.getLastModified());//�ļ�����޸�ʱ��
						                dos.flush();
						                dos.writeLong(vfile.getSize());//�ļ�����޸�ʱ��
						                dos.flush();
						               
						                byte[] sendBytes =new byte[AppContext.BufferSize];
						                int length =0;
						                while((length = fis.read(sendBytes,0, sendBytes.length)) >0){
						                    dos.write(sendBytes,0, length);
						                    dos.flush();
						                }
    						    	}catch(Exception e){
    						    		Logger.getInstance().logError("�汾�ļ�["+vfile.getLocalPath()+"]���䷢���쳣��"+e.toString());
    						    	}finally{
    			                		if(fis!=null)
    			                			fis.close();
    			                	}
    						    }
    						    Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo,"Ӧ��"+appID+"�İ汾��"+versionID+"�������");  
    					  }else{
    						  Logger.getInstance().logError("�汾������·��["+startPath+"]�޷���ȡ���ļ�");
    					  }
    				}
    		 }else{
    			 this.canLoop=true;
    		 }
		}catch(Exception e){
			Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo,"AppPeer.processNetwork()�źŽ��շ����쳣��"+e.toString());
			Logger.getInstance().logError("AppPeer.processNetwork()�źŽ��շ����쳣��"+e.toString());
		}
		finally{
			try{
				if(dos !=null)
	                dos.close();
			}catch(Exception e){
				Logger.getInstance().logError("AppPeer.processRecvBytes()�ļ�������Դ�Ƿ����쳣��"+e.toString());
			}
		}
	}
	
	
	public void exit(){
		try{
			Logger.getInstance().log(AppContext.ServiceNetwork+this.threadNo,"�汾������ɣ������߳̽�����");
			if(this.dis!=null)
				this.dis.close();
			if(this.in!=null)
				this.in.close();
			if(this.out!=null)
				this.out.close();
			if(this.session!=null)
				this.session.close();
		}catch(Exception e){
			 Logger.getInstance().logError("AppPeer.exit()�ͷ�������Դ�쳣��"+e.toString());
		}
	}
	
	private Socket session;
	private InputStream in = null;
	private OutputStream out = null;
	private DataInputStream dis=null;
	private int threadNo;
	private boolean runFlag=false;
}
