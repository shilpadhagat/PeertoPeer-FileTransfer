package assignment.client;

import java.io.File;
import java.util.Scanner;

public class GhostClientManager {
	private static void launchGhostClients(String ipAddress, Integer[] ports, int numberOfGhostClients, String homeFolder/*, String fileNames*/){
		for(int i=0;i<numberOfGhostClients;i++){
			Thread t = new Thread(new GhostClientTask(ipAddress, ports[i], homeFolder + File.separator + i/*, fileNames*/));
			t.start();
		}
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the number of ghost clients you want to launch!");
		int numberOfGhostClients = scanner.nextInt();
		
		while(numberOfGhostClients <= 0){
			System.out.println("Oops!! That's not a valid number. Enter the positive number of ghost clients you want to launch!");
			numberOfGhostClients = scanner.nextInt();
		}
		Integer[] ports = new Integer[numberOfGhostClients];
		for(int i=0;i<numberOfGhostClients;i++){
			System.out.println("Enter " + i + " client port number");
			ports[i] = scanner.nextInt();
		}
		
		System.out.println("Enter the external IP address of the box!!");
		String ipAddress = scanner.next();
		
		System.out.println("Enter home folder for ghost clients!!");
		String homeFolder = scanner.next();
		
		launchGhostClients(ipAddress, ports, numberOfGhostClients, homeFolder/*, fileNames*/);
	}
}
