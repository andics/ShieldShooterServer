package eu.fusster.ui;

import static eu.fusster.toolkit.Utils.*;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import eu.fusster.ServerApp;
import eu.fusster.game.CommandExecutor;
import eu.fusster.player.PlayerInputListener;
import eu.fusster.player.PlayerThread;
import eu.fusster.player.RawPlayer;
import eu.fusster.toolkit.Variables;

public class Server {
	
	static ServerSocket socket;
	static Socket connectionSocket = null;
	
	public static void start(int port){
		try {
			socket = new ServerSocket(port);

			String ip = "Offline";

			try {
				ip = new BufferedReader(new InputStreamReader(new URL("http://ipecho.net/plain").openStream()))
						.readLine();
			} catch (Exception e) {
				ServerApp.offline = true;
			}
			
			java.awt.Color c = new java.awt.Color(10,10,230);
			
			append("Server successfully started.", c);
			append("Server version 0.0.1.2",c);
			append("Server info:",c);
			append("Public IP : " + ip,c);
			append("Port : " + socket.getLocalPort(),c);

			float usedMemBytes = (float) (Runtime.getRuntime().totalMemory() - Runtime
					.getRuntime().freeMemory());
			float usedMemMB = usedMemBytes / (1024 * 1024);
			float usedMemMBrounded = BigDecimal.valueOf(usedMemMB)
					.setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();

			append("Used Memmory : " + usedMemMBrounded + " MB.",c);
			
			accepetConnections();
		} catch (Exception e1) {
			append(e1.getMessage(), Color.RED);
			append("Failed to start on port " + port, Color.RED);
			append("Server is shutting down.", Color.RED);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Runtime.getRuntime().exit(1);
		}
		Variables.init();
	}
	
	private static void accepetConnections(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(!socket.isClosed()){
					try {
						connectionSocket = socket.accept();
						append("New Client.",darkSeaGreen, true);
						waitReg();
					} catch (IOException e) {
						error(e.getMessage());
					}
				}
			}
		}).start();
	}
	
	private static void waitReg(){
		
		new Thread(new Runnable() {
			public void run() {
				PlayerThread pt = new PlayerThread(new RawPlayer(connectionSocket));
				
				send(pt.getRawPlayer(), "connected");
				
				try {
					String str = pt.getRawPlayer().getDataInputStream().readLine();
					if (str.startsWith("reg:") && str!=null){
						toPlayer(pt.getRawPlayer(), str.substring(4).trim());
						finalizePlayer(pt);
					} else {
						pt.getRawPlayer().getSocket().close();
						Runtime.getRuntime().gc();
					}
				} catch (IOException e) {
					error(e.getMessage());
				}
				
			}
		}).start();
		
	}
	
	private static void finalizePlayer(PlayerThread pt){
		pt.add(new PlayerInputListener() {
			@Override
			public void onInputRecieve(Socket socket, String s) {
				append("Recieved:" + s,darkSeaGreen,true);
				append("Sender : " + getPlayer(socket).getName(),darkSeaGreen,true);
				CommandExecutor.execute(CommandExecutor.CLIENT_COMMAND, s, socket);
			}
		});
		pt.start();
	}
	
	public static void stop(){
		try {
			socket.close();
		} catch (IOException e) {
			error(e.getMessage());
		}
	}


}
