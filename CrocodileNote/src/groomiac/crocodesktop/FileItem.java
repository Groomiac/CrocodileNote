package groomiac.crocodesktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileItem {
	static String thefolder = null;
	static String foldername = null;

	static byte[] genIV(String filename){
		String salt = foldername.toLowerCase() + "/" + filename;
		byte[] ret = new byte[16];
		System.arraycopy(Base.ivMac.doFinal(salt.getBytes()), 0, ret, 0, 16);
		return ret;
	}
	
	static String loadEncFiles(int i) {
		try {
			Cipher c = Cipher.getInstance(Base.cbc);
			IvParameterSpec ivSpec = new IvParameterSpec(genIV(filename(i)));
			c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base.kcipher.doFinal(Base.tmp_esk), Base.aes), ivSpec);
			
			CipherInputStream cis = new CipherInputStream(new FileInputStream(file(i)), c);
			return Utils.readFile(cis, "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
	
	static void storeEncFiles(int i, String s) {
		try {
			Cipher c = Cipher.getInstance(Base.cbc);
			IvParameterSpec ivSpec = new IvParameterSpec(genIV(filename(i)));
			c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base.kcipher.doFinal(Base.tmp_esk), Base.aes), ivSpec);
			
			CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(file(i)), c);
			Utils.writeFile(s, cos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private final static String filename(int i) {
		return "data_" + i + ".dat";
	}
	
	static File file(int i){
		return new File(getAbsoluteFolder(), filename(i));
	}

	protected static String getAbsoluteFolder(){
		return thefolder;
	}
	
}
