package assignment.client;

import java.rmi.RemoteException;

public class P2PClient2 {
	public static void main(String[] args) throws RemoteException{
		new P2PClientImpl("localhost", 5562, "/Users/shilpa/Desktop/ProjectFiles/Client2").connect();
	}
}
