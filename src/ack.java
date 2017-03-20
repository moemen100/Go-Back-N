
import java.io.Serializable;
import java.util.zip.CRC32;

public class ack implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int len;
	private int ackno;
	private long checksum;

	public ack(int len,int ackno,long checksum) {
     this.setLen(len);
     this.setAckno(ackno);
 	this.setChecksum(checksum);

	}
	public int getAckno() {
		return ackno;
	}
	public void setAckno(int ackno) {
		this.ackno = ackno;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public long getChecksum() {
		return checksum;
	}

	public void setChecksum(long checksum) {
		this.checksum = checksum;
	}
}
