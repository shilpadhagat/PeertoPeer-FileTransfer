package assignment.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import assignment.exchange.dao.Message;
import assignment.exchange.dao.RequestType;
import assignment.util.JsonUtils;

public class Test {
	public static void main(String[] args) throws Exception{
		Socket socketClient= new Socket("localhost", 5555/*61*/);
		System.out.println("Success");
		BufferedReader reader = 
	    		new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

	    BufferedWriter writer= 
        		new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
	    Message message = new Message(",client", RequestType.FILE_DOWNLOAD.name(), "fileName");
	    writer.write(JsonUtils.toJson(message) + "\r\n");
	    writer.flush();
	    String response = reader.readLine();
	    System.out.println(response);
	}
}
