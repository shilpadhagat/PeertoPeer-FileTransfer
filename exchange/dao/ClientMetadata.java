package assignment.exchange.dao;

import java.util.concurrent.atomic.AtomicInteger;

public class ClientMetadata implements Comparable<ClientMetadata>{
	private String hostPort;
	private AtomicInteger numberOfSuccessfulDownloads;
	public ClientMetadata(String hostPort) {
		this.hostPort = hostPort;
		this.numberOfSuccessfulDownloads = new AtomicInteger();
	}
	public String getHostPort() {
		return hostPort;
	}
	public AtomicInteger getNumberOfSuccessfulDownloads() {
		return this.numberOfSuccessfulDownloads;
	}
	public void incrementNumberOfSuccessfulDownloads(){
		this.numberOfSuccessfulDownloads.incrementAndGet();
	}
	
	@Override
	public int compareTo(ClientMetadata o) {
		final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;
	    if (this == o) return EQUAL;
	    
	    if(this.numberOfSuccessfulDownloads.get() > o.numberOfSuccessfulDownloads.get()) return BEFORE;
	    
	    if(this.numberOfSuccessfulDownloads.get() < o.numberOfSuccessfulDownloads.get()) return AFTER;
	    
	    if(this.hostPort.compareTo(o.hostPort) < 0) return BEFORE;
	    
	    if(this.hostPort.compareTo(o.hostPort) > 0) return AFTER;
	    
	    return EQUAL;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hostPort == null) ? 0 : hostPort.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientMetadata other = (ClientMetadata) obj;
		if (hostPort == null) {
			if (other.hostPort != null)
				return false;
		} else if (!hostPort.equals(other.hostPort))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ClientMetadata [hostPort=" + hostPort + ", numberOfSuccessfulDownloads=" + numberOfSuccessfulDownloads
				+ "]";
	}
}
