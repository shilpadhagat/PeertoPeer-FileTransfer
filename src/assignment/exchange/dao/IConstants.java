package assignment.exchange.dao;

public interface IConstants {
	public static final String FROM_CLIENT = "client";
	public static final String FROM_SERVER = "server";
	public static final String NOT_FOUND = "NOT_FOUND";
	public static final String FOUND = "FOUND";
	public static final int CLIENT_LISTENING_PORT = 5556;
	public static final String CLIENT_IP_PORT = "127.0.0.1:" + CLIENT_LISTENING_PORT;
	public static final String SERVER_HOST = "localhost";
	public static final int SERVER_PORT = 5555;
	public static final String SERVER_IP_PORT = "127.0.0.1:" + SERVER_PORT;
}
