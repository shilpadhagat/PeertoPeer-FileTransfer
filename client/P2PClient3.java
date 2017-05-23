package assignment.client;

import java.rmi.RemoteException;

public class P2PClient3 {
	public static void main(String[] args) throws RemoteException{
		new P2PClientImpl("localhost", 5563, "/Users/shilpa/Desktop/ProjectFiles/Client3").connect();
	}
}
