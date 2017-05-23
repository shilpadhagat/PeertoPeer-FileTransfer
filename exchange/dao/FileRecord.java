package assignment.exchange.dao;

import java.util.HashSet;
import java.util.Set;

public class FileRecord {

	private String fileName;
	
	private String filePath;
	private Set<String> hostPortSet;
	public FileRecord(String fileName) {
		this.fileName = fileName;
		this.hostPortSet = new HashSet<String>();
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void addNewHostPort(String hostPort){
		this.hostPortSet.add(hostPort);
	}
	
	public String getHostPorts(){
		StringBuilder sb = new StringBuilder();
		if(hostPortSet!=null){
			for(String str : hostPortSet){
				sb.append(str);
				sb.append(",");
			}
			sb.setLength(sb.length()-1);
		}
		return sb.toString();
	}
	
	public boolean removeHostPort(String hostPort){
		this.hostPortSet.remove(hostPort);
		return this.hostPortSet.isEmpty();
	}
	public void setFilePath(String filePath){
		this.filePath = filePath;
	}
	
	public String getFilePath(){
		return this.filePath;
	}
	@Override
	public String toString() {
		return "FileRecord [fileName=" + fileName + ", filePath=" + filePath + ", hostPortSet=" + hostPortSet + "]";
	}
}
