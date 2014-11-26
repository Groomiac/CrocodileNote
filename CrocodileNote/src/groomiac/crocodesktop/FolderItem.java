package groomiac.crocodesktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FolderItem {
	private String showname, realname;
	
	public FolderItem(String path, String nickname){
		realname = path;
		showname = nickname;
	}
	
	public String getShow(){
			if(showname == null){
				showname = loadInfo(getNewfile(realname).getAbsolutePath(), realname);
			}

			if(showname == null) showname = "Unknown";
			
			return showname;
	}

	public String getReal(){
		return realname;
	}
	
	static final File getNewfile(String filename) {
		return new File(CrocodileNote.folderfile, filename);
	}
	
	static String loadInfo(String folder, String salt) {
		try {
			Cipher c = Cipher.getInstance(Base.cbc);
			IvParameterSpec ivSpec = new IvParameterSpec(genIV(salt));
			c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base.kcipher.doFinal(Base.tmp_esk), Base.aes), ivSpec);
			
			CipherInputStream cis = new CipherInputStream(new FileInputStream(new File(folder, ".info")), c);
			return Utils.readFile(cis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static byte[] genIV(String salt){
		byte[] ret = new byte[16];
		System.arraycopy(Base.ivMac.doFinal(salt.getBytes()), 0, ret, 0, 16);
		return ret;
	}

	static FolderItem createnew(String nickname){
			String path = UUID.randomUUID().toString();
			File tmp = getNewfile(path);
			while(tmp.exists()){
				path = UUID.randomUUID().toString();
				tmp = getNewfile(path);
			}
			
			tmp.mkdirs();
			
			storeInfo(getNewfile(path).getAbsolutePath(), path, nickname);
			
			return new FolderItem(path, nickname);
	}
	
	private static void storeInfo(String folder, String salt, String showname) {
		try {
			Cipher c = Cipher.getInstance(Base.cbc);
			IvParameterSpec ivSpec = new IvParameterSpec(genIV(salt));
			c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base.kcipher.doFinal(Base.tmp_esk), Base.aes), ivSpec);
			
			CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(new File(folder, ".info")), c);
			Utils.writeFile(showname, cos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return getShow();
	}

}
