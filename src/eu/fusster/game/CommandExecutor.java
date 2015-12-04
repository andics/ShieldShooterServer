package eu.fusster.game;

import static eu.fusster.toolkit.Utils.append;
import static eu.fusster.toolkit.Utils.getPlayer;
import static eu.fusster.toolkit.Utils.getPlayers;
import static eu.fusster.toolkit.Utils.removePlayer;
import static eu.fusster.toolkit.Utils.sendInfo;

import java.awt.Color;
import java.net.Socket;

import eu.fusster.ServerApp;
import eu.fusster.player.Player;
import eu.fusster.toolkit.Utils;

public class CommandExecutor {

	public static final int CLIENT_COMMAND = 0;
	public static final int SERVER_COMMAND = 1;
	
	public static void execute(int type, String command, Socket s) {
		if (command != null) {
			if (type == CLIENT_COMMAND) {
				
				Player p = getPlayer(s);
				
				if (!p.hasActed()){
					if (command.startsWith("shoot:")) {
						p.setTarget(getPlayer(command.substring(6)));
						p.setShooting(true);
						p.setActed(true);
					} else if (command.equals("shield")){
						p.setDefending(true);
						p.setActed(true);
					} else if (command.equals("reload")){
						p.setReloading(true);
						p.setActed(true);
					} else {
						Utils.append(s.getInetAddress().toString()
								+ " send unkown command \"" + command + "\" .",
								Color.RED, true);
					}
				}
				
			} else if (type == SERVER_COMMAND) {

				switch (command) {

				case "cls":
					ServerApp.getServerUI().getConsoleArea().setText("");
					break;

				case "list":
					append("Currently online: ");
					for (Player p : getPlayers()) {
						append(p.getName());
					}
					break;

				case "start":
					Game.start();
					break;

				case "sendInfo":
					Utils.append("Sending player info", Color.GREEN, true);
					sendInfo();
					break;

				default:
					if (command.startsWith("kick")) {
						removePlayer(getPlayer(command.substring(5)), "Kicked by Console.");
						break;
					}
					append("Command not found.", Color.RED);
					break;
				}

			}
		}
	}
}
