package groomiac.crocodesktop;

import groomiac.encryptor.PBKDF2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64;

public class Base {
	final static String aes = "AES";
	final static String ecb = "AES/ECB/NoPadding";
	final static String cbc = "AES/CBC/PKCS5Padding";
	final static String hmac = "HmacSHA256";
	
	final static String file_secret = "secret.conf";
	
	final static String appname = "CrocodileNote";
	
	final static String t_cancel = "Cancel";
	final static String t_ok = "Ok";
	final static String t_okay = "Okay";
	final static String t_yes = "Yes";
	final static String t_no = "No";
	final static String t_return = "Return";

	protected static byte[] tmp_esk;
	protected static Cipher kcipher;
	protected static Mac ivMac;
	
	static void deinit(){
		if(Log.DEBUG) System.out.println("DEINIT");
		
		secretpropFile = null;
		keyprops = null;
		keyprops = new Properties();
	}
	
	static void logout(){
		if(Log.DEBUG) System.out.println("LOGOUT");
		
		if(tmp_esk != null) new Random(System.nanoTime()).nextBytes(tmp_esk);
		try {
			if(kcipher != null)
				kcipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(tmp_esk, aes));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	static void loadSecrets() {
		if(secretpropFile.exists() && secretpropFile.isFile()){
			try {
				FileInputStream fis = new FileInputStream(secretpropFile);
				keyprops.load(fis);
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//Crypto
	
	static boolean loaded(){
		return tmp_esk != null;
	}
	
	static boolean loadpw(String pw){
		if(loaded()) return true;
		
		try {
			//Check key material
			byte[] tmp_ce, tmp_cd, tmp_ke, s;
			int it = getSecretInt(_P.i);
			tmp_ce = getSecretBytes(_P.ce);
			tmp_cd = getSecretBytes(_P.cd);
			tmp_ke = getSecretBytes(_P.ke);
			s = getSecretBytes(_P.s);

			ivMac = Mac.getInstance(hmac);
			ivMac.init(new SecretKeySpec(s, aes));
			
			Mac mac = Mac.getInstance(hmac);
			PBKDF2 ff = new PBKDF2(mac);
			
			byte[] pwkey = ff.generateDerivedParameters(256, pw.getBytes(), s, it);

			SecretKeySpec seckey = new SecretKeySpec(pwkey, aes);
			Cipher cipher = Cipher.getInstance(ecb);
			cipher.init(Cipher.DECRYPT_MODE, seckey);
			
			tmp_ke = cipher.doFinal(tmp_ke);
			seckey = new SecretKeySpec(tmp_ke, aes);
			cipher.init(Cipher.DECRYPT_MODE, seckey);
			
			tmp_ce = cipher.doFinal(tmp_ce);

			if(Arrays.equals(tmp_cd, tmp_ce)){
				KeyGenerator key_gen = KeyGenerator.getInstance(aes);
				key_gen.init(256);
				long max = System.currentTimeMillis() % 11;
				max++;
				for(int i=0; i < max; i++)
					key_gen.generateKey();
				SecretKey new_key = key_gen.generateKey();
				
				byte[] tmp_k = new_key.getEncoded();
				
				seckey = new SecretKeySpec(tmp_k, aes);
				cipher.init(Cipher.ENCRYPT_MODE, seckey);
				tmp_esk = cipher.doFinal(tmp_ke);
				
				
				kcipher = Cipher.getInstance(ecb);
				kcipher.init(Cipher.DECRYPT_MODE, seckey);
				
				return true;
			}
			else{
				JOptionPane.showOptionDialog(null,
						"The password is wrong.", Base.appname,
						JOptionPane.PLAIN_MESSAGE, JOptionPane.WARNING_MESSAGE, null, new Object[]{"      OK      "}, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	//Properties
	static Properties keyprops = new Properties();
	static File secretpropFile;


	//Properties for secrets
	static void saveSecret(_P key, String val){
		saveSecret(key.name(), val);
	}

	static void saveSecret(_P key, byte[] val){
		saveSecret(key.name(), Base64.encodeBase64URLSafeString(val));
	}

	static void saveSecret(_P key, int val){
		saveSecret(key.name(), val + "");
	}

	static void saveSecret(String key, String val){
		try {
			keyprops.setProperty(key, val);
			
			FileOutputStream fos = new FileOutputStream(secretpropFile);
			keyprops.store(fos, appname);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	static String getSecret(_P key){
		return keyprops.getProperty(key.name(), null);
	}
	
	static int getSecretInt(_P key){
		try {
			return Integer.parseInt(keyprops.getProperty(key.name(), null));
		} catch (Exception e) {
			return -1;
		}
	}
	
	static byte[] getSecretBytes(_P key){
		return Base64.decodeBase64(keyprops.getProperty(key.name(), null));
	}
	
	static void initSecretProps(File path){
		secretpropFile = new File(path, file_secret);
	}
	
	
	private static String mainfolder = null;
	private static File folderfile = null;

	static final File getFolderfile() {
		return folderfile;
	}

	static final String getFolder() {
		return mainfolder;
	}

	static final void setFolder(String folder) {
		if (mainfolder == null) {
			mainfolder = folder;
			folderfile = new File(folder);
		}
	}

	static final void setFolder(File folder) {
		if (mainfolder == null) {
			mainfolder = folder.getAbsolutePath();
			folderfile = folder;
		}
	}

	static final File getNewfile(String filename) {
		return new File(folderfile, filename);
	}
	
	static final File getArchFolder(String folder){
		return new File(folder, appname + "_Export");
	}
	
	static final File getArchFolder(File folder){
		return new File(folder, appname + "_Export");
	}
	

	static void dialog_createpw(final StringResult srX){
		PWTriggerCreate.main(new StringResult() {
			
			@Override
			void receive(String ret) {
				
				try {
					KeyGenerator key_gen = KeyGenerator.getInstance(aes);
					key_gen.init(256);
					long max = System.currentTimeMillis() % 11;
					max++;
					
					//Don't know if it is meaningful, but sure it does not hurt!
					for(int i=0; i < max; i++)
						key_gen.generateKey();
					SecretKey new_key = key_gen.generateKey();
					
					int its = new Random(System.currentTimeMillis()).nextInt(1000) + 6000;
					
					byte[] s = new_key.getEncoded();
					
					for(int i=0; i < max; i++)
						key_gen.generateKey();
					new_key = key_gen.generateKey();
					
					byte[] k = new_key.getEncoded();
					
					for(int i=0; i < max; i++)
						key_gen.generateKey();
					new_key = key_gen.generateKey();
					
					byte[] c_dec = new_key.getEncoded();
					
					Mac mac = Mac.getInstance(hmac);
					PBKDF2 ff = new PBKDF2(mac);
					
					byte[] pwkey = ff.generateDerivedParameters(256, ret.getBytes(), s, its);
					
					SecretKeySpec seckey = new SecretKeySpec(pwkey, aes);
					Cipher cipher = Cipher.getInstance(ecb);
					cipher.init(Cipher.ENCRYPT_MODE, seckey);
					
					saveSecret(_P.ke, Base64.encodeBase64URLSafeString(cipher.doFinal(k)));
					
					seckey = new SecretKeySpec(k, aes);
					cipher.init(Cipher.ENCRYPT_MODE, seckey);

					saveSecret(_P.ce, Base64.encodeBase64URLSafeString(cipher.doFinal(c_dec)));

					saveSecret(_P.cd, Base64.encodeBase64URLSafeString(c_dec));
					saveSecret(_P.s, Base64.encodeBase64URLSafeString(s));
					saveSecret(_P.i, its);
					
					loadpw(ret);
					
					srX.receive(null);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
