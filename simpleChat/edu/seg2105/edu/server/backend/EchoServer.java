package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import ocsf.server.*;
import java.io.*;

import edu.seg2105.client.common.ChatIF;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ChatIF serverUI;
  final private static String loginKey = "loginId";
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	
	//echo message from client to everyone
	String loginId = (String) client.getInfo(loginKey);
		
	System.out.println("Message received: " + msg + " from " + loginId);
		
	this.sendToAllClients( loginId + "> "+ msg);
	  
	if(msg.toString().startsWith("#login")) 
	{
		//check if first time called
		if(client.getInfo(loginKey) == null) 
		{
			//store loginId
			String[] words = msg.toString().split(" "); 
			loginId = words[1];
			client.setInfo(loginKey, loginId );
			
			//display that client successfully logged in
			this.serverUI.display(loginId + " has logged on.");
			try {
				client.sendToClient(loginId + " has logged on");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else 
		{
			//already logged in Terminate connection
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	} 
		
	
    
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  
  //Class methods ***************************************************
   
  
  
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
 * @throws IOException 
   */
	public void handleMessageFromServerUI(String message) 
	{
		// TODO Auto-generated method stub
		//check if message is a command
		 
		if(message.length() > 1 && message.charAt(0) == '#') { // evaluate expression from left to right so avoid index error
			String[] words = message.split(" ");
			
			String command = words[0].substring(1);
			
			switch (command) {
				case "quit" :
					try
					{
						this.close();
					}
					catch(IOException e) {}
					System.exit(0);
					break;
					
				case "start" :
					if(this.isListening() )//server hasn't stop
					{
						System.out.println("You are already listening for Clients");
					} else {
						try {
							this.listen();
						} catch (IOException e) {}
					}
					
					break;
					
				case "stop" : 
					this.stopListening();
					break;
				case "close" :
					try {
						this.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case "setport" :
					try
					{
						int newPort = Integer.parseInt(words[1]);
						this.setPort(newPort);
					}
					catch(NumberFormatException e) {
						this.serverUI.display("InvalidPortNumber");
					}
					break;
	
				case "getport" : 
					this.serverUI.display( Integer.toString(this.getPort()));
					break;
				default :
					this.serverUI.display(command + " is not a valid command");
					break;
					
					
			}
		} else 
		{
			  
			//otherwise send message to server and all clients
			System.out.println("SERVER MSG> " + message);
		    this.sendToAllClients("SERVER MSG> " + message);
		}
	}
  
  
  //hooks
 

	/**
	 * Implementation of the hook method called each time a new client connection is
	 * accepted. The default implementation does nothing.
	 * @param client the connection connected to the client.
	 */
	protected void clientConnected(ConnectionToClient client)
	{
		System.out.println("A client just connected");
	}

	/**
	 * Implementation of the hook method called each time a client disconnects.
	 * The default implementation does nothing. The method
	 * may be overridden by subclasses but should remains synchronized.
	 *
	 * @param client the connection with the client.
	 */
	synchronized protected void clientDisconnected(ConnectionToClient client) 
	{
		System.out.println("A client just disconnected");
		this.serverUI.display(client.getInfo(loginKey) + " just disconnected");
	}

	
}
//End of EchoServer class
