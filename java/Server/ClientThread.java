package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class ClientThread extends Thread {
	private Socket _myClient;
	private ServerMulti _myServer;
	private PrintWriter write;
	public ClientThread(ServerMulti serverMulti, Socket socket){
		System.out.println("Client Accepted! " + socket.getInetAddress().getHostAddress());
		_myServer = serverMulti;
		_myClient = socket;
	}

	@Override
	public void run() {
		String line;
		String name = _myClient.getInetAddress().getHostName();
		try{
			write = new PrintWriter(_myClient.getOutputStream(), true);//pushes client data
			BufferedReader read = new BufferedReader(new InputStreamReader(_myClient.getInputStream()));//gets client input
			write.println("Welcome to the server!");
			write.println("Type \"help\" to see the commands :D");
			write.println("Please enter your name: ");
			name = (read.readLine());
		do
		{
			line = read.readLine();
			boolean personalCommand = false; //Prevents other users from seeing commands. 
			if ( line == null )
				return;
			if(line.toLowerCase().trim().equals("users")){
				write.println("There are: " + _myServer.getUsers());
				personalCommand = true; 
			}
			if(line.toLowerCase().trim().equals("clean")){
				_myServer.checkForDeadThreads();
				personalCommand = true; 
			}
			if(line.toLowerCase().trim().equals("ipaddress")){
				write.println(_myClient.getInetAddress().getHostName());
				personalCommand = true; 
			}
			if(line.toLowerCase().trim().equals("thread")){
				write.println("ID: "+this.getId() + "| Priority: " + this.getPriority() + "| Group: " +this.getThreadGroup());
				personalCommand = true; 
			}
			if(line.toLowerCase().trim().equals("help")){
				write.println("Commands: help - this menu, clean - kill dead threads, users - #active users, IPAddress - get your IPAddress, Thread - current thread");
				personalCommand = true; 
			}
			if(!personalCommand){
				_myServer.forwardMessage(name + ": " + line,this.getId());
			}
		}while ( !line.trim().equals("bye"));
			_myClient.close();
			write.flush();
			write.close();
		}catch(IOException | java.lang.NullPointerException e){
			System.out.println("Client Left :( Goodbye friend");
			return;
		}
		
	
	}

	public void sendMessage(String line) {
		if(write!=null)
			write.println(line);
		else
			System.out.println("There is no write!");
	}
}
