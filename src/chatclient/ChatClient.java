
package chatclient;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;   
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
/**
 *
 * @author Scott PUrcell
 * cs 3250
 * Final test
 * multithreading socket client in javafx
 */
public class ChatClient extends Application {
   public String val;
   public String userName="Anonymous";
    String fileArg;
    String fileArg2;
    private BufferedReader in;
    private PrintWriter out;
    Socket socket;
    int port=8000;
    String serverAddress;
    TextArea textArea;

    @Override
    public void start(Stage primaryStage) throws Exception
    {     
        StackPane root = new StackPane();       
        HBox hbox = new HBox(30); // create a HBox to hold 2 vboxes        
        // create a vbox with a textarea that grows vertically
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-background-color: whitesmoke;");
        Label lbName = new Label("Name: ");
        Label lbName2 = new Label("Enter Text: ");
        TextField textField = new TextField();
        TextField nameField = new TextField();
        textField.setStyle("-fx-base:cadetblue;-fx-border-width: 2px;");
         nameField.setStyle("-fx-base:cadetblue;-fx-border-width: 2px;");
        textArea = new TextArea();
        textArea.setStyle("-fx-base:silver;-fx-background-color: powderblue;-fx-border-width: 2px;");
        textArea.setPrefWidth(450);
        VBox.setVgrow(textArea, Priority.ALWAYS);        
        vbox.getChildren().addAll(lbName,nameField, lbName2,textField, textArea);
        Button bt = new Button("Disconnect");
        bt.setStyle("-fx-base: powderblue;");
        vbox.getChildren().add(bt);
        hbox.setPadding(new Insets(20));
        hbox.getChildren().addAll(vbox);
        root.getChildren().add(hbox);
        Scene scene = new Scene(root, 500, 700); 
        primaryStage.setTitle("ChatClient");
        primaryStage.setScene(scene);
        primaryStage.show(); 
        
        //get the args fron the command line
          List <String> unnamedParameters=getParameters().getUnnamed();
       if(unnamedParameters.size()>0)
        {
            fileArg=unnamedParameters.get(0);
            userName=fileArg;
            fileArg2=unnamedParameters.get(1); 
            port= Integer.parseInt(fileArg2);
        }
        
        
        val= ("connect "+ userName);
        socket = new Socket("localhost", port);
        nameField.setText(userName);//fileArg
        
        //declare new threads
     
        new Thread(new WriteThread(socket)).start();//new thread
         new Thread(new RecieveThread(socket)).start();//new thread
         
         
       // Listen for TextField text changes
        textField.textProperty().addListener(new ChangeListener<String>() 
        {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) 
            {
                textField.setOnAction((event) -> {
                   val= textField.getText();
                   new Thread(new WriteThread(socket)).start();//new thread
                   
                  textArea.appendText("\nYou sent-- " + textField.getText());
                   textField.clear();
                 
                 
                });
                    
            }
        });
        // button action sends disconnect message to server
       bt.setOnAction(new EventHandler<ActionEvent>() 
       {
            @Override public void handle(ActionEvent e) 
            {
                out.println("disconnect " + userName);
                textArea.appendText("\nYou sent-- disconnect "+userName);
            } 
            
        });
    }
 
   //********************************* threads ***********************************************************
   
    //recieves from the server
    class RecieveThread implements Runnable
    {
        private Socket  socket;// a connected socket
       private String val;
        /** construct a thread */
        public RecieveThread(Socket socket)
        {
            this.socket = socket;
            this.val=val;
        }
        
        /** run a thread */
        public void run()
        {
            System.out.println(" recieve thread started\n");
            try
            {
                in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
                while(true)
                    textArea.appendText("\nServer response-- "+in.readLine());  
            } // end try
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    //writes to the servver
       
         class WriteThread implements Runnable
    {
        private Socket  socket;// a connected socket
        
       
        /** construct a thread */
        public WriteThread(Socket socket)
        {
            this.socket = socket;
           
        }
        
        /** run a thread */
        public void run()
        {
            System.out.println("Write thread started\n");
            try
            {
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(val);
                //textArea.appendText("\nYou sent--"+val);  
            } // end try
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
       
     

    public static void main(String[] args)
    {
        Application.launch(args);
    }
}
