package assignment.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import assignment.exchange.dao.ClientMetadata;
import assignment.exchange.dao.FileDownloadResponse;
import assignment.exchange.dao.FileRecord;
import assignment.exchange.dao.IConstants;
import assignment.exchange.dao.Message;
import assignment.exchange.dao.RequestType;
import assignment.server.api.P2PServer;
import assignment.util.FileUtils;
import assignment.util.JsonUtils;
import assignment.util.StringUtils;

public class P2PClientImpl {
	private String clientListeningHostPort;
	private Map<String, FileRecord> localClientFileRecordMap;
	private boolean isStopped;
	private String homeDir;
	private Map<String, ClientMetadata> clientHostPortVsSuccessfulDownloads;
	private P2PServer serverService;
	private boolean isRMI;
	public P2PClientImpl(String host, int listeningPort, String homeDir) throws RemoteException{
		this.clientListeningHostPort = host + ":" + listeningPort;
		this.localClientFileRecordMap = new HashMap<String, FileRecord>();
		this.isStopped = false;
		this.homeDir = homeDir;
		this.clientHostPortVsSuccessfulDownloads = new HashMap<String, ClientMetadata>();
		getRemoteInstance();
	}
	
	private void getRemoteInstance(){
		Registry registry;
		try{
			registry = LocateRegistry.getRegistry("localhost", 12345);
			this.serverService = (P2PServer) registry.lookup("1");
			System.out.println("Successfully acquired remote object.");
		}catch(Exception e){
			System.err.println(e);
		}
	}
	
