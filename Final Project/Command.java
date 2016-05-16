enum Command
{
	ERROR_MESSAGE, BROADCAST, CHANGE_NAME, ASTERISKS, DISCONNECT, JOIN_ROOM, LEAVE_ROOM, 
	MESSAGE_ROOM, LIST_ROOMS;

	public static Command get(int i)
	{
		return values()[i];
	}
}