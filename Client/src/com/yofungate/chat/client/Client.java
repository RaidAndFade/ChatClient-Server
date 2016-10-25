package com.yofungate.chat.client;

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
public class Client extends JFrame implements KeyListener
{  
   
   private static final long serialVersionUID = 7999291528285974685L;
   private Socket socket              = null;
   private DataInputStream  console   = null;
   private DataOutputStream streamOut = null;
   private ChatClientThread client    = null;
   private JTextPane  display = new JTextPane();
   private JScrollPane scrollPane;
   private TextField input   = new TextField();
   private Button    send    = new Button("Send"), connect = new Button("Connect"),
                     quit    = new Button("Quit");
   private String    serverName = "localhost";
   private int       serverPort = 2083;
   String    clientName = "Anon";
   public String macAddr;
   private String text = "";
   private String ID;

   
   public Client(){
   super("DougloChat");
   Panel keys = new Panel();
   scrollPane = new JScrollPane(display,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
           JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
   scrollPane.setMaximumSize(new Dimension(1000,200));
   keys.setLayout(new GridLayout(1,2));
   keys.add(quit); 
   keys.add(connect);
   Panel south = new Panel(); 
   south.setLayout(new BorderLayout());
   south.add("West", keys);
   south.add("Center", input);
   south.add("East", send);
   Label title = new Label("DougloChat", Label.CENTER);
   title.setFont(new Font("Helvetica", Font.BOLD, 14));
   setLayout(new BorderLayout());
   quit.setEnabled(false);
   send.setEnabled(false);
   EventHandler h = new EventHandler();
   display.setEditable(false);
   display.setEditorKit(new HTMLEditorKit());
   quit.addActionListener(h);
   send.addActionListener(h);
   connect.addActionListener(h);
   input.addKeyListener(this);
   add("North", title);
   add("Center", scrollPane);
   add("South",  south);
   }
   private class EventHandler implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if (e.getSource() == quit)
		      {  send(",bye");
		         quit.setEnabled(false);
		         send.setEnabled(false);
		         connect.setEnabled(true);
		         }
		      else if (e.getSource() == connect)
		      {
		    	  connect(serverName, serverPort);
		      }
		      else if (e.getSource() == send)
		      {  
		    	  send(); 
		    	  input.requestFocus();
		      }
		}
	}
   public void connect(String serverName, int serverPort)
   {  
	  handle(".clear");
	  println("<font color=\"red\">Establishing connection. Please wait ...</font>");
      try
      {  
    	 socket = new Socket(serverName, serverPort);
         println("<font color=\"lime\">Connected: " + socket + "</font>");
         open();
         send(",name-"+clientName);
         send(",macaddr-"+macAddr);
         send.setEnabled(true);
         connect.setEnabled(false);
         quit.setEnabled(true);
      }
      catch(UnknownHostException uhe)
      {  
    	  println("Host unknown: " + uhe.getMessage());
      }
      catch(IOException ioe)
      {
    	  println("Unexpected exception: " + ioe.getMessage());
      }
   }
   private void send()
   {  
	  try
      {  
	   streamOut.writeUTF(input.getText());
	   streamOut.flush();
	   input.setText("");
	  }
      catch(IOException ioe)
      {  
    	  println("Sending error: " + ioe.getMessage());
    	  close();
      }
   }
   private void send(String s)
   {  
	  try
      {  
	   streamOut.writeUTF(s);
	   streamOut.flush();
	  }
      catch(IOException ioe)
      {  
    	  println("Sending error: " + ioe.getMessage());
    	  close();
      }
   }
   public void handle(String msg)
   { 
	   
	  if(msg.equalsIgnoreCase(".clear")){ 
		  text = "";
		  display.setText(text);
   	  }else
	  if (msg.equals(".bye"))
      {  
		  println("<font color=\"red\">Good bye.</font>");
		  close();
		  quit.setEnabled(false);
	      send.setEnabled(false);
	      connect.setEnabled(true);
	  }else
	  if (msg.split("-")[0].equalsIgnoreCase(".ID")){
		  if(msg.split("-").length>1){
			  ID = msg.split("-")[1];
		  }
	  }else
	  if (msg.split(" ")[0].equalsIgnoreCase(".red")){
		  println("<font color=\"red\">"+msg.replace(".red ", "")+"</font>");
	  }else
	  if (msg.split(" ")[0].equalsIgnoreCase(".pink")){
		  println("<font color=\"#E4287C\">"+msg.replace(".pink ", "")+"</font>");
	  }else
	  if (msg.split(" ")[0].equalsIgnoreCase(".gray")){
		  println("<font color=\"#C0C0C0\">"+msg.replace(".gray ", "")+"</font>");
	  }else
	  if (msg.split(" ")[0].equalsIgnoreCase(".green")){
		  println("<font color=\"#32CD32\">"+msg.replace(".green ", "")+"</font>");
	  }else
	  if (msg.split(" ")[0].equalsIgnoreCase(".blue")){
		  println("<font color=\"blue\">"+msg.replace(".blue ", "")+"</font>");
	  }else
	  if (msg.split(" ")[0].equalsIgnoreCase(".orange")){
		  println("<font color=\"orange\">"+msg.replace(".orange ", "")+"</font>");
	  }else
	  if (msg.contains(ID)){
		  println("<font color=\"#CCCC00\">"+msg.replace(".admin ", "[Admin]")+"</font>");
	  }else
	  if (msg.split(" ")[0].equalsIgnoreCase(".admin")){
		  println("<font color=\"#32CD32\">"+msg.replace(".admin ", "[Admin]")+"</font>");
	  }
      else println(msg);
   }
   public void open()
   {  
	  try
      {  
		 streamOut = new DataOutputStream(socket.getOutputStream());
         client = new ChatClientThread(this, socket, clientName);
      }
      catch(IOException ioe)
      {  
    	 println("<font color=\"#CCCC00\">Error opening output stream: " + ioe +" </font>");
      }
   }
   public void close()
   {  
	  try
      {  
		 if (streamOut != null)  streamOut.close();
         if (socket    != null)  socket.close();
      }
      catch(IOException ioe)
      {  
      println("Error closing ...");
      }
      client.close();
      }
   private void println(String msg)
   {  
	  text = text + msg + "<br>";
	  display.setVisible(false);
	  display.setText(text);
	  display.setCaretPosition(display.getDocument().getLength());
	  display.setVisible(true);
   }
public void keyTyped(KeyEvent e) {}
public void keyPressed(KeyEvent e) {
	if(e.getKeyCode()==10){
		send(); 
		input.requestFocus();
	}
}
public void keyReleased(KeyEvent e) {}
}