package com.yofungate.chat.server;

import java.io.BufferedReader;

public class ServerCommandHandler extends Thread {
	   private BufferedReader reader = null;
	   private Server server = null;
	   public ServerCommandHandler(Server _server, BufferedReader br){  
	        this.reader = br;  
	        this.server = _server;
	    }  
	      
	@Override  
	public void run() {  
	    System.out.println("Starting Console Command Processor");  
	    GetHoldOfTheConsole();  
	    super.run();  
	}  
	      
    public void GetHoldOfTheConsole(){  
	    while(true){  
	        try{  
	            String getData = reader.readLine();  
	            handle(getData);
	        }catch(Exception ex){  
	             
	        }  
	    }  
	}

	private void handle(String getData) {
		String command = getData.charAt(0)=='/' ? getData.replaceFirst("/", "").split(" ")[0] : getData.replaceFirst("", "").split(" ")[0];
		String[] args = getData.split(" ");
		for(int i=1;i<args.length;i++){
		args[i-1] = args[i];
		}
		args[args.length-1]="";
		server.handleServerCmd(command,args);
	}
}
