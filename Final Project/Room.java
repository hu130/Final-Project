import java.io.*;
import java.util.concurrent.*;

class Room
{
	private ConcurrentLinkedQueue<ClientBox> roomBoxes = new ConcurrentLinkedQueue<ClientBox>();
	private String roomName;
	private TCPServer server;

	public Room(String name, ClientBox box)
	{
		roomName = name;
		roomBoxes.add(box);
		box.getClient().setRoom(this);
	}

	public String getName()
	{
		return roomName;
	}

	public void addClient(ClientBox box)
	{
		roomBoxes.add(box);
		box.getClient().setRoom(this);
	}

	public void removeClient(ClientBox box)
	{
		for (ClientBox xbox : roomBoxes)
		{
			if (xbox.equals(box))
			{
				roomBoxes.remove(box);
				box.getClient().setRoom(null);
			}
		}
	}

	public void disperse(Message message) throws IOException
	{
		for (ClientBox box : roomBoxes)
		{
			box.put(message);
		}
	}

	public boolean isEmpty()
	{
		return roomBoxes.isEmpty();
	}
}