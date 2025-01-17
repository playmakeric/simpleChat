// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;
  long loginId;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(long loginId, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginId = loginId;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
 * @throws IOException 
   */
  public void handleMessageFromClientUI(String message) throws IOException
  {
	
	//check if message is a command
	 
	if(message.length() > 1 && message.charAt(0) == '#') { // evaluate expression from left to right so avoid index error
		String[] words = message.split(" ");
		
		String command = words[0].substring(1);
		
		switch (command) {
			case "quit" :
				this.quit();
				break;
			case "logoff" :
				try 
				{
					this.closeConnection();
				} 
				catch (IOException e) {}
				break;
			case "sethost" :			
				this.setHost(words[1]);		
				break;
			case "setport" :
				try
				{
					int newPort = Integer.parseInt(words[1]);
					this.setPort(newPort);
				}
				catch(NumberFormatException e) {
					this.clientUI.display("InvalidPortNumber");
				}
				break;
			case "login" :
				if(this.isConnected()) {
					this.clientUI.display("You are already Connected to the Server");
				}
				else {
					this.openConnection();
				}
				break;
			case "gethost" :
				this.clientUI.display(this.getHost());
				break;
			case "getport" : 
				this.clientUI.display( Integer.toString(this.getPort()));
				break;
			default :
				this.clientUI.display(command + " is not a valid command");
				break;
				
				
				
		}
	}
	else {
		  
		//otherwise send message to server
	    try
	    {
	      sendToServer(message);
	    }
	    catch(IOException e)
	    {
	      clientUI.display
	        ("Could not send message to server.  Terminating client.");
	      quit();
	    }
	}
	
	  
	
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  
  //Hooks
  
  /**
	 * Implementation of the hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
	protected void connectionClosed() {
		this.clientUI.display("Connection closed");
	}
	
	/**
	 * Implementation of the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
	protected void connectionException(Exception exception) {
		this.clientUI.display("Server has shut down");
		this.quit();
	}
	
	/**
	 * Hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
	protected void connectionEstablished() {
		String message = "#login "+ Long.toString(this.loginId);
		
		 try
		    {
		      sendToServer(message);
		    }
		    catch(IOException e)
		    {
		      clientUI.display
		        ("Could not send message to server.  Terminating client.");
		      quit();
		    }
	}
}
//End of ChatClient class
