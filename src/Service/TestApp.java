package Service;

public class TestApp {
	public static void main(String[] args){
		 App app=App.getInstance();
		 AppContext.getInstance().setRunFlag("Yes");
		 app.run();
	}
}
