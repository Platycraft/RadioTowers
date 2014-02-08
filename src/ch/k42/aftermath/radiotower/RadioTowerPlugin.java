package ch.k42.aftermath.radiotower;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import sun.net.www.content.text.plain;

public class RadioTowerPlugin extends JavaPlugin
{


        public static final String TOWERS_FILE = "towers.csv";
		protected RadioTowerConfig config;
		private RadioTuningListener radioListener;
        private RadioTowerManager radioTowerManager;
		
		@Override
	    public void onEnable()
		{
			//Creates a Config
			config = new RadioTowerConfig(getConfig());
            if (!hasConfig()) 
            {
				getConfig().options().copyDefaults(true);
				saveConfig();
				
				// Load it again
				config = new RadioTowerConfig(getConfig());
				getLogger().info("Creating default configuration.");
			}



            radioListener = new RadioTuningListener(ChatColor.translateAlternateColorCodes('&', config.getLoreItemRadio()),this);
            RadioTower.setParameters(config.getRTMinHeight(),config.getRTMaxHeight(),config.getRTMaxRange());
            radioTowerManager = new RadioTowerManager(this);

            getLogger().info("Reading towers from disk in file: " + TOWERS_FILE);
            List<Location> towers = readRadioTowersFromFile(TOWERS_FILE);
            for(Location location : towers){
                radioTowerManager.registerTower(location);
            }

            getServer().getPluginManager().registerEvents(radioListener, this);
            getServer().getPluginManager().registerEvents(radioTowerManager, this);
            long time = 20L*config.getRadioCooldown();
            getServer().getScheduler().scheduleSyncRepeatingTask(this,radioTowerManager,100L ,time);

	    }

        @Override
	    public void onDisable()
	    {

	    	Set<Location> towers = radioTowerManager.getTowerLocations();
            getLogger().info("Writing " + towers.size() + " towers to disk in file: " + TOWERS_FILE);
            writeRadioTowersToFile(TOWERS_FILE,towers);
	    }
	    
	    //Checks if a config file exists
	    private boolean hasConfig() {
			File file = new File(getDataFolder(), "config.yml");
			return file.exists();
		}

        private File fromFilename(String filename){
            return new File(getDataFolder().getAbsolutePath()+File.separator+filename);
        }

        public List<Location> readRadioTowersFromFile(String filename){
            File file = fromFilename(filename);

            List<Location> towers = new ArrayList<>();

            try(BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line;

                Location location;
                int x,y,z;

                while (( line = br.readLine()) != null) {
                    if(line.charAt(0)=='#') continue;
                    String[] split = line.split(",");
                    if(split.length<4) continue;
                    World world = Bukkit.getServer().getWorld(split[0]);
                    x = Integer.parseInt(split[1]);
                    y = Integer.parseInt(split[2]);
                    z = Integer.parseInt(split[3]);
                    location = new Location(world,x,y,z);
                    towers.add(location);
                }
                String everything = sb.toString();
            } catch (FileNotFoundException e) {
                getLogger().warning("no input file found!");
            } catch (IOException e) {
                getLogger().warning("can't read input file");
            }

            return towers;
        }

    public boolean writeRadioTowersToFile(String filename,Set<Location> towers){
        File file = fromFilename(filename);

        try {

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            StringBuffer sb;

            bw.write("# contains the location of all radiotowers\n");
            bw.write("# world,X,Y,Z\n");

            for(Location location : towers){
                sb = new StringBuffer();
                sb.append(location.getWorld().getName());
                sb.append(',');
                sb.append(location.getBlockX());
                sb.append(',');
                sb.append(location.getBlockY());
                sb.append(',');
                sb.append(location.getBlockZ());
                sb.append('\n');
                bw.write(sb.toString());
            }

            bw.close();

            return true;
        } catch (IOException e) {
            getLogger().warning("Can't write towers file! " + e.getMessage());
        }
        return false;
	}
}