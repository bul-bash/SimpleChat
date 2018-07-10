package Server;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.io.IOException;

public class ServerMulti {
	private ServerSocket _serverSocket;
	private final int PORT = 54000;
	private ArrayList<ClientThread> _clients;
	//getPortRequests
	public ServerMulti() throws IOException{
		//Initialize variables
		_clients = new ArrayList<ClientThread>();

		try{
			_serverSocket = new ServerSocket(PORT);
		}catch(java.net.BindException e){
			System.out.println("This port("+PORT+") is currently being used... Ending Program");
			return;
		}

		//Loop - accept - and pass on new clients to threads
		while(true){
			System.out.println("Accepting new clients...");
			ClientThread x = new ClientThread(this, _serverSocket.accept());
			x.start();
			_clients.add(x); //add Thread to client list
			checkForDeadThreads();
			this.forwardMessage("A new client has entered the server! Current at: " + _clients.size() + " users.");
			
		}
	}
	protected void checkForDeadThreads() {
		ArrayList<ClientThread>dead = new ArrayList<ClientThread>();
		for(ClientThread c: _clients){
			if(c.isAlive()!=true){
				dead.add(c);
			}
		}
		System.out.println("Killed: " + dead.size() + " Threads.");
		for(ClientThread d: dead){
			_clients.remove(d);
		}
		dead.clear();
	}
	public static void main(String[]args){
		try {
			new ServerMulti();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void forwardMessage(String line, long threadID) {
		for(ClientThread c: _clients){
			if(threadID != c.getId()){
				c.sendMessage(line);
			}
		}
	}
	private void forwardMessage(String string) {
		forwardMessage(string, -1); //threads cannot have the ID of -1
	}
	protected String getUsers() {
		return ""+_clients.size();
	}
}
