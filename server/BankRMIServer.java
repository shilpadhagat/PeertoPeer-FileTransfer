package assignment.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import assignment.exchange.dao.Message;
import assignment.server.api.P2PServer;
import assignment.server.impl.P2PServiceImpl;

public class BankRMIServer extends UnicastRemoteObject implements P2PServer{
	private String name;
	private static final long serialVersionUID = 1L;

	public BankRMIServer(String name) throws RemoteException {
		this.name = name;
	}

	@Override
	public String registerFile(Message message) throws RemoteException{
		System.out.println("server side RMI invocation of registerFile Message --> " + message);
		return P2PServiceImpl.getInstance().registerFile(message);
	}

	@Override
	public Message queryFile(Message message) throws RemoteException{
		System.out.println("server side RMI invocation of queryFile Message --> " + message);
		return P2PServiceImpl.getInstance().queryFile(message);
	}

	@Override
	public String deregisterClient(Message message) throws RemoteException{
		System.out.println("server side RMI invocation of deregisterClient Message --> " + message);
		return P2PServiceImpl.getInstance().deregisterClient(message);
	}

}
