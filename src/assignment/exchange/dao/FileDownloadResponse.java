package assignment.exchange.dao;

public class FileDownloadResponse {
	private String response;
	private byte[] data;
	public FileDownloadResponse(String response) {
		this.response = response;
	}
	public String getResponse() {
		return response;
	}
	
	public void setData(byte[] bytes){
		this.data = bytes;
	}
	
	public byte[] getData() {
		return this.data;
	}
	@Override
	public String toString() {
		return "FileDownloadResponse [response=" + response +"]";
	}
}
