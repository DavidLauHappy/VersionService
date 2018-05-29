package Service;

public class VFile {
	private String localPath;
	private String remotePath;
	private String md5;
	private long size;
	private long lastModified;
	
	public VFile(String localPath, String remotePath, long size,
			long lastModified) {
		super();
		this.localPath = localPath;
		this.remotePath = remotePath;
		this.size = size;
		this.lastModified = lastModified;
		this.md5=AppUtils.getMd5ByPath(this.localPath);
	}
	public String getLocalPath() {
		return localPath;
	}
	public String getRemotePath() {
		return remotePath;
	}
	public String getMd5() {
		return md5;
	}
	public long getSize() {
		return size;
	}
	public long getLastModified() {
		return lastModified;
	}
}
