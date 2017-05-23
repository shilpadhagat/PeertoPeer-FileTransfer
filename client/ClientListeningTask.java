package assignment.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import assignment.exchange.dao.FileDownloadResponse;
import assignment.exchange.dao.FileRecord;
import assignment.exchange.dao.IConstants;
import assignment.exchange.dao.Message;
import assignment.exchange.dao.RequestType;
import assignment.exchange.dao.ResponseType;
import assignment.util.FileUtils;
import assignment.util.JsonUtils;

public class ClientListeningTask implements Runnable {
	
	private String clientHostPort;
	private Map<String, FileRecord> localClientFileRecordMap;
	private volatile boolean isStopped;
	private AtomicInteger currentNumberOfDownloadRequests;
	public ClientListeningTask(String clientHostPort, Map<String, FileRecord> localClientFileRecordMap) {
		this.clientHostPort = clientHostPort;
		this.localClientFileRecordMap = localClientFileRecordMap;
		this.isStopped = false;
		this.currentNumberOfDownloadRequests = new AtomicInteger();
	}
	
	public void stop(Writer writer) throws InterruptedException, IOException{
		this.isStopped = true;
		Thread.sleep(1000);
		writer.write("stop\r\n");
		writer.flush();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			System.out.println("Client is Starting!!" );
			ServerSocket mysocket = new ServerSocket(Integer.parseInt(clientHostPort.split(":")[1]));
			while(!this.isStopped){
				try{
					
					Socket connectionSocket = mysocket.accept();
					AtomicInteger currentDownloadReq = this.currentNumberOfDownloadRequests;
					currentDownloadReq.incrementAndGet();
					Runnable task = new Runnable() {
						
						@Override
						public void run() {
						
							try{
								BufferedReader reader =
						           		new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
						        BufferedWriter writer= 
						           		new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
						       
						        	try{
							           String data1 = reader.readLine().trim();
							           
							           if("stop".equals(data1)){
							        	   Integer downloadCount = -1;
							        	   while((downloadCount = currentDownloadReq.get()) > 1){
							        		   System.out.println("Waiting for download requests to get over. Current count - " + downloadCount);
							        		   Thread.sleep(10);
							        	   }
							        	   System.out.println("All pending downloads are over now");
							        	   setStopped();
							        	   return;
							           }
							           
							           Message message = JsonUtils.fromJson(data1, Message.class);
							           
							           process(message, writer);
							       
							           writer.flush();
						        	}catch(Exception e){
						        		System.err.println("Exception - " + e);
						        	}
						        connectionSocket.close();
							}catch(Exception e){
								System.err.println("Exception - " + e);
							}finally {
								currentDownloadReq.decrementAndGet();
							}
						}
					};
					
					Thread t = new Thread(task);
					t.start();
				}catch(Exception e){
					System.err.println("Exception while running client listening socket thread - " + e);
				}
			}
		}catch(Exception e){
			System.err.println("Exception - " + e);
		}
		try{
			System.out.println("Stop received - Waiting for 20 sec for other threads to finish.");
			Thread.sleep(20000);
		}catch(Exception e){
			System.err.println("Exception - " + e);
		}
		System.out.println("Client - " + this.clientHostPort + " has exited!!");
	}
	
	private final void setStopped(){
		this.isStopped = true;
		System.out.println("After setStopped value is " + this.isStopped);
	}
	
	private void process(Message input, BufferedWriter writer) throws IOException{
		// Types of requests a server may receive- 1. Register a new file from a client. 2. Query for an existing file. 3. Deregistration of a client.
		if(RequestType.FILE_DOWNLOAD.name().equals(input.getType())){
			String fileName = input.getMessage();
			String from = input.getFrom();
			if(!IConstants.FROM_CLIENT.equals(from.split(",")[1])){
				System.err.println("Client can process request only from client side.");
				throw new RuntimeException("Client can process request only from client side.");
			}
			
			FileRecord fr = this.localClientFileRecordMap.get(fileName);
			FileDownloadResponse response = null;
			if(fr == null){
				response = new FileDownloadResponse(IConstants.NOT_FOUND);
			}else{
				response = new FileDownloadResponse(IConstants.FOUND);
				byte[] bytes = FileUtils.readFileBytes(fr.getFilePath());
				response.setData(bytes);
			}
			
			writer.write(JsonUtils.toJson(response)+ "\r\n");
		}else{
			throw new RuntimeException("Invalid Input type is passed - " + input);
		}
	}

}
