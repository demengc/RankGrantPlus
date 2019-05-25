package ga.demeng7215.rankgrantplus.utils;

import ga.demeng7215.rankgrantplus.inventories.DurationChooseInv;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class RGPInventoryListeners implements Listener {


    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();
        UUID playerUUID = player.getUniqueId();
        UUID inventoryUUID = RGPInventory.openInventories.get(playerUUID);

        if (inventoryUUID != null) {
            e.setCancelled(true);
            RGPInventory gui = RGPInventory.getInventoriesByUUID().get(inventoryUUID);
            RGPInventory.InvAction action = gui.getActions().get(e.getSlot());

            if (action != null) {
                action.click(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent e) {

        Player player = (Player) e.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (Bukkit.getScheduler().isCurrentlyRunning(
                DurationChooseInv.getTaskId())) Bukkit.getScheduler().cancelTask(DurationChooseInv.getTaskId());

        RGPInventory.openInventories.remove(playerUUID);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent e) {

        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (Bukkit.getScheduler().isCurrentlyRunning(
                DurationChooseInv.getTaskId())) Bukkit.getScheduler().cancelTask(DurationChooseInv.getTaskId());

        RGPInventory.openInventories.remove(playerUUID);
    }
}
