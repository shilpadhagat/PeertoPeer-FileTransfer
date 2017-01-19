package assignment.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import assignment.exchange.dao.IConstants;

public class TestServer {
	public static void main(String[] args) throws Exception{
		ServerSocket mysocket = new ServerSocket(IConstants.SERVER_PORT);
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
			            String data1 = reader.readLine().trim();
			            
			            System.out.println(data1);
			            writer.write(data1 + "\r\n");
			            writer.flush();
			         }
			         
					}catch(Exception e){
						System.err.println("Exception - " + e);
					}finally{
						try{
							if(connectionSocket!=null){
								connectionSocket.close();
							}
						}catch(Exception e){
							System.err.println(e);
						}
					}
				}
			};
	         Thread t = new Thread(task);
	         t.start();
        }
	}
}
