package com.yofungate.chat.server;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Server implements Runnable
{  private ChatServerThread clients[] = new ChatServerThread[50];
   private String clientNames[] = new String[100000];
   private int lastMsg[] = new int[100000];
   private boolean SocialSpy[] = new boolean[100000];
   private boolean authList[] = new boolean[100000];
   private ServerSocket server = null;
   private Thread       thread = null;
   private int clientCount = 0;

   public Server(int port)
   {  try
      {  
	   	 System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);  
         System.out.println("Server started: " + server);
         start();
      }
      catch(IOException ioe)
      {  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); }
   }
   public void run()
   {  while (thread != null)
      {  try
         {  System.out.println("Waiting for a client ..."); 
            addThread(server.accept()); }
         catch(IOException ioe)
         {  System.out.println("Server accept error: " + ioe); stop(); }
      }
   }
   private int findClient(int ID)
   {  for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }
   public synchronized void handle(int ID, String input)
   {  
	  if(input.length()>0){
	  try{
	  if(input.charAt(0)==','&&(input.split("-").length>1||input.equals(",bye"))){
		  	if(input.split("-")[0].equals(",name")){clientNames[ID] = input.split("-")[1]+"("+ID+")";clients[findClient(ID)].send(".ID-"+ID);}
		   	if(input.equals(",bye")){  	clients[findClient(ID)].send(".bye");	remove(ID); }
	  }else if(input.charAt(0)=='/'){
		  System.out.println(clientNames[ID]+"("+authList[ID]+")"+ " Used Command : "+input);
		   
		  try{
		  if(input.split(" ")[0].equalsIgnoreCase("/login")){if(input.split(" ")[1].equals("admin")){if(input.split(" ")[2].equals("datdongerpass")){clients[findClient(ID)].send("<font color=\"#32CD32\">[Server]:You have been authenticated</font>");authList[ID] = true;System.out.println(ID+" Has been authenticated.");}}}
		  }catch(ArrayIndexOutOfBoundsException e){
			  clients[findClient(ID)].send(".red Usage /login *user* *password*");
		  }
		  if(input.split(" ")[0].equalsIgnoreCase("/who")||input.split(" ")[0].equalsIgnoreCase("/list")||input.split(" ")[0].equalsIgnoreCase("/online")){
			  clients[findClient(ID)].send(".blue [Server]: There Are "+clientCount+" People on");
		  }
		  if(input.split(" ")[0].equalsIgnoreCase("/msg")||input.split(" ")[0].equalsIgnoreCase("/message")){
			  try{			  
				  lastMsg[ID]=Integer.parseInt(input.split(" ")[1]);
				  lastMsg[Integer.parseInt(input.split(" ")[1])]=ID;
				  for (int i = 0; i < clientCount; i++) if(SocialSpy[i]) clients[i].send(".gray SS:["+clientNames[ID]+"]->["+clientNames[Integer.parseInt(input.split(" ")[1])]+"] : "+input.replace("/reply ", "").replace("/r", "").replace(lastMsg[ID]+"", ""));  
				  clients[findClient(Integer.parseInt(input.split(" ")[1]))].send(".blue ["+clientNames[ID]+"]->[You] : "+input.replace("/message ", "").replace("/msg", "").replace(input.split(" ")[1], ""));
				  clients[findClient(ID)].send(".orange [You]->["+clientNames[Integer.parseInt(input.split(" ")[1])]+"] : "+input.replace("/message ", "").replace("/msg", "").replace(input.split(" ")[1], ""));
			  }catch(ArrayIndexOutOfBoundsException e){
				  clients[findClient(ID)].send(".red Usage /msg ID message");
			  }
		  }
		  if(input.split(" ")[0].equalsIgnoreCase("/r")||input.split(" ")[0].equalsIgnoreCase("/reply")){
			  for (int i = 0; i < clientCount; i++) if(SocialSpy[clients[i].getID()]) clients[i].send(".gray SS:["+clientNames[ID]+"]->["+clientNames[lastMsg[ID]]+"] : "+input.replace("/reply ", "").replace("/r", "").replace(lastMsg[ID]+"", ""));  
			  clients[findClient(lastMsg[ID])].send(".blue ["+clientNames[ID]+"]->[You] : "+input.replace("/reply ", "").replace("/r", "").replace(lastMsg[ID]+"", ""));
			  clients[findClient(ID)].send(".orange [You]->["+clientNames[lastMsg[ID]]+"] : "+input.replace("/reply ", "").replace("/r", "").replace(lastMsg[ID]+"", ""));
		  }
		  if(input.split(" ")[0].equalsIgnoreCase("/clear")){
			  clients[findClient(ID)].send(".clear");
		  }
		  if(authList[ID]){
		    	if(input.split(" ")[0].equalsIgnoreCase("/kick")){
		    		clients[findClient(ID)].send("<font color=\"red\">[Server]:You have kicked "+clientNames[Integer.parseInt(input.split(" ")[1])]+"</font>");
		    		clients[findClient(Integer.parseInt(input.split(" ")[1]))].send("You have been kicked!");
		    		clients[findClient(Integer.parseInt(input.split(" ")[1]))].send(".bye");
		    		remove(Integer.parseInt(input.split(" ")[1]));
		    	}
		    	if(input.split(" ")[0].equalsIgnoreCase("/ss")||input.split(" ")[0].equalsIgnoreCase("/socialspy")||input.split(" ")[0].equalsIgnoreCase("/spy")){
		    		try{
		    			SocialSpy[ID] = !SocialSpy[ID];
		    		}catch(NullPointerException npe){SocialSpy[ID]=true;}
		    		clients[findClient(ID)].send(SocialSpy[ID] ? ".green [Server]:SocialSpy Enabled" : ".red [Server]:SocialSpy Disabled");
		    	}
		    }
	  }else{
		 if(authList[ID]){
			 System.out.println(clientNames[ID]+input);
	         for (int i = 0; i < clientCount; i++) clients[i].send(".admin " + clientNames[ID] + ": " + input);  
		 }else{
			 System.out.println(clientNames[ID]+input);
			 for (int i = 0; i < clientCount; i++) clients[i].send(clientNames[ID] + ": " + input);  
		 }
	  }
	  }catch(Exception e){e.printStackTrace();clients[findClient(ID)].send(".red You caused an error at server!!!");}
	  }
   }
   public void handleServerCmd(String command, String[] args) {
		if(command.equalsIgnoreCase("say")){
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < args.length; i++) {
			   result.append( args[i] );
			   result.append(" ");
			}
			for (int i = 0; i < clientCount; i++) clients[i].send(".pink [Server]: " + result.toString()); 
		}
	}
   public synchronized void remove(int ID)
   {  
	  int pos = findClient(ID);
      if (pos >= 0)
      {  ChatServerThread toTerminate = clients[pos];
         System.out.println("Removing client thread " + ID + " at " + pos);
         clientNames[ID]=null;
         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;
         try
         {  toTerminate.close(); }
         catch(IOException ioe)
         {  System.out.println("Error closing thread: " + ioe); }
         toTerminate.stop(); }
   }
   private void addThread(Socket socket)
   {  
	  if (clientCount < clients.length)
      {  System.out.println("Client accepted: " + socket);
         String Clientname = "Anon";
		clients[clientCount] = new ChatServerThread(this, socket, Clientname);
         try
         {  clients[clientCount].open(); 
            clients[clientCount].start();  
            clientCount++; }
         catch(IOException ioe)
         {  System.out.println("Error opening thread: " + ioe); } }
      else
         System.out.println("Client refused: maximum " + clients.length + " reached.");
   }
   public void start()
   {  if (thread == null)
      {  thread = new Thread(this); 
         thread.start();
      }
   }
   public void stop()
   {  if (thread != null)
      {  
	     thread.stop(); 
         thread = null;
      }
   }
   public static void main(String args[])
   {  Server server = null;
	      if (args.length != 1)
	    	  server = new Server(2083);
	      else
	    	 server = new Server(Integer.parseInt(args[0]));
      ServerCommandHandler consolehandler = new ServerCommandHandler(server, new BufferedReader(new InputStreamReader(System.in)));
      consolehandler.start();
      
   }

}
