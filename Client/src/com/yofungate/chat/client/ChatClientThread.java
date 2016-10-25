package com.yofungate.chat.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClientThread extends Thread
{  private Socket           socket   = null;
   private Client       client   = null;
   private String       clientName = "Anon";
   private DataInputStream  streamIn = null;

   public ChatClientThread(Client _client, Socket _socket, String _clientName)
   {  client   = _client;
      socket   = _socket;
      clientName = _clientName;
      open();  
      start();
   }
   public void open()
   {  try
      {  streamIn  = new DataInputStream(socket.getInputStream());
      }
      catch(IOException ioe)
      {  System.out.println("Error getting input stream: " + ioe);
         client.close();
      }
   }
   public void close()
   {  try
      {  if (streamIn != null) streamIn.close();
      }
      catch(IOException ioe)
      {  System.out.println("Error closing input stream: " + ioe);
      }
   }
   boolean err = false;
   public void run()
   {  
	  while (err==false)
      {  try
         {  
    	    client.handle(streamIn.readUTF());
         }
         catch(IOException ioe)
         {  
        	System.out.println("Listening error: " + ioe.getMessage());
        	err = true;
        	try {
				streamIn.close();
			} catch (IOException e) {
			}
            client.close();
            this.close();
         }
      }
   }
}