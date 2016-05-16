import java.net.*;
class Client
{
	private Socket socket;
	private String name;
	private Room currentRoom;
	
	public Client(Socket pSocket, String pName)
	{
		socket = pSocket;
		name = pName;
	}
	
	public boolean equals(Client c)
	{
		return (socket.equals(c.getSocket()) && name.equals(c.getName()));	
	}
	
	public int hashCode()
	{
		return (37 * (socket.hashCode() + name.hashCode()));
	}

	public Socket getSocket()
	{
		return socket;
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String newName)
	{
		name = newName;
	}
	
	public void setRoom(Room room)
	{
		currentRoom = room;
	}

	public Room getRoom()
	{
		return currentRoom;
	}
}