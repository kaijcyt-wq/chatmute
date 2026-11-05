package com.yourname.chatmute;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatMutePlugin extends JavaPlugin implements Listener {

    private final Set<String> mutedWorlds = ConcurrentHashMap.newKeySet();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("mutechat")) return false;

        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("mutechat.toggle")) {
            player.sendMessage("§cYou don't have permission to do that.");
            return true;
        }

        String world = player.getWorld().getName();
        if (mutedWorlds.remove(world)) {
            player.sendMessage("§aChat is now unmuted in §e" + world + "§a.");
        } else {
            mutedWorlds.add(world);
            player.sendMessage("§cChat is now muted in §e" + world + "§c.");
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!mutedWorlds.contains(player.getWorld().getName())) return;
        if (player.hasPermission("mutechat.bypass")) return;

        event.setCancelled(true);
        player.sendMessage("§cChat is muted in this world.");
    }
}