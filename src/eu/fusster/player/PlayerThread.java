package eu.fusster.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.fusster.toolkit.Utils;

public class PlayerThread extends Thread {

	private RawPlayer rp;

	private List<PlayerInputListener> listeners = new ArrayList<PlayerInputListener>();

	public PlayerThread(RawPlayer rawPlayer) {
		this.rp = rawPlayer;
	}

	@Override
	public synchronized void start() {
		super.start();
		while (!rp.getSocket().isClosed()) {
			try {
				String str = rp.getDataInputStream().readLine();
				if (str != null) {
					str.trim();
					for (PlayerInputListener l : listeners)
						l.onInputRecieve(rp.getSocket(), str);
				}
			} catch (IOException e) {
				Utils.append(e.getMessage(), java.awt.Color.RED, true);
				Utils.removePlayer(Utils.getPlayer(rp.getSocket()), "Disconnected");
			}
		}
	}

	public void add(PlayerInputListener pl) {
		listeners.add(pl);
	}

	public RawPlayer getRawPlayer() {
		return rp;
	}

}
