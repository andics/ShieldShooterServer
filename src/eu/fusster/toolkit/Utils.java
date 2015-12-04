package eu.fusster.toolkit;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import eu.fusster.ServerApp;
import eu.fusster.game.Game;
import eu.fusster.player.Player;
import eu.fusster.player.RawPlayer;
import eu.fusster.ui.ServerUI;

public class Utils {

	public static Color darkOvileGreen = new Color(85, 107, 47);
	public static Color darkSeaGreen = new Color(143, 188, 143);

	private static LinkedList<Player> allPlayers = new LinkedList<Player>();

	private static boolean debugging = false;

	public synchronized static void addPlayer(Player p) {
		boolean shouldAdd = true;
		for (Player p1 : allPlayers) {
			if (p1.getName().equals(p.getName()))
				shouldAdd = false;
		}
		if (shouldAdd) {
			getPlayers().add(p);
			append(p.getName() + " ( "
					+ p.getSocket().getInetAddress().toString()
					+ " ) successfully joined the game.");
			updateUsernameArea();
		}
	}

	public static Player getPlayer(String str) {
		for (Player p : getPlayers()) {
			if (p.getName().trim().equals(str.trim()))
				return p;
		}
		return null;
	}

	public static Player toPlayer(RawPlayer rp, String name) {
		Player p = new Player(rp.getSocket(), name);
		addPlayer(p);
		return p;
	}

	public static Player getPlayer(java.net.Socket s) {
		for (Player p : getPlayers()) {
			if (s.equals(p.getSocket())) {
				return p;
			}
		}
		return null;
	}

	public static Player getPlayer(int i) {
		if (i > allPlayers.size())
			return null;
		else
			return allPlayers.get(i);
	}

	public static void send(RawPlayer p, String toSend) {
		p.getPrintWriter().println(toSend);
		if (p instanceof Player)
			Utils.append("Sending \"" + toSend + "\" to " + 
				((Player) p).getName()+ ".", Color.GREEN);
	}

	public static void send(String pName, String toSend) {
		Player p = getPlayer(pName);
		Utils.send(p, toSend);
	}

	public static void sendAll(String toSend) {
		for (Player p : getPlayers())
			Utils.send(p, toSend);
	}

	public static void sendInfo() {
		if (getPlayers().size() < 1.5) {
			return;
		} else {
			StringBuffer toSend = new StringBuffer("reg:");

			for (int i = 0; i < getPlayers().size(); i++) {
				for (int j = 0; j < getPlayers().size(); j++) {
					if (getPlayers().get(i).getName() == getPlayers().get(j)
							.getName()) {
						continue;
					} else if (i == getPlayers().size() - 1) {
						toSend.append(getPlayers().get(j).getName());
					} else {
						toSend.append(getPlayers().get(j).getName() + ",");
					}
				}
			}
			sendAll(toSend.toString());
		}
	}

	public static void sendVariables() {
		for (String s : Variables.allVariables.keySet()) {
			sendAll("var:" + s + "-" + Variables.allVariables.get(s));
		}
	}
	
	public static void sendMoves(){
		for(Player p : getPlayers()){
			if (p.hasActed()){
				if (p.isReloading()){
					sendAll(p.getName() + " defended on the previous turn.");
				} else if (p.isShooting()){
					sendAll(p.getName() + " tryied to shoot " + p.getTarget().getName() + " .");
				} else if (p.isDefending()){
					sendAll(p.getName() + " reloaded on the previous turn");
				}
			}
		}
	}

	public static void removePlayer(Player p, String reason) {
		try {
			if (p != null && p.getSocket() != null){
				if(!p.getSocket().isClosed()){
					p.getPrintWriter().println("close");
					p.getSocket().close();
				}
			}
		} catch (IOException e) {

		}
		getPlayers().remove(p);

		String name = "Unknown";

		if (p.getName() != null) {
			name = p.getName();
		}

		if (reason == null) {
			append(name + " disconnected from the game.", Color.RED);
		} else {
			append(name + " disconnected from the game. Reason: < " + reason
					+ " >.");
		}
		Game.poke();
		updateUsernameArea();
		Runtime.getRuntime().gc();
	}

	public static synchronized void append(String str) {
		append(str, darkOvileGreen);
	}

	public static void error(String msg) {
		append(msg, new Color(178, 34, 34), true);
	}

	public static synchronized void append(String str, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				StyleConstants.Foreground, c);
		ServerUI ui = ServerApp.getServerUI();
		try {
			ui.getConsoleArea()
					.getDocument()
					.insertString(
							ui.getConsoleArea().getDocument().getLength(),
							str + System.lineSeparator(), aset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void append(String str, Color c, boolean requireDebbiging) {
		if (!requireDebbiging) {
			append(str, c);
		} else if (requireDebbiging && debugging) {
			append(str, c);
		}
	}

	@SuppressWarnings("unchecked")
	public static void updateUsernameArea() {
		List<String> names = new ArrayList<String>();
		for (Player p : allPlayers) {
			names.add(p.getName());
		}
		ServerApp.getServerUI().getUsernamePane().setListData(names.toArray());
	}

	public static LinkedList<Player> getPlayers() {
		return allPlayers;
	}

	public static boolean isDebbuging() {
		return debugging;
	}

	public static void setDebbuging(boolean debbuging) {
		Utils.debugging = debbuging;
	}

}
