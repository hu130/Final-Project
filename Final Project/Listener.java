import java.io.*;
import java.net.*;

class Listener implements Runnable
{
	private ServerSocket mainSocket;
	private TCPServer server;
	private static final String PROTOCOL_CODE = "DIANA.PROTOCOL.V5";

	public Listener(int port, TCPServer server) throws IOException
	{
		mainSocket = new ServerSocket(port);
		this.server = server;
	}

	public void run()
	{
		Client potentialClient = null;
		while(true)
		{
			try
			{
				potentialClient = listenForNewClient();
			}
			catch(IOException e) {}
			if (potentialClient != null) 
			{
				ClientBox pipe = new ClientBox(potentialClient, server);
				server.addClient(potentialClient.getName(), pipe);
				(new Thread(pipe)).start();
			}
		}
	}

	private Client listenForNewClient() throws IOException
	{
		Socket newUser = null;
		try
		{
			newUser = mainSocket.accept();
		}
		catch(SocketTimeoutException e) {}
		mainSocket.setSoTimeout(10);
		if (newUser != null && checkProtocol(newUser)) return new Client(newUser, readUnit(newUser.getInputStream()));
		return null;
	}	

	private boolean checkProtocol(Socket soc) throws IOException
	{
		InputStream in = soc.getInputStream();
		byte [] code = new byte [PROTOCOL_CODE.length()];
		for (int i = 0; i < PROTOCOL_CODE.length(); i++)
		{
			code[i] = (byte)in.read();
		}
		return (PROTOCOL_CODE.equals(new String(code)));
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
}