package groomiac.crocodesktop;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;


public class Ini {
	enum P{
		path
	}
	
	private static HashMap<String, String> hm = new HashMap<>();
	

	public static void load(File file){
		load(file.getAbsolutePath());
	}

	public static void load(String file){
		String raw = Utils.readFile(file).trim().replace("\r", "");
		String[] props = raw.split("\n");
		for(String s: props){
			if(s == null || s.length() == 0 || s.indexOf("=") < 0) continue;
			String[] pair = s.split("=");
			if(pair == null || pair.length != 2) continue;
			hm.put(pair[0].trim(), pair[1].trim());
		}
	}
	
	public static String get(String key){
		return hm.get(key);
	}
	
	public static void set(String key, String val){
		hm.put(key, val);
	}
	
	public static String get(P key){
		return hm.get(key.name());
	}
	
	public static void set(P key, String val){
		hm.put(key.name(), val);
	}

	public static void save(File file){
		save(file.getAbsolutePath());
	}

	public static void save(String file){
		StringBuilder sb = new StringBuilder();
		Iterator<String> keys = hm.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			sb.append(key);
			sb.append('=');
			sb.append(hm.get(key));
			sb.append('\r');
			sb.append('\n');
		}
		
		try {
			Utils.writeFile(sb.toString(), new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
