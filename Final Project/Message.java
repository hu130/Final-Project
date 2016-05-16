import java.io.*;
import java.net.*;

class Message
{
	private byte command;
	private String text;
	private Client from;
	private Client to;
	private Room room;
	private byte [] name;

	public Message(byte com, String input, Client sending, Client receiving)
	{
		command = com;
		text = input;
		from = sending;
		to = receiving;
	}

	public Message(InputStream in, Client sending, Client receiving) throws IOException
	{
		
		from = sending;
		to = receiving;
		command = (byte)in.read();
		int nameSize = (byte)in.read()*256 + (byte)in.read();
		for (int i = 0; i < nameSize; i++)
		{
			name[i] = (byte)in.read();
		}
		int textSize = (byte)in.read()*256 + (byte)in.read();
		byte [] textArray = new byte [textSize];
		for (int i = 0; i < textSize; i++)
		{
			textArray[i] = (byte)in.read();
		}
		text = new String(textArray);
	}

	public byte getCommand()
	{
		return command;
	}

	public String getMessage()
	{
		return text;
	}
		
	public Client getSendingClient()
	{
		return from;
	}

	public Client getReceivingClient()
	{
		return to;
	}

	public static String readUnit(InputStream in) throws IOException
	{
		byte highByte = (byte)in.read();
		byte lowByte = (byte)in.read();
		int size = highByte * 256 + lowByte;
		byte [] incoming = new byte [size];
		for (int i = 0; i < size; i++)
		{
			incoming[i] = (byte)in.read();
		}
		return new String(incoming);
	}

	public void broadcast(DataOutputStream out) throws IOException
	{
		byte [] message = text.getBytes();
		byte [] sender = from.getName().getBytes();
		out.write(command);
		out.write(sender.length / 256);
		out.write(sender.length % 256);
		out.write(sender);
		out.write(message.length / 256);
		out.write(message.length % 256);
		out.write(message);
	}
}