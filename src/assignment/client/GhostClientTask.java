package assignment.client;

public class GhostClientTask implements Runnable {
	private String ipAddress;
	private int port;
	private String homeFolder;
	
	public GhostClientTask(String ipAddress, int port, String homeFolder/*, String fileNames*/) {
		this.ipAddress = ipAddress;
		this.port = port;
		this.homeFolder = homeFolder;
	}
	@Override
	public void run() {
		new P2PGhostImpl("localhost", this.port, this.homeFolder).connect();
	}

}
