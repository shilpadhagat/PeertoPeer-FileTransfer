package assignment.server.api;

import assignment.exchange.dao.Message;

public interface P2PServerService {
	public String registerFile(Message message);
	public Message queryFile(Message message);
	public String deregisterClient(Message message);
}
