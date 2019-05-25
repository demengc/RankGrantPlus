package ga.demeng7215.rankgrantplus.utils;

import ga.demeng7215.demapi.api.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class RGPInventory {

    private final UUID uuid;

    private final Inventory inv;
    private final Map<Integer, InvAction> actions;
    private static final Map<UUID, RGPInventory> inventoriesByUUID = new HashMap<>();
    static final Map<UUID, UUID> openInventories = new HashMap<>();

    protected RGPInventory(int size, String title) {

        this.uuid = UUID.randomUUID();

        this.inv = Bukkit.createInventory(null, size, MessageUtils.color(title));

        this.actions = new HashMap<>();
        inventoriesByUUID.put(getUUID(), this);
    }

    public interface InvAction {
        void click(Player player);
    }

    protected void setItem(int slot, ItemStack stack, String name, List<String> lore, InvAction action) {

        ItemMeta meta = stack.getItemMeta();
        List<String> loreList = new ArrayList<>();

        for (String s : lore) {
            loreList.add(MessageUtils.color(s));
        }

        meta.setDisplayName(MessageUtils.color(name));
        meta.setLore(loreList);

        stack.setItemMeta(meta);

        inv.setItem(slot, stack);
        if (action != null) {
            actions.put(slot, action);
        }
    }

    public void open(Player p) {
        p.openInventory(inv);
        openInventories.put(p.getUniqueId(), getUUID());
    }

    private UUID getUUID() {
        return uuid;
    }

    static Map<UUID, RGPInventory> getInventoriesByUUID() {
        return inventoriesByUUID;
    }

    Map<Integer, InvAction> getActions() {
        return actions;
    }
}
