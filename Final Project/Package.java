import java.io.*;

class Package
{ 	 
	private byte commandByte;
	private Command command;
	private String name;
	private String text;
	private static final String special = "*";
	private static final String space = " ";
	private static final String whisper = "[WHISPER]";
	private boolean namesOn;

	public Package(InputStream in, boolean names) throws IOException
	{
		command = Command.get(in.read());
		int nameSize = in.read()*256 + in.read();
		byte [] nameArray = new byte [nameSize];
		for (int i = 0; i < nameSize; i++)
		{
			nameArray[i] = (byte)in.read();
		}
		name = new String(nameArray);
		int textSize = in.read()*256 + in.read();
		byte [] textArray = new byte [textSize];
		for (int i = 0; i < textSize; i++)
		{
			textArray[i] = (byte)in.read();
		}
		text = new String(textArray);
		if (name.equals("")) name = "EVERYONE";
		namesOn = names;
	}
		
	public Package(byte pCommand, String pName, String pText)
	{	
		commandByte = pCommand;
		command = Command.get((int)pCommand);
		name = pName;
		text = pText;
	}
	
	public void send(OutputStream out) throws IOException
	{
		out.write(commandByte);
		out.write(name.length() / 256);
		out.write(name.length() % 256);
		out.write(name.getBytes());
		out.write(text.length() / 256);
		out.write(text.length() % 256);
		out.write(text.getBytes());
	}
	
	public void printToScreen()
	{
		String chatMessage = null;
		switch (command)
		{	
			case ERROR_MESSAGE: chatMessage = text; break;
			case BROADCAST: chatMessage = (namesOn) ? (name + space + text) : (text); break;
			case ASTERISKS: chatMessage = (special + name + space + text + special); break;
			case CHANGE_NAME: chatMessage = text; break;
			case DISCONNECT: chatMessage = (name + space + text); break;
			case JOIN_ROOM: chatMessage = text; break;
			case LEAVE_ROOM: chatMessage = text; break;
			case MESSAGE_ROOM: chatMessage = (namesOn) ? (name + space + text) : (text); break;
			case LIST_ROOMS: chatMessage = text; break;
		}
		System.out.println(chatMessage);
		
	}

	public Command getCommand()
	{
		return command;
	}

	public String getName()
	{
		return name;
	}

	public String getText()
	{
		return text;
	}
}