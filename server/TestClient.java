package assignment.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {
	public static void main(String[] args) throws Exception {
		Socket socketClient= new Socket("localhost",5555);
	    System.out.println("Client: "+"Connection Established");

	    BufferedReader reader = 
	    		new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

	    BufferedWriter writer= 
        		new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
	    while(true){
		    Scanner consoleReader = new Scanner(System.in);
		    writer.write(consoleReader.next()+"\r\n");
		    writer.flush();
		    System.out.println(reader.readLine());
	    }
	}
}