	public void connect(){
		ClientListeningTask clientListeningTask = new ClientListeningTask(clientListeningHostPort, this.localClientFileRecordMap);
		Thread clientListeningThread = new Thread(clientListeningTask);
		
		clientListeningThread.start();
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try{
		    Socket socketClient= new Socket(IConstants.SERVER_HOST/*"localhost"*/,/*5555*/IConstants.SERVER_PORT);
		    System.out.println("Client: "+"Connection Established");
 
		    reader = 
		    		new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
 
		    writer= 
	        		new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
		    String serverMsg;
		  
			Scanner consoleReader = new Scanner(System.in);  // Reading from System.in
			
			System.out.println("Welcome to P2P client server. I am listening on - " + this.clientListeningHostPort);
			System.out.println("To choose RMI press R else press any key to continue. Default is Socket based.");
			String rMI = consoleReader.next();
			if("R".equalsIgnoreCase(rMI)){
				this.isRMI = true;
			}
			while(!this.isStopped){
				System.out.println("To register a new file with the server press 1 and enter.\nTo query for a globally unique file press 2 and enter.\nTo deregister/disconnect from the server press 3 and enter.");
				
				int choice = consoleReader.nextInt();
				
				System.out.println("You have entered - " + choice);
				
				if(choice == 1){
					System.out.println("Enter globally unique file name.");
					String fileName = consoleReader.next();
					System.out.println("Enter the file path.");
					String filePath = consoleReader.next();
					registerNewFile(fileName, filePath, writer, reader);
				}else if (choice == 2){
					System.out.println("Enter globally unique file name.");
					String fileName = consoleReader.next();
					Message message = new Message(this.clientListeningHostPort, RequestType.QUERY.name(), fileName);
					
					Message responseMessage = null;
					if(this.isRMI){
						responseMessage = this.serverService.queryFile(message);
					}else{
						String serverResponse = null;
						writer.write(JsonUtils.toJson(message) + "\r\n");
						writer.flush();
						Thread.sleep(1000);
						serverResponse=reader.readLine();
						if(serverResponse!=null){
							responseMessage = JsonUtils.fromJson(serverResponse, Message.class);
						}
					}
					
					if(responseMessage!=null){
						String listOfHostPortClients = responseMessage.getMessage();
						if(IConstants.NOT_FOUND.equals(listOfHostPortClients)){
							System.err.println("File - " + fileName + " is not found on the server");
						}
						else{
							Set<ClientMetadata> setOfHostPorts = new TreeSet<ClientMetadata>();
							if(!StringUtils.isEmpty(listOfHostPortClients)){
								for(String hostPort : listOfHostPortClients.split(",")){
									ClientMetadata clientMetaData = this.clientHostPortVsSuccessfulDownloads.get(hostPort);
									if(clientMetaData == null){
										clientMetaData = new ClientMetadata(hostPort);
										this.clientHostPortVsSuccessfulDownloads.put(hostPort, clientMetaData);
									}
									setOfHostPorts.add(clientMetaData);
								}
							}
							
							boolean isDownloaded = false;
							System.out.println("setOfHostPorts --> " + setOfHostPorts);
							for(ClientMetadata clientMetaData : setOfHostPorts){
								if(downloadFileFromOtherClient(clientMetaData.getHostPort(), fileName/*, locationToSaveFile*/)){
									
									ClientMetadata ai = this.clientHostPortVsSuccessfulDownloads.get(clientMetaData.getHostPort());
									ai.incrementNumberOfSuccessfulDownloads();
									System.out.println("Successful download is from client - " + clientMetaData);
									isDownloaded = true;
									break;
								}
							}
							if(!isDownloaded){
								System.err.println("File - " + fileName + " could not be downloaded. Please try again after sometime.");
							}
							//}
						}
					}
				}else if(choice == 3){
					// Deregister from server side.
					deregister(writer, reader);
					// finally
					String[] hostPortArr = clientListeningHostPort.split(":");
					Socket clientThreadSocket= new Socket(hostPortArr[0],Integer.parseInt(hostPortArr[1]));
					BufferedReader clientThreadReader = 
				    		new BufferedReader(new InputStreamReader(clientThreadSocket.getInputStream()));

				    BufferedWriter clientThreadWriter= 
			        		new BufferedWriter(new OutputStreamWriter(clientThreadSocket.getOutputStream()));
					clientListeningTask.stop(clientThreadWriter);
					this.isStopped = true;
				}else{
					throw new RuntimeException("Invalid choice - " + choice);
				}
			}
			System.out.println("Client - " + this.clientListeningHostPort + " is deregistered successfully. Exiting now!!");
			//}
			
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   finally{
		   try{
		   if(reader!=null){
			   reader.close();
		   }
		   }catch(Exception e){}
		   try{
		   if(writer!=null){
			   writer.close();
		   }
		   }catch(Exception e){}
	   }
	}
	
	private boolean downloadFileFromOtherClient(String hostPort, String fileName/*, String localLocationToSave/*, BufferedReader reader, BufferedWriter writer*/) {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try{
			System.out.println("Starting download file from other client. hostPort - " + hostPort + " and fileName - " + fileName);
			String[] hostPortArr = hostPort.split(":");
			Socket socketClient= new Socket(hostPortArr[0],Integer.parseInt(hostPortArr[1]));
			reader = 
		    		new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
	
		    writer= 
	        		new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
		    Message message = new Message(this.clientListeningHostPort+",client", RequestType.FILE_DOWNLOAD.name(), fileName);
		    writer.write(JsonUtils.toJson(message)+ "\r\n");
		    writer.flush();
		    String response = reader.readLine();
		    if(!StringUtils.isEmpty(response)){
		    	FileDownloadResponse fDresponse= JsonUtils.fromJson(response, FileDownloadResponse.class);
		    	if(IConstants.FOUND.equals(fDresponse.getResponse())){
		    		byte[] byteArr = fDresponse.getData();
		    		FileUtils.saveFileBytes(byteArr, this.homeDir + File.separator + fileName, true);
		    		return true;
		    	}else{
		    		System.err.println("File - " + fileName + " could not be found on the server");
		    	}
		    }
		}catch(Exception e){
			System.err.println("Download failed. Exception - " + e);
		}finally{
			try{
				if(reader!=null){
					reader.close();
				}
			}catch(Exception e){}
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(Exception e){}
		}
	    return false;
	}
	
	private void deregister(BufferedWriter writer, BufferedReader reader) throws IOException{
		Message message = new Message(clientListeningHostPort+",client", RequestType.CLIENT_DEREGISTER.name(), "NA");
		String responseFromServer = null;
		if(this.isRMI){
			responseFromServer = this.serverService.deregisterClient(message);
			if(!responseFromServer.equals("Client - " + this.clientListeningHostPort + " is deregistered successfully!!")){
				System.err.println("Could not deregister successfully!!");
				throw new RuntimeException("Could not deregister successfully!!");
			}
		}else{
			String json = JsonUtils.toJson(message);
			writer.write(json + "\r\n");
			writer.flush();
			while(true){
				if(!((responseFromServer=reader.readLine())!=null && responseFromServer.equals("Client - " + this.clientListeningHostPort + " is deregistered successfully!!"))){
					break;
				}
			}
		}
		
		
	}
	
	private void registerNewFile(String fileName, String filePath, BufferedWriter writer, BufferedReader reader) throws IOException{
		File file = new File(filePath);
		
		if(!file.exists()){

			System.err.println("File with filePath - " + filePath + " doesn't exist. We cannot send the register request to server.");
			return;
		}
		
		Message registerRequest = new Message(clientListeningHostPort+",client", RequestType.FILE_REGISTER.name(), fileName);
		String responseFromServer = null;
		if(this.isRMI){
			responseFromServer = this.serverService.registerFile(registerRequest);
		}else{
			String json = JsonUtils.toJson(registerRequest);
			writer.write(json + "\r\n");
			writer.flush();
			responseFromServer=reader.readLine();
			
		}
		if((responseFromServer)!=null && responseFromServer.equals("File - " + fileName + " is registered successfully!!")){
			FileRecord fr = new FileRecord(fileName);
			fr.addNewHostPort(this.clientListeningHostPort);
			fr.setFilePath(filePath);
			this.localClientFileRecordMap.put(fileName, fr);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new P2PClientImpl("localhost", 5561, "C:/Users/asax/AppData/Local/Temp").connect();
	}
}
