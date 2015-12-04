package eu.fusster.player;

import java.io.*;
import java.net.Socket;

public class RawPlayer {
	
	protected Socket socket;
	protected BufferedReader buffredReader;
	protected PrintWriter out;
	
	
	public RawPlayer(Socket socket) {
		this.socket = socket;
		try {
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.buffredReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public BufferedReader getDataInputStream() {
		return buffredReader;
	}

	public PrintWriter getPrintWriter() {
		return out;
	}

}
