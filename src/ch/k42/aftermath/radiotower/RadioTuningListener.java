package ch.k42.aftermath.radiotower;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
//import sun.net.www.content.text.plain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Thomas on 07.02.14.
 */
public class RadioTuningListener implements Listener{

    private final String LOREITEMRADIO;
    private final Plugin plugin;
    private Map<Player,RadioListener> receivers = new ConcurrentHashMap<Player, RadioListener>();

    public RadioTuningListener(String LOREITEMRADIO, Plugin plugin) {
        this.LOREITEMRADIO = LOREITEMRADIO;
        this.plugin = plugin;
    }

    @EventHandler
    public void itemChange(PlayerItemHeldEvent event){
        int slot = event.getNewSlot();
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(slot);

        if(hasName(item, LOREITEMRADIO)){
            if(receivers.containsKey(player)) return; // already listening
            plugin.getLogger().info("Adding listener");
            RadioListener l = new RadioListener(LOREITEMRADIO, event.getPlayer());
            receivers.put(player,l);
            Bukkit.getPluginManager().registerEvents(l, plugin);
            return;
        }else{
            if(receivers.containsKey(player)){
                plugin.getLogger().info("Removing listener");
                RadioMessageEvent.getHandlerList().unregister(receivers.remove(player));
            }
        }
    }

    private boolean hasName(ItemStack item, String lore){
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta==null) return false;
        String name = itemMeta.getDisplayName();
        if(name==null) return false;
        plugin.getLogger().finest(name + " <=> " + lore + " ?");
        return name.equals(lore);
    }

}
