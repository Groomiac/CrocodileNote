package groomiac.crocodesktop;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Popups extends Application {
	public static abstract class ReturnListener{
		public abstract void retrieve(int ret);
	}
	
	private static final Image warn = new Image("groomiac/crocodesktop/warn.png");
	private static final Image ico = new Image(Popups.class.getResourceAsStream("CrocodileNote-32.png"));
	

	private Stage myDialog;
    @Override
    public void start(Stage primaryStage) {
    }

    private ReturnListener rl;
    public void setReturnListener(ReturnListener rl){
    	this.rl = rl;
    }
    
    public void create(String msg, ReturnListener rl, int focusid, String... buttons){
    	setReturnListener(rl);
    	
        myDialog = new Stage();
        myDialog.initModality(Modality.APPLICATION_MODAL);
        myDialog.setResizable(false);
        myDialog.setTitle(/*Base.appname*/ "Warning");
        myDialog.getIcons().add(ico);

        ImageView iv = new ImageView(warn);
        
        Button[] bs = createButtons(buttons);
        
        HBox hb = HBoxBuilder.create().children(iv, t(msg))
        		.spacing(10)
        		.padding(new Insets(10))
        		.build();
        hb.setAlignment(Pos.TOP_LEFT);
        
        TilePane tileButtons = new TilePane(Orientation.HORIZONTAL);
        tileButtons.setPadding(new Insets(0, 0, 5, 0));
        tileButtons.setHgap(20);
        tileButtons.setAlignment(Pos.CENTER);
        tileButtons.getChildren().addAll(bs);
        tileButtons.setPrefColumns(bs.length);
        
        VBox vb = VBoxBuilder.create().children(hb, tileButtons).build();

        myDialog.setScene(new Scene(vb, Color.WHITESMOKE));
        myDialog.sizeToScene();
        myDialog.show();
        
        if(focusid > 0 && focusid < bs.length)
        	bs[focusid].requestFocus();
    }
    
    @SuppressWarnings("rawtypes")
	private EventHandler eve;
    {
        eve = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent ae) {
            	myDialog.close();
                if(rl != null) rl.retrieve(Integer.parseInt(((Button)ae.getSource()).getId()));
            }
        };
    }
    
    @SuppressWarnings("unchecked")
	private Button[] createButtons(String... s){
    	Button[] ret = new Button[s.length];
    	
    	for(int i=0; i<s.length; i++){
    		ret[i] = new Button(s[i]);
    		ret[i].setId("" + i);
    		ret[i].setOnAction(eve);
    		ret[i].setFont(Font.font("Helvetica", FontWeight.NORMAL, 13));
    		ret[i].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    	}
    	
    	return ret;
    }
    
    private Text t(String s){
    	Text t = new Text(s);
    	
    	t.setFill(Color.BLACK);
    	t.setFontSmoothingType(FontSmoothingType.LCD);
    	t.setFont(Font.font("Helvetica", FontWeight.SEMI_BOLD, 14));
    	
    	return t;
    }
}
