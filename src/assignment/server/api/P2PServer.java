package assignment.server.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

import assignment.exchange.dao.Message;

public interface P2PServer extends Remote{
	public String registerFile(Message message) throws RemoteException;
	public Message queryFile(Message message) throws RemoteException;
	public String deregisterClient(Message message) throws RemoteException;
}
