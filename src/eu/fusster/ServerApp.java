package eu.fusster;

import eu.fusster.ui.Server;
import eu.fusster.ui.ServerUI;

public class ServerApp {
	
	private static ServerUI serverUI;
	public static boolean offline = false;
	
	public static void main(String[] args) {
		serverUI = new ServerUI();
		Server.start(9876);
	}
	
	public static ServerUI getServerUI(){
		return serverUI;
	}
	
	public static boolean isOffline(){
		return offline;
	}
	
}
