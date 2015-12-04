package eu.fusster.toolkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public final class Variables {
	
	private static File configFile; 
	
	public static HashMap<String, Integer> allVariables;
	
	private Variables(){}
	
	public static void init(){
		
		File f = new File(System.getProperty("java.class.path"));
		File dir = f.getAbsoluteFile().getParentFile();
		String path = dir.toString();
		
		configFile = new File(path + "/config.fuss");
		
		allVariables = new HashMap<String, Integer>();
		// Put default values
	//	allVariables.put("MIN_PLAYERS", 2);
	//	allVariables.put("MAX_PLAYERS", 8);
		allVariables.put("ROUND_DELAY", 45);
		allVariables.put("WAIT_FOR_PLAYERS", 10);
		allVariables.put("MAX_AMMO", 2);
		allVariables.put("MAX_SHIELDS_IN_A_ROW", 3);
		allVariables.put("START_AMMO", 0);
		// Read config
		readConfig();
		
		for (String s : allVariables.keySet()){
			Utils.append("Setting value for " + s + " at " + allVariables.get(s), new java.awt.Color(10,10,230));
		}
	}
	
	private static void readConfig(){
		if(configFile.exists()){
			Utils.append("Config file exists");
			try {
				FileReader fr = new FileReader(configFile);
				BufferedReader br = new BufferedReader(fr);
				String line;
				while((line = br.readLine()) != null){
					if (line.startsWith("//")) continue;
					String[] str = line.split(":");
					allVariables.put(str[0], Integer.parseInt(str[1]));
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else { 
			createDefaultConfig();
		}
	}
	
	private static void createDefaultConfig(){
		try {
			configFile.createNewFile();
			
			FileWriter fw = new FileWriter(configFile);
			fw.write("// Default Generated Config" + System.lineSeparator());
			for (String s : allVariables.keySet()){
				fw.write(s +":" + allVariables.get(s) + System.lineSeparator());
			}
			
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
