package filecompare;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 *
 * @author kim
 * 이 클래스는 파일의 hash값을 계산하고 저장하기 위한 클래스입니다.
 */
public class FileHashCaculator implements ICancelable {
	public File location;
	public byte[] md5;
	public byte[] sha1;
	public byte[] sha256;
	public byte[] sha512;
	public boolean canceled = false;
	public IProgressUpdate ipu = new IProgressUpdate() {
		public void updateProgress(int progress) {
		}
	};
	int currentBound = 0;

	public FileHashCaculator(File file, IProgressUpdate ipu) {
		if (ipu != null) {
			this.ipu = ipu;
		}
		this.location = file;
	}

	public void start() throws Exception {
		IProgressUpdate p = new IProgressUpdate() {
			@Override
			public void updateProgress(int progress) {
				int boundmin = FileHashCaculator.this.currentBound;
				int partial = (int) ((progress / 100.0f) * 25);
				FileHashCaculator.this.ipu.updateProgress(boundmin + partial);
			}
		};
		if (canceled)
			return;
		currentBound = 0;
		md5 = digest(location, "MD5", p);
		if (canceled)
			return;
		currentBound = 25;
		sha1 = digest(location, "SHA-1", p);
		if (canceled)
			return;
		currentBound = 50;
		sha256 = digest(location, "SHA-256", p);
		if (canceled)
			return;
		currentBound = 75;
		sha512 = digest(location, "SHA-512", p);
		if (canceled)
			return;
		currentBound = 100;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FileHashCaculator) {
			FileHashCaculator f = (FileHashCaculator) obj;
			boolean md5eql = Arrays.equals(md5, f.md5);
			boolean sha1eql = Arrays.equals(sha1, f.sha1);
			boolean sha256eql = Arrays.equals(sha256, f.sha256);
			boolean sha512eql = Arrays.equals(sha512, f.sha512);
			return md5eql && sha1eql && sha256eql && sha512eql;
		} else {
			return false;
		}
	}

	public byte[] digest(File file, String algorithm, IProgressUpdate updater)
			throws Exception {
		// allocate 10KB buffer
		byte[] buffer = new byte[10240];
		int len = 0;
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		long totalJob = file.length();
		long currentJob = 0;
		MessageDigest md = MessageDigest.getInstance(algorithm);
		while ((len = bis.read(buffer)) != -1) {
			if (canceled) {
				bis.close();
				return null;
			}
			md.update(buffer, 0, len);
			currentJob += len;
			updater.updateProgress((int) (((float) currentJob / (float) totalJob) * 100));
			// System.out.println("subprogress:"+((int)
			// (((float)currentJob/(float)totalJob)*100)));
		}
		bis.close();
		return md.digest();
	}

	@Override
	public String toString() {
		return "위치: "+location.getAbsolutePath()+"\nMD5: " + toHexString(md5) + "\nSHA-1: " + toHexString(sha1)
				+ "\nSHA-256: " + toHexString(sha256) + "\nSHA-512: "
				+ toHexString(sha512);
	}

	static final String HEXES = "0123456789ABCDEF";

	public static String toHexString(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	@Override
	public void cancel() {
		this.canceled = true;
	}
}
