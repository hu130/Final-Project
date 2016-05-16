import java.util.concurrent.*;
import java.io.*;
import java.net.*;

class ClientBox implements Runnable
{
	private Client client;
	private TCPServer server;
	volatile private boolean needToRemove;
	private ConcurrentLinkedQueue<Message> inbox = new ConcurrentLinkedQueue<Message>();
	private ConcurrentLinkedQueue<Message> outbox = new ConcurrentLinkedQueue<Message>();

	public ClientBox(Client client, TCPServer server)
	{
		this.client = client;
		this.server = server;
	}

	public synchronized void put(Message message)
	{
		inbox.add(message);
	}

	public synchronized Message get()
	{
		return outbox.poll();
	}
	
	protected void changeName(String newName)
	{
		client.setName(newName);
	}

	protected Client getClient()
	{
		return client;
	}
	
	protected TCPServer getServer()
	{
		return server;
	}

	protected boolean needsRemoving()
	{
		return needToRemove;
	}

	protected void setToRemove()
	{
		needToRemove = true;
	}

	private void collectFromClient() throws IOException
	{
		InputStream in = client.getSocket().getInputStream();
		if (in.available() > 0)
		{
			MessageHandler handler = new MessageHandler(this);
			Message message = handler.processStream();
 			if (message != null) 
 			{
 				outbox.add(message);
 			}
		}	
	}

	private void sendToClient() throws IOException
	{
		DataOutputStream out = new DataOutputStream(client.getSocket().getOutputStream());
		if (inbox.size() > 0)
		{
			Message mes = inbox.poll();
			try
			{
				mes.broadcast(out);
			}
			catch(SocketException e)
			{
				outbox.add(new Message((byte)5, "has left the chatroom", client, null));
				needToRemove = true;
			}
		}
	}

	public void run()
	{
		while(true)
		{
			try
			{
				collectFromClient();
				sendToClient();
			}
			catch(IOException e){}
		}
	}
}