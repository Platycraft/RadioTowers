package ch.k42.aftermath.radiotower;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by Thomas on 07.02.14.
 */
public class Minions {
    private static final char obfuscate0 = ChatColor.WHITE.getChar();
    private static final char obfuscate1 = ChatColor.GRAY.getChar();
    private static final char obfuscate2 = ChatColor.DARK_GRAY.getChar();

    private static final void appendLvl0(StringBuffer buf, char c){
        buf.append(ChatColor.COLOR_CHAR).append(obfuscate0).append(c);
    }
    private static final void appendLvl1(StringBuffer buf, char c){
        buf.append(ChatColor.COLOR_CHAR).append(obfuscate1).append(c);
    }
    private static final void appendLvl2(StringBuffer buf, char c){
        buf.append(ChatColor.COLOR_CHAR).append(obfuscate2).append(c);
    }
    private static final void appendLvl3(StringBuffer buf, char c){
        buf.append(' ');
    }


    /**
     * Obfuscates a message depending on a scale
     * @param message message to obfuscate
     * @param scale a scale from 0 (nearly no obfuscation) to 1 (nearly fully obfuscated), higher values are possible
     * @return an obfuscated message
     */
    public static final String obfuscateMessage(String message,double scale){
        if(scale>=1.5) return "";
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        double mean = 3*scale;
        double sigma = 0.5;

        for(int i=0;i<message.length();i++){
            int obsfucation =0;

            obsfucation = (int) (r.nextGaussian()*sigma+mean);
            if(obsfucation<0)
                obsfucation =0;
            else if (obsfucation>3)
                obsfucation=3;

            switch (obsfucation){
                case 0:
                    appendLvl0(sb,message.charAt(i));
                    break;
                case 1:
                    appendLvl1(sb,message.charAt(i));
                    break;
                case 2:
                    appendLvl2(sb,message.charAt(i));
                    break;
                case 3:
                    appendLvl3(sb,message.charAt(i));
                    break;
            }
        }
        return sb.toString();
    }

    public static final byte[] MAGIC_SALT = {(byte) 0xDE,(byte) 0xAD,(byte) 0xBE,(byte) 0xEF};
    public static final int cryptoHashToInt(int value){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update((byte) value);
            digest.update((byte) (value >>> 8));
            digest.update((byte) (value >>> 16));
            digest.update((byte) (value >>> 24));
            digest.update(MAGIC_SALT);
            byte[] bytes = digest.digest();
            int ret = (bytes[0] << 24) | (bytes[1] << 16) | (bytes[2] << 8) | (bytes[3]);
            return Math.abs(ret);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Hashing failed: " + e.getMessage());
            return value;
        }
    }

    public static final String frequencyToString(double frequency){
        if(frequency>=1000000){ // GHz range?
            frequency /= 1000000;
            return String.format("%1.0fGHz",frequency);
        }else if(frequency>=1000){ // MHz range
            frequency /= 1000;
            return String.format("%1.0fMHz",frequency);
        }else{ // Hz range
            return String.format("%1.0fHz",frequency);
        }
    }

    public static final String powerToString(double power){
        if(power<=0) return "0dBm";

        double db =(Math.log10(1000*power));
        return String.format("%1.2fdBm",db);
    }

    private static final Material RADIO = Material.COMPASS;
    public static final boolean isNamedRadio(ItemStack item, String LOREITEMRADIO){
        if(item==null) return false;
        if(!item.getType().equals(RADIO)) return false;
        ItemMeta meta = item.getItemMeta();
        if(meta==null) return false;
        String name = meta.getDisplayName();
        if(name==null) return false;
        if(!name.equals(LOREITEMRADIO)) return false;
        return true;

    }

    public static final boolean hasName(ItemStack item, String lore){
        if(item==null) return false;
        ItemMeta meta = item.getItemMeta();
        if(meta==null) return false;
        String name = meta.getDisplayName();
        if(name==null) return false;
        if(!name.equals(lore)) return false;
        return true;
    }
}
