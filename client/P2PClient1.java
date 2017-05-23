package assignment.client;

import java.rmi.RemoteException;

public class P2PClient1 {
	public static void main(String[] args) throws RemoteException{
		new P2PClientImpl("localhost", 5561, "/Users/shilpa/Desktop/ProjectFiles/Client1").connect();
	}
}
