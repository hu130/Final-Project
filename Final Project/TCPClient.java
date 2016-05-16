import java.net.*;
import java.io.*;
import java.util.*;

class TCPClient
{
	private boolean NAMES_ON;
	private static final String PROTOCOL_CODE = "DIANA.PROTOCOL.V5";
	private String clientName;
	private Socket mySocket;
	private InputStream in;
	private DataOutputStream out;
	private BufferedReader keyboard;
	private static byte [] self = {127, 0, 0, 1};
	private static char marker = '/';
	private String empty = "";
	private boolean socketClosed;
	
	public TCPClient(int serverPort, byte [] address) throws IOException
	{
		mySocket = new Socket(InetAddress.getByAddress(address), serverPort);
		in = mySocket.getInputStream();
		keyboard = new BufferedReader(new InputStreamReader(System.in));
		out = new DataOutputStream(mySocket.getOutputStream());
	}

	public void start() throws IOException
	{
		configureSettings();
		sendCodeToServer();
		sendNameToServer();
		while(!socketClosed)
		{
			readFromServer();
			writeToServer();
		}
		closeConnection();
	}

	private static byte [] getIP(String s)
	{
		byte [] ip = new byte [4];
		StringTokenizer token = new StringTokenizer(s, ".");
		for (int i = 0; token.hasMoreTokens(); i++)
		{
			ip[i] = (byte)(Integer.parseInt(token.nextToken()));
		}
		return ip;
	}

	private void configureSettings() throws IOException
	{
		while(true)
		{
			System.out.print("Chater Names ON/OFF? ");
			String onOff = keyboard.readLine();
			if (onOff.equals("ON")) 
			{
				NAMES_ON = true;
				return;
			}
			else if (onOff.equals("OFF"))
			{
				NAMES_ON = false;
				return;
			}
			else System.out.println("Invalid input. Input ON or OFF");
		}
	}

	private void sendCodeToServer() throws IOException
	{
		out.write(PROTOCOL_CODE.getBytes());
	}

	private void sendNameToServer() throws IOException
	{
		System.out.print("Please enter your name: ");
		while(true)
		{
			if (keyboard.ready()) 
			{
				clientName = keyboard.readLine();
				out.write(clientName.length() / 256);
				out.write(clientName.length() % 256);
				out.write(clientName.getBytes());
				break;
			}
		}
	}

	private void readFromServer() throws IOException
	{
		if (in.available() > 0)
		{
			Package toReceive = new Package(in, NAMES_ON);
			toReceive.printToScreen();
		}
	}

	private void writeToServer() throws IOException
	{
		Package packageToSend = null;
		if (keyboard.ready())
		{
			String input = keyboard.readLine();
			if (input.startsWith("/"))
			{
				CommandHandler handler = new CommandHandler(input);
				packageToSend = handler.process();
				if (packageToSend == null) return;
				if (handler.disconnectRequested()) socketClosed = true;
				packageToSend.send(out);
			}
			else
			{
				packageToSend = new Package((byte)1, empty, input);
				packageToSend.send(out);
			}
		}
	}
	
	private void closeConnection() throws IOException
	{
		in.close();
		out.close();
		keyboard.close();
		mySocket.close();	
		System.out.println("You have left the chatroom.");
	}
	
	public static void main(String [] args) throws IOException
	{
		byte [] ipAddress = args.length > 1 ? getIP(args[2]) : self;
		TCPClient user = new TCPClient(3000, self);
		user.start();
	}
}