package groomiac.crocodesktop;

import groomiac.crocodesktop.Ini.P;
import groomiac.crocodesktop.Popups.ReturnListener;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.UIManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import netscape.javascript.JSObject;

public class CrocodileNote extends Application{
	@SuppressWarnings({ "rawtypes", "unused" })
	private static Class THECLASS = CrocodileNote.class;

	private static final String main = "___Main___";
	
	private static ArrayList<FolderItem> l = new ArrayList<FolderItem>();

	static File folderfile;

	static long ctr = 0;
	
	private static String userfolder = null;
	private static final String ini = "CrocodileNote.ini";

	/*
	 * TODO: make sure UTF-8 support for all files and folders
	 * 
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		launch(args);
	}
	
	
	private void loadInitFiles(){
		userfolder = System.getProperty("user.home") + File.separator + ".crocodilenote";
		new File(userfolder).mkdirs();
		
		Base.secretpropFile = new File(userfolder, "secret.conf");
		
		if(!Base.secretpropFile.exists() || !Base.secretpropFile.isFile()){
			Base.secretpropFile.delete();
			Base.dialog_createpw(new StringResult() {
				
				@Override
				void receive(String ret) {
					loadMoreInitFiles();
					initMainSystems();
				}
			});
			
			return;
		}
		
		loadMoreInitFiles();
		
		PWTrigger.main(new StringResult() {
			
			@Override
			void receive(String ret) {
				if(!Base.loadpw(ret)){
					PWTrigger.main(this);
					
					return;
				}
				
				initMainSystems();
			}
		});

	}
	
	private void loadMoreInitFiles(){
		Base.loadSecrets();
		if(Log.DEBUG) System.out.println(Base.secretpropFile.getAbsolutePath());
		
		File propfile = new File(userfolder, ini);
		
		if(!propfile.exists()){
			try {
				propfile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Ini.load(propfile.getAbsolutePath());
		
		String rawpath = Ini.get(P.path);
		if(Log.DEBUG) System.out.println("RAW:" + rawpath);
		
		if(rawpath == null){
			rawpath = "data";
			Ini.set(P.path, rawpath);
			Ini.save(propfile);
		}
		
		if(rawpath.length() > 1 && (rawpath.charAt(1) == ':' || rawpath.charAt(0) == '\\' || rawpath.charAt(0) == '/')){
			folderfile = new File(rawpath);
		}
		else{
			folderfile = new File(userfolder, rawpath);
		}
		
		if(Log.DEBUG) System.out.println("LIVE:" + folderfile);
	}

	private Scene scene;
	private Stage stage;

	@Override
	public void start(Stage stagex) throws Exception {
		URL.setURLStreamHandlerFactory(new MyURLStreamHandlerFactory());

		this.stage = stagex;
		
		stage.getIcons().add(new Image(getClass().getResourceAsStream("CrocodileNote-32.png")));
		//stage.getIcons().add(new Image(getClass().getResourceAsStream("CrocodileNote-64.png")));
		
		loadInitFiles();
	}
	
	private void initMainSystems(){
		folderfile.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (!pathname.isDirectory())
					return false;

				l.add(new FolderItem(pathname.getName(), null));
				return true;
			}
		});

		if (l.size() == 0){
			l.add(FolderItem.createnew(main));
		}

		Collections.sort(l, new Comparator<FolderItem>() {

			@Override
			public int compare(FolderItem lfi, FolderItem rfi) {
				if (lfi == null || rfi == null)
					return 0;

				String lhs = lfi.getShow();
				String rhs = rfi.getShow();
				
				if (main.equals(lhs))
					return -1;
				if (lhs == rhs)
					return 0;
				if (lhs == null && rhs == null)
					return 0;
				if (lhs == null)
					return -1;
				if (rhs == null)
					return -1;

				lhs = lhs.toLowerCase();
				rhs = rhs.toLowerCase();

				if (lhs.startsWith("_") && lhs.startsWith("_"))
					return lhs.compareTo(rhs);

				if (lhs.startsWith("_"))
					return -1;
				if (rhs.startsWith("_"))
					return 1;

				return lhs.compareTo(rhs);
			}

		});
		
		if(Log.DEBUG) System.out.println(l);
		
		String tmp = l.get(0).getReal();
		FileItem.thefolder = new File(folderfile, tmp).getAbsolutePath();
		FileItem.foldername = tmp;
		
	    Platform.runLater(new Runnable() {
	        public void run() {     
	        	
	    		Platform.setImplicitExit(false);

	    		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	    		    @Override
	    		    public void handle(WindowEvent event) {
	    		        event.consume();

	    		        if(!"0".equals(b.executeScript("data_id.length").toString())){
	    		        	new Popups().create("There are unsaved elements. Do you really want to exit without saving?", new ReturnListener() {
								
								@Override
								public void retrieve(int ret) {
									if(ret == 0)
										doExit();
									
								}
							}, 1, "Discard changes", "Cancel");
	    		        }
	    		        else{
	    		        	doExit();
	    		        }
	    		    }
	    		});
	    		
	    		
	    		// create the scene
	    		stage.setTitle(Base.appname);
	    		Browser browse = new Browser();
	    		b = browse.getB();
	    		
	    		browse.dostuff();
	    		
	    		scene = new Scene(browse, 800, 800, Color.web("#666970"));
	    		stage.setMinHeight(650);
	    		stage.setMinWidth(650);
	    		
	    		stage.setScene(scene);
	    		stage.show();
	        }
	     });
	}
	
	private void doExit(){
		Base.logout();
		Base.deinit();
        
        stage.close();
        Platform.exit();
	}

	private WebEngine b;
	class Browser extends Region {
		final WebView browser = new WebView();
		final WebEngine webEngine = browser.getEngine();
		
		public Browser() {
			webEngine.load("myapp:///uibase.html");
			getChildren().add(browser);
		}
		
		public WebEngine getB(){
			return webEngine;
		}

		@Override
		protected void layoutChildren() {
			double w = getWidth();
			double h = getHeight();
			layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
		}

		@Override
		protected double computePrefWidth(double height) {
			return 750;
		}

		@Override
		protected double computePrefHeight(double width) {
			return 500;
		}
		
		void dostuff(){
	        // process page loading
	        webEngine.getLoadWorker().stateProperty().addListener(
	            new ChangeListener<State>() {
	                @Override
	                public void changed(ObservableValue<? extends State> ov,
	                    State oldState, State newState) {
	                    if (newState == State.SUCCEEDED) {
	                            JSObject win = (JSObject) webEngine.executeScript("window");
	                            win.setMember("app", new GrooApp());
	                            
	                            doInit();
	                            
	                            webEngine.executeScript("start();");
	                        }
	                    }
	                }
	        );
		}
	}

	public void doInit(){
		for(int i=l.size()-1; i>-1; i--){
			FolderItem f = l.get(i);
			b.executeScript("addString('list', '<p onclick=\"clickList(this)\" class=\"tabentryx\" id=\"" + f.getReal() +"\">" + f.getShow() + "</p>')");
		}
		
		b.executeScript("clickListFromID('" + l.get(0).getReal() + "')");
		
		if(Log.DEBUG) System.out.println("Init complete");
	}

    public class GrooApp {
        public void log(String s) {
            System.out.println(s);
        }
    	 
        public void log(Object o) {
            System.out.println(o.toString());
        }
    	 
        public void reallyreload(final String s){
        	new Popups().create("There are unsaved elements. Do you really want to discard all changes?", new ReturnListener() {
				
				@Override
				public void retrieve(int ret) {
					if(ret != 0) return;
					
					b.executeScript("clickListFromID('" + s + "', false, true)");
				}
			}, 0, "  Discard  ", "  Cancel  ");
        	
        }
        
        public void delokQ(final String s){
        	//TODO efficency: HashMap with IDs instead of loop?

        	int no = -1;
        	for(int i=0; i<l.size(); i++){
        		FolderItem fi = l.get(i);
        		if(fi.getReal().equals(s)){
        			no = i;
        			break;
        		}
        	}
        	
        	final int theno = no;
        	
        	new Popups().create("Do you really want to delete the complete folder \"" + l.get(no).getShow() + "\"?", new ReturnListener() {
				
				@Override
				public void retrieve(int ret) {
					if(ret != 0) return;
					
	            	l.remove(theno);
	            	new Remover(new File(folderfile, s)).removeDir();
	            	
	    			if (l.size() == 0){
	    				l.add(FolderItem.createnew(main));
						b.executeScript("addString('list', '<p onclick=\"clickList(this)\" class=\"tabentryx\" id=\"" + l.get(0).getReal() +"\">" + l.get(0).getShow() + "</p>')");
	    			}
	    			
					b.executeScript("delAction('" + s + "');");
				}
			}, 0, "  Delete  ", "  Cancel  ");
        }
        
        public void clickelem(String s){
        	if(Log.DEBUG) System.out.println("LOAD: " + s);
        	
			FileItem.thefolder = new File(folderfile, s).getAbsolutePath();
			FileItem.foldername = s;
			
			try {
				ctr = 0;
			      Path dir = FileSystems.getDefault().getPath(FileItem.thefolder);
			      DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
			      for (Path path: stream) {
			    	  if(!path.getFileName().toString().startsWith("data_") && !path.getFileName().toString().endsWith(".dat"))
			    		  continue;
			    	  ctr++;
			      }
			      stream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return;
        }

        
        public void newelem(){
        	NewTrigger.main(new StringResult() {
				
				@Override
				void receive(final String ret) {
					l.add(FolderItem.createnew(ret));
					
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							String id = l.get(l.size() - 1).getReal();
							
							b.executeScript("addString('list', '<p onclick=\"clickList(this)\" class=\"tabentryx\" id=\"" + id +"\">" + ret + "</p>')");
							b.executeScript("clickListFromID('" + id + "', true)");
						}
					});
				}
			});
        }

        //TODO efficency? - cache file content in HashMap instead of file access; maybe not all but certain window size?; (update cache on save?) 

		public String extGet(int i){
			try {
					String tmp = FileItem.loadEncFiles(i);

					if(tmp == null) tmp = "";
					return tmp;
				} catch (Exception e) {
					e.printStackTrace();
				}

				return "error I323";
		}

		public long getLen(){
				return ctr;
		}
		
		
		int savect;
		public void save(int i, String content){
			savect++;
			FileItem.storeEncFiles(i, content);
		}
		
		public void startSave(){
			savect = 0;
		}
		
		public void endSave(){
			if(savect > 0) b.executeScript("toast('Saved (" + savect + ")');");
			else b.executeScript("toast('Nothing to save');");
		}

    }

	public class MyURLStreamHandlerFactory implements URLStreamHandlerFactory {

		public URLStreamHandler createURLStreamHandler(String protocol) {
			if (protocol.equals("myapp")) {
				return new MyURLHandler();
			}
			return null;
		}

	}

	public class MyURLHandler extends URLStreamHandler {

		@Override
		protected URLConnection openConnection(URL url) throws IOException {
			return new MyURLConnection(url);
		}

	}

	public class MyURLConnection extends URLConnection {
		private String thefile;

		public MyURLConnection(URL u) {
			super(u);
			thefile = u.toString();
			
			thefile = thefile.substring("myapp:/".length(), thefile.length());
		}

		private byte[] data;

		@Override
		public void connect() throws IOException {
			if (connected) {
				return;
			}
			connected = true;
		}

		public String getHeaderField(String name) {
			if ("Content-Type".equalsIgnoreCase(name)) {
				return getContentType();
			} else if ("Content-Length".equalsIgnoreCase(name)) {
				return "" + getContentLength();
			}
			return null;
		}

		public String getContentType() {
			// TODO: general switch based on file-type

			if(thefile.endsWith(".html"))
				return "text/html";
			
			String fileName = getURL().getFile();
			String ext = fileName.substring(fileName.lastIndexOf('.'));
			return "image/" + ext;
		}

		public int getContentLength() {
			return data.length;
		}

		public long getContentLengthLong() {
			return data.length;
		}

		public boolean getDoInput() {
			return true;
		}

		public InputStream getInputStream() throws IOException {
			connect();
			return getClass().getResourceAsStream(thefile);
		}


		public OutputStream getOutputStream() throws IOException {
			return null;
		}

		public java.security.Permission getPermission() throws IOException {
			return null;
		}
	}
}
