import java.net.*;
import java.util.*;
import java.io.*;

class TCPServer
{

	private Hashtable<String,ClientBox> boxes = new Hashtable<String,ClientBox>();
	private Hashtable<String,Room> rooms = new Hashtable<String,Room>();
	private LinkedList<Message> messages = new LinkedList<Message>();
	private Listener listener;

	public TCPServer(int port) throws IOException
	{
		listener = new Listener(port, this);
	}

	public void start() throws IOException
	{
		System.out.println("Server started...");
		(new Thread(listener)).start();
		while(true)
		{
			collectMessages();
			disperseMessages();
		}
	}

	protected void addClient(String name, ClientBox box)
	{
		synchronized (boxes)
		{
			boxes.put(name, box);
		}
	}

	protected void changeClientName(Client client, String newName)
	{
		synchronized (boxes)
		{
			ClientBox original = boxes.get(client.getName());
			boxes.remove(client.getName());
			original.changeName(newName);
			boxes.put(client.getName(), original);
		}
	}

	protected Client getClient(String name)
	{
		synchronized (boxes)
		{
			ClientBox result = boxes.get(name);
			if (result != null) return result.getClient();
		}
		return null;
	}

	protected void removeRoom(Room room)
	{
		synchronized (rooms)
		{
			rooms.remove(room.getName());
		}
	}

	protected void addRoom(Room room)
	{
		synchronized (rooms)
		{
			rooms.put(room.getName(), room);
		}
	}

	protected Room getRoom(String name)
	{
		synchronized (rooms)
		{
			return rooms.get(name);
		}
	}

	protected boolean noRoomsExist()
	{
		synchronized (rooms)
		{
			return (rooms.size() == 0);
		}
	}

	protected String getRoomList()
	{
		String fullList = "";
		synchronized (rooms)
		{
			for (Room room : rooms.values())
			{
				fullList += room.getName() + ", ";
			}
		}
		return fullList;
	}	

	protected void updateRooms()
	{
		Room toRemove = null;
		synchronized (rooms)
		{
			for (Room room : rooms.values())
			{
				if (room.isEmpty()) toRemove = room;
			}
			if (toRemove != null) rooms.remove(toRemove.getName());
		}
	}
	
	private void collectMessages() throws IOException
	{
		synchronized (boxes)
		{	
			for (ClientBox box : boxes.values())
			{
				Message mes = box.get();
				if (mes != null) 
				{
					messages.add(mes);
					updateRooms();
				}
			}
		}
	}

	private void disperseMessages() throws IOException
	{
		synchronized (messages)
		{
			for (Message message : messages)
			{
				Command command = Command.get(message.getCommand());
				boolean roomMessage = (message.getCommand() > 5);
				synchronized (boxes)
				{
					for (ClientBox box : boxes.values())
					{
						Client to = message.getReceivingClient();
 						if (to == null || (to != null && box.getClient().equals(to))) 	//handles whisper
 						{
 							box.put(message);
 						}
					}
				}
			}
		}
		updateClientList();
		messages.clear();
	}

	private void updateClientList() throws IOException
	{
		ClientBox toRemove = null;
		synchronized (boxes)
		{
			for (ClientBox box : boxes.values())
			{
				if (box.needsRemoving()) 
				{
					toRemove = box;
				}
			}
		}
		if (toRemove != null) 
		{
			Message mes = toRemove.get();
			if (mes != null) 
			{
				messages.add(mes);
				disperseMessages();
			}
			boxes.remove(toRemove.getClient().getName());
		}
	}

	private boolean clientMatches(Message mes, ClientBox box)
	{
		return (mes.getSendingClient().getRoom().equals(box.getClient().getRoom()));
	}
	
	public static void main(String [] args) throws IOException
	{
		TCPServer server = new TCPServer(3000);
		server.start();
	}
}