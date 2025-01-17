package edu.seg2105.edu.server.ui;

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;



public class ServerConsole implements ChatIF {
	EchoServer server;
	Scanner fromConsole;
	final public static int DEFAULT_PORT = 5555;

	public ServerConsole(int port) {
		// TODO Auto-generated constructor stub
	 
		//Initialize server instance
	    server= new EchoServer(port, this);
	    // Create scanner object to read from console
	    fromConsole = new Scanner(System.in); 
	}
	
	
	 //Instance methods ************************************************
	  
	  /**
	   * This method waits for input from the console.  Once it is 
	   * received, it sends it to the client's message handler.
	   */
	  public void accept() 
	  {
	    try
	    {

	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        server.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }

	  /**
	   * This method overrides the method in the ChatIF interface.  It
	   * displays a message onto the screen.
	   *
	   * @param message The string to be displayed.
	   */
	  
	@Override
	public void display(String message)
	{
		System.out.println("> " + message);

	}
	
	 /**
	   * This method is responsible for the creation of 
	   * the server instance (there is no UI in this phase).
	   *
	   * @param args[0] The port number to listen on.  Defaults to 5555 
	   *          if no argument is entered.
	   */
	  public static void main(String[] args) 
	  {
	    int port = 0; //Port to listen on

	    try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(Throwable t)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
		
	    ServerConsole serverUI = new ServerConsole(port);
	    
	    try 
	    {
	    	serverUI.server.listen(); //Start listening for connections
	    	serverUI.accept(); //listen for commands in the UI
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println("ERROR - Could not listen for clients!");
	    }
	  }

}
