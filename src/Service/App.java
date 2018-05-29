package Service;

public class App {

	public static void main(String[] args){
		if("start".equals(args[0])){
			 App app=App.getInstance();
			 AppContext.getInstance().setRunFlag("Yes");
			 app.run();
		}else if("stop".equals(args[0])){
			 App app=App.getInstance();
			 AppContext.getInstance().setRunFlag("No");
			 System.exit(0);
		}
	}
	

	
	public void run(){
		try{
			 while(runFlag){
				 boolean svcFlag=AppContext.getInstance().runable();
				 if(!svcFlag){
					  Logger.getInstance().log(AppContext.ServiceNetworkManager, "App.run()网络服务未开启，不启动服务，程序自动退出");
					  AppService.getInstance().setRunable(false);
					  AppService.getInstance().exit();
					  runFlag=false;
					 break;
				 }else{
					 if(!AppService.getInstance().getRunable()){
						 AppService.getInstance().setRunable(true);
						 AppService.getInstance().start();
					 }
					 Thread.sleep(5*1000);
				 }
			 }
		}catch(Exception e){
			Logger.getInstance().logError("App.run()异常,服务退出需要手工重启:"+e.toString());
		}
	}
	
	public static App getInstance(){
		if(uniqueInstance==null)
			uniqueInstance=new App();
		return uniqueInstance;
	}
	
	private static App uniqueInstance;
	private boolean runFlag=true;
	
	private App(){
		 	String appPath=this.getStartPath();
		    AppContext.getInstance().init(appPath);
	}
	
	private String getStartPath(){
		String path="";
		try {
			path= System.getProperty("user.dir");//这种不支持中文路径吧
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  path;
	}
}
