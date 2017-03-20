
import java.io.Serializable;
import java.util.zip.CRC32;

public class Data implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private int len;
private int seqno;
private byte data[];
private long checksum;
public Data(int len,int seqno,byte data[],long checksum) {
	this.setData(data);
	this.setLen(len);
	this.setSeqno(seqno);
	this.setChecksum(checksum);
	
}

public int getLen() {
	return len;
}

public void setLen(int len) {
	this.len = len;
}

public int getSeqno() {
	return seqno;
}

public void setSeqno(int seqno) {
	this.seqno = seqno;
}

public byte[] getData() {
	return data;
}

public void setData(byte data[]) {
	this.data = data;
}

public long getChecksum() {
	return checksum;
}

public void setChecksum(long checksum) {
	this.checksum = checksum;
}

}