package assignment.server.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import assignment.exchange.dao.FileRecord;
import assignment.exchange.dao.IConstants;
import assignment.exchange.dao.Message;
import assignment.exchange.dao.ResponseType;
import assignment.server.api.P2PServerService;

public class P2PServiceImpl implements P2PServerService{
	private Map<String, FileRecord> fileRecordMap;
	private static P2PServerService INSTANCE = new P2PServiceImpl();
	private P2PServiceImpl() {
		this.fileRecordMap = new HashMap<String, FileRecord>();
	}
	public static P2PServerService getInstance(){
		return INSTANCE;
	}
	@Override
	public String registerFile(Message message) {
		try{
			String fileName = message.getMessage();
			// host:port,server or host:port,client
			String clientListeningPort = message.getFrom();
			String[] hostPort = clientListeningPort.split(":");
			String host = hostPort[0];
			String[] portFrom = hostPort[1].split(",");
			int port = Integer.parseInt(portFrom[0]);
			String clientOrServer = portFrom[1];
			FileRecord fr = fileRecordMap.get(fileName);
			if(fr == null){
				fr = new FileRecord(fileName);
				fileRecordMap.put(fr.getFileName(), fr);
			}
			String clientHostPort = host+":"+port;
			fr.addNewHostPort(clientHostPort);
			return "File - " + /*fileName*/ message.getMessage() + " is registered successfully!!";
		}catch(Exception e){
			System.err.println("Exception while registering new file - " + e);
			
			return "Failure";
		}
	}

	@Override
	public Message queryFile(Message message) {
		String fileName = message.getMessage();
		FileRecord fr = fileRecordMap.get(fileName);
		Message msg = null;
		if(fr != null){
			msg = new Message(IConstants.SERVER_IP_PORT, ResponseType.SERVER_RESPONSE.name(), fr.getHostPorts());
		}else{
			msg = new Message(IConstants.SERVER_IP_PORT, ResponseType.SERVER_RESPONSE.name(), IConstants.NOT_FOUND);
		}
		return msg;
	}

	@Override
	public String deregisterClient(Message message) {
		String from = message.getFrom();
		String[] hostPortFrom = from.split(",");
		String hostPort = hostPortFrom[0];
		String fromType = hostPortFrom[1];
		if(!"client".equals(fromType)){
			System.err.println("Only clients can be deregistered!!" + "\r\n");
			throw new RuntimeException("Only clients can be deregistered!!");
		}
		Set<String> setOfFileNamesToBeRemoved = new HashSet<String>();
		for(String fileName : this.fileRecordMap.keySet()){
			FileRecord fr = this.fileRecordMap.get(fileName);
			if(fr.removeHostPort(hostPort)){
				setOfFileNamesToBeRemoved.add(fr.getFileName());
			}
		}
		
		for(String fileName : setOfFileNamesToBeRemoved){
			this.fileRecordMap.remove(fileName);
		}
		return "Client - " + hostPort + " is deregistered successfully!!";
	}

}
