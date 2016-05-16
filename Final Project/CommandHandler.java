import java.io.*;
import java.util.*;

class CommandHandler
{
	private String input;
	private Hashtable<String,Command> commandTable = new Hashtable();
	private String empty = "";
	private boolean socketClosed;

	public CommandHandler(String unprocessedString)
	{
		input = unprocessedString;
	}

	public Package process() throws IOException
	{	
		if (inputIs("bye")) 
		{
			socketClosed = true; return new Package((byte)5, empty, empty);
		}

		else 
		{
			System.out.println("No such command. Type '/bye' to leave the chatroom.");
		}
		return null;
	}

	private boolean inputIs(String s)
	{	
		return (input.length() >= s.length()+1 && input.substring(1, (s.length()+1)).equals(s));
	}
	
	public boolean disconnectRequested()
	{
		return socketClosed;
	}
}