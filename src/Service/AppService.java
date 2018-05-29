package Service;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

//版本更新服务端网络管理线程
public class AppService extends Thread {
	
	public static AppService getInstance() {
		 if(unique_instance==null)
			 unique_instance=new AppService();
		 return unique_instance;
	 }
	
	
	private AppService(){
		 peers=new ConcurrentHashMap<Socket,AppPeer>();
	}
	
	 public void addPeer(Socket ip,AppPeer peer){
		 this.peers.put(ip, peer);
	 }
	 
	 public void run(){
		 try{
			  if(this.runFlag){
				  serverSocket=new ServerSocket(AppContext.getInstance().getServicePort());
				  Logger.getInstance().log(AppContext.ServiceNetworkManager, "服务启动开启端口["+AppContext.getInstance().getServicePort()+"]监听");
			  }
			  while(this.runFlag){
					 Socket connection=serverSocket.accept();//accept会自动阻塞等待连接,所以服务退出一定会有一个异常
					 Logger.getInstance().log(AppContext.ServiceNetworkManager,"收到来自["+connection.getInetAddress().getHostAddress()+"]的连接");
					 (new AppPeer(connection,this.peerNo++)).start();
			  	}
			  }
		 catch(Exception e) {
			 Logger.getInstance().log(AppContext.ServiceNetworkManager,"AppService.run()异常:"+e.toString());
			 Logger.getInstance().logError("AppService.run()异常:"+e.toString());
			 this.setRunable(false);
		 }
	 }
	 
	 
	 public synchronized void setRunable(boolean flag){
		 this.runFlag=flag;
	 }
	 
	 public boolean getRunable(){
		 return this.runFlag;
	 }
	 
	 public void exit(){
		 try{
			 this.runFlag=false;
			 if(serverSocket!=null)
				 serverSocket.close();
			 for(Socket ip: this.peers.keySet())
				 this.peers.get(ip).exit();
			 if(unique_instance!=null)
				 unique_instance.interrupt();
			 unique_instance=null;
		 }catch(Exception e){
			 Logger.getInstance().log(AppContext.ServiceNetworkManager,"AppService.exit()异常:"+e.toString());
			 Logger.getInstance().logError("AppService.exit()异常:"+e.toString());
		 }
	 }
	 
	 private static AppService unique_instance;
	 private boolean runFlag=false;
	 private ServerSocket  serverSocket=null;
	 private ConcurrentMap<Socket,AppPeer> peers;
	 private int peerNo=1;
}
