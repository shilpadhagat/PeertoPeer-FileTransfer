package assignment.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import assignment.exchange.dao.IConstants;
import assignment.exchange.dao.Message;
import assignment.exchange.dao.RequestType;
import assignment.server.impl.P2PServiceImpl;
import assignment.util.JsonUtils;
import assignment.util.StringUtils;


// Message Format - 
public class ServerImpl {

	public ServerImpl() throws Exception{
		startRMI();
		start();
	}
	
	private void startRMI(){
		String name = "1";
		try{
    		BankRMIServer bankRmiServer = new BankRMIServer(name);
    		Registry registry = LocateRegistry.createRegistry(12345);
    		registry.bind(name, bankRmiServer);
    	}catch(Exception e){
    		System.err.println(e);
    	}
	}
	
	private void start() throws Exception{
		 System.out.println("Server is starting!!" );
         ServerSocket mysocket = new ServerSocket(IConstants.SERVER_PORT);
         System.out.println("Server has started!!" );
         while(true){
	         Socket connectionSocket = mysocket.accept();
	         Runnable task = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try{
					BufferedReader reader =
			         		new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			         BufferedWriter writer= 
			         		new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
			         while(true)
			         {
			            String data1 = reader.readLine();
			            
			            if(StringUtils.isEmpty(data1)){
			            	continue;
			            }
			            
			            data1 = data1.trim();
			            System.out.println("Received message from socket --> " + data1);
			            
			            Message message = JsonUtils.fromJson(data1, Message.class);
			            
			            String ret = processInput(message, writer);
			            
			            if(ret!=null && ret.equals("break")){
			            	break;
			            }
			            
			            writer.flush();
			         }
			         
					}catch(Exception e){
						System.err.println("Exception - " + e);
					}finally{
						if(connectionSocket!=null){
							try{
							connectionSocket.close();
							}catch(Exception e){System.err.println(e);}
						}
					}
				}
			};
	         Thread t = new Thread(task);
	         t.start();
         }
	}
	
	private String processInput(Message input, BufferedWriter writer) throws IOException{
		// Types of requests a server may receive- 1. Register a new file from a client. 2. Query for an existing file. 3. Deregistration of a client.
		if(RequestType.FILE_REGISTER.name().equals(input.getType())){
			
			P2PServiceImpl.getInstance().registerFile(input);
			writer.write("File - " + /*fileName*/ input.getMessage() + " is registered successfully!!" + "\r\n");
		}else if(RequestType.QUERY.name().equals(input.getType())){
			
			Message msg = P2PServiceImpl.getInstance().queryFile(input);
			writer.write(JsonUtils.toJson(msg) + "\r\n");
		}else if(RequestType.CLIENT_DEREGISTER.name().equals(input.getType())){
			String from = input.getFrom();
			String[] hostPortFrom = from.split(",");
			String hostPort = hostPortFrom[0];
			
			P2PServiceImpl.getInstance().deregisterClient(input);
		
			writer.write("Client - " + hostPort + " is deregistered successfully!!" + "\r\n");
			return "break";
		}else{
			throw new RuntimeException("Invalid Input type is passed - " + input);
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception{
		new ServerImpl();
	}
}
