package tech.mrbcy.mrpc.demo.demo3.server;

public class ServerRunner {
	public static void main(String[] args) {
		try {
			new DemoRpcServer(9999).run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
