import java.io.*;
import java.net.*;

class MessageHandler
{
	private ClientBox box;
	private TCPServer server;
	private Client client;
	
	public MessageHandler(ClientBox box)
	{
		this.box = box;
		client = box.getClient();
		server = box.getServer();
	}

	public Message processStream() throws IOException
	{
		DataOutputStream out = new DataOutputStream(client.getSocket().getOutputStream());
		InputStream in = client.getSocket().getInputStream();
		if (in.available() > 0)
		{
			byte commandByte = (byte)in.read();
			Command command = Command.get(commandByte);
			Client toClient = server.getClient(Message.readUnit(in));
			String text = Message.readUnit(in);	
			switch (command)
			{
				case BROADCAST:		
									return new Message(commandByte, text, client, toClient);
									
				case CHANGE_NAME:	
									String oldName = client.getName();
									server.changeClientName(client, text);
									return new Message(commandByte, (oldName + " changed their name to " + client.getName()), client, toClient);
				
				case DISCONNECT:	
									box.setToRemove();
									return new Message(commandByte, "has left the chatroom", client, toClient);
			}
		}
		return null;
	}
}