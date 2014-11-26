package groomiac.crocodesktop;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
	
	public static String readFile(String file){
		FileInputStream fis = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 

		try {
			fis = new FileInputStream(new File(file));
			
			byte[] thearray = new byte[1024 * 16];
			int b = 0;
			
			while (true){
				try {
					b = fis.read(thearray);
					if (b>=0){
						baos.write(thearray, 0, b);
					}
					else{
						break;
					}
				} catch (Exception e) {
					break;
				}			
			}

		} catch (IOException e) {
			return null;
		} finally{
			try {
				if(fis!=null) fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return new String(baos.toString().trim()).replace("\r", "");
	}
	
	//TODO: efficency? - fixed char buffer instead? - also for invalidation of strings...less string parts in-mem!
	public static String readFile2(String file){
		BufferedReader r = null;
		StringBuilder sb = new StringBuilder((int) new File(file).length());
		
		try {
			r = new BufferedReader(new FileReader(file));

			String buff = r.readLine();
			while(buff != null){
				sb.append(buff);
				sb.append('\n');
				buff = r.readLine();
			}
		} catch (IOException e) {
			return null;
		} finally{
			try {
				if(r!=null) r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString().trim();
	}
	
	public static String readFile(InputStream in){
		return readFile(in, null);
	}

	public static String readFile(InputStream in, String encoding){
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 

		try {
			byte[] thearray = new byte[1024 * 4];
			int b = 0;
			
			while (true){
				try {
					b = in.read(thearray);
					if (b>=0){
						baos.write(thearray, 0, b);
					}
					else{
						break;
					}
				} catch (Exception e) {
					break;
				}			
			}

		} catch (Exception e) {
			return null;
		} finally{
			try {
				if(in != null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(encoding == null)
			return new String(baos.toString().trim()).replace("\r", "");
		else
			try {
				return new String(baos.toByteArray(), encoding).trim().replace("\r", "");
			} catch (Exception e) {
				return null;
			}
	}
	
	public static void writeFile(String string, OutputStream fos){
		try {
			fos.write(string.getBytes("UTF-8"));
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public static boolean copyFile(String src, String dest) {
		return copyFile(new File(src), new File(dest));
	}

	public static boolean copyFile(File src, File dest) {
		if(src == null || dest == null || !src.exists() || !src.isFile() || (dest.exists() && dest.isDirectory())) return false;
		
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(src);
			fos = new FileOutputStream(dest);
			byte buffer[] = new byte[1024 * 4];
			int bytes;
			while (true) {
				bytes = fis.read(buffer);
				if (bytes <= -1) break;
				fos.write(buffer, 0, bytes);
			}
		} catch (Exception e) {
			System.err.println("error reading or writing: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (Exception e) {}
			if (fos != null)
				try {
					fos.flush();
					fos.close();
				} catch (Exception e) {}
		}
		
		if(dest.exists() && dest.isFile()) return true;
		return false;
	}
	
}
