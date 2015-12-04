package eu.fusster.player;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import eu.fusster.toolkit.Utils;
import eu.fusster.toolkit.Variables;

public class Player extends RawPlayer{

	private String name;
	private int ammo;

	private int shieldsInARow;

	private boolean isDead;
	private boolean acted;
	private boolean isInited;

	private boolean isDefending;
	private boolean isReloading;
	private boolean isShooting;
	private boolean inGame;

	private Player killer;
	private Player target;
	
	private int toatlTimesShot = 0;
	private int totalTimesReloaded = 0;
	private int totalTimesDefended = 0;
	
	private List<String> pplKilled = new ArrayList<String>();
	
	public Player(Socket s, String name) {
		super(s);
		this.name = name;
		this.ammo = 0;
		this.inGame = false;
		setDead(false);
		setKiller(null);
		setTarget(null);
		isInited = true;
	}
	public void shoot(Player target) {
			target.setDead(true);
			target.setKiller(this);
			pplKilled.add(target.getName());
	}
	public boolean isInited(){
		return isInited;
	}
	public String getName() {
		return name;
	}
	public int getAmmo() {
		return ammo;
	}
	public void addAmmo(){
		if (this.ammo > Variables.allVariables.get("MAX_AMMO")) return;
		this.ammo++;
	}
	public boolean isDead() {
		return isDead;
	}
	public void setDead(boolean dead) {
		this.isDead = dead;
	}
	public Player getKiller() {
		return killer;
	}
	public void setKiller(Player killer) {
		this.killer = killer;
	}
	public boolean hasActed() {
		return acted;
	}
	public void setActed(boolean acted) {
		if(acted) Utils.append(getName() + " has been market as acted.");
		this.acted = acted;
	}
	public boolean isDefending() {
		return isDefending;
	}
	public void setDefending(boolean isDefending) {
		if(isDefending) totalTimesDefended++;
		this.isDefending = isDefending;
	}
	public boolean isReloading() {
		return isReloading;
	}
	public void setReloading(boolean isRealoding) {
		if (isRealoding) totalTimesReloaded++;
		this.isReloading = isRealoding;
	}
	public boolean isShooting() {
		return isShooting;
	}
	public void setShooting(boolean isShooting) {
		if(isShooting) toatlTimesShot++;
		this.isShooting = isShooting;
	}
	public Player getTarget() {
		return target;
	}
	public void setTarget(Player target) {
		this.target = target;
	}
	public InetAddress getIP(){
		return socket.getInetAddress();
	}
	public boolean isInGame() {
		return inGame;
	}
	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}
	public int getShieldsInARow() {
		return shieldsInARow;
	}
	public int getToatlTimesShot() {
		return toatlTimesShot;
	}
	public int getTotalTimesReloaded() {
		return totalTimesReloaded;
	}
	public int getTotalTimesDefended() {
		return totalTimesDefended;
	}
	public List<String> pplKilled(){
		return pplKilled;
	}
	public void reset(){
		this.ammo = 0;
		this.inGame = false;
		setDead(false);
		setActed(false);
		setKiller(null);
		setTarget(null);
		setDefending(false);
		setShooting(false);
		setReloading(false);
		
		toatlTimesShot = 0;
		totalTimesReloaded = 0;
		totalTimesDefended = 0;
		
	}

}
