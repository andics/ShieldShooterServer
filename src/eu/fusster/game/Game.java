package eu.fusster.game;

import static eu.fusster.toolkit.Utils.append;
import static eu.fusster.toolkit.Utils.getPlayers;
import static eu.fusster.toolkit.Utils.isDebbuging;
import static eu.fusster.toolkit.Utils.removePlayer;
import static eu.fusster.toolkit.Utils.sendVariables;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import eu.fusster.player.Player;
import eu.fusster.toolkit.Utils;
import eu.fusster.toolkit.Variables;

public class Game {
	
	private static boolean isOver = false;

	private static LinkedList<Player> alivePlayers = new LinkedList<Player>();
	
	private static int waitPlayers, roundDelay, roundCount = 0;

	private static Semaphore sem = new Semaphore(0);

	private static void waitPlayers() throws InterruptedException {
		// Start a timer ticking every second
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (getPlayers().size() < Variables.allVariables.get("MIN_PLAYERS")) {
					append("Waiting for players to connect", new Color(153,0,51), true);
				} else {
					// Wait for additional players
					append("[" + (Variables.allVariables.get("WAIT_FOR_PLAYERS") - waitPlayers)
							+ "] Waiting for additional players to connect",
							new Color(153,0,51), true);
					waitPlayers++;
					if (waitPlayers >= Variables.allVariables.get("WAIT_FOR_PLAYERS")) {
						waitPlayers = 0;
						sem.release();
						super.cancel();
					}
				}
			}
		}, 0, 1000);
		sem.acquire();
	}

	private static void waitRoundDelay() throws InterruptedException {
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {

				boolean everyoneActed = true;
				for (Player p : alivePlayers) {
					if (!p.hasActed())
						everyoneActed = false;
				}

				boolean roundDelayOver = false;
				if (roundDelay >= Variables.allVariables.get("ROUND_DELAY")) {
					roundDelayOver = true;
				}

				if (!roundDelayOver) {
					append("[ " + (Variables.allVariables.get("ROUND_DELAY") - roundDelay)
							+ " ] Waiting round delay", new Color(153,0,51), true);
					roundDelay++;
				} else {
					roundDelay = 0;
				}

				if (roundDelayOver || everyoneActed) {
					roundDelay = 0;
					super.cancel();
					sem.release();
				}
			}
		}, 0, 1000);
		sem.acquire();
	}

	public static void start() {

		alivePlayers.addAll(getPlayers());

		new Thread(new Runnable() {
			public void run() {
				try {
					append("Starting a full game", Color.cyan, true);
					// Wait for minimum players to connect
					waitPlayers();
					Variables.init();
					sendVariables();
					
					// Add players
					StringBuffer str = new StringBuffer();
					for (Player p : alivePlayers) {
						p.setInGame(true);
						str.append(p.getName() + " ,");
					}
					append("Game starting with " + alivePlayers.size()
							+ " players[ " + str + " ]", Color.green);
					isOver=false;
					while (!isOver) {
						try {
							// Do every round
							Utils.append("Round " + roundCount + " is Starting");
							Utils.sendAll("msg:newRound");
							Utils.sendInfo();
							// Wait Round Delay
							waitRoundDelay();
							// Execute round
							alivePlayers = execute(alivePlayers);

							Utils.sendMoves();
							roundCount++;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// Game Over
					gameOver();

				} catch (Exception e) {
					append("[ERROR] " + e.getMessage(), Color.RED);
				}
			}
		}).start();

	}

	public static void gameOver() {
		Utils.append("Game Over", Color.BLUE, false);
		waitPlayers = 0; roundDelay = 0;  roundCount = 0;
		
		for (Player p : alivePlayers){
			p.reset();
		}
		
		Runtime.getRuntime().gc();
	}
	
	public static int getRoundCount() {
		return roundCount;
	}

	public static synchronized void poke() {
		for (Player p : alivePlayers) {
			if (!getPlayers().contains(p))
				alivePlayers.remove(p);
		}
	}

	public static LinkedList<Player> execute(LinkedList<Player> alivePlayers) {
		Game.alivePlayers = alivePlayers;
		prepare();
		checkShooting();
		addAmmo();
		removeDeadPlayers();
		cleanUp();
		return alivePlayers;
	}

	private static void prepare() {
		if (alivePlayers.size() < 1.5) {
			isOver = true;
		} else {
			// Removes players who didn't move
			for (Player p : alivePlayers) {
				if (!p.hasActed()) {
					p.setDead(true);
				}
			}
		}
	}

	private static void checkShooting() {
		for (Player p : alivePlayers) {
			if (p.isShooting()) {
				if (p.getTarget().isDefending()) {
					Utils.send(p, "msg:" + p.getTarget().getName() + " used shield and dodged your atack.");
					if (isDebbuging()) {
						append(p.getName() + " dodged "
								+ p.getTarget().getName() + "'s attack.");
					}
				} else {
					if (p.getAmmo() >= 1) {
						p.shoot(p.getTarget());
					} else {
						if (isDebbuging()) {
							append(p.getName()
									+ " tried to shoot but didn't have ammo.");
						}
					}
				}
			}
		}
	}

	private static void addAmmo() {
		for (Player p : alivePlayers) {
			if (p.isReloading()) {
				if (p.isDead())
					continue;
				p.addAmmo();
				if (isDebbuging()) {
					append(p.getName() + " added 1 ammo and now has "
							+ p.getAmmo() + " ammo.");
				}
			}
		}
	}

	private static void removeDeadPlayers() {
		for (Player p : alivePlayers) {
			if (p.isDead()) {
				alivePlayers.remove(p);
				removePlayer(p, "Disconnected");
				p.reset();
				if (p.getKiller()!=null)
				append(p.getKiller().getName() + " brutaly raped "
						+ p.getName() + " .");
			}
		}
	}

	private static void cleanUp() {
		for (Player p : alivePlayers) {
			p.setActed(false);

			p.setReloading(false);
			p.setDefending(false);
			p.setShooting(false);
			
		}
	}

}
