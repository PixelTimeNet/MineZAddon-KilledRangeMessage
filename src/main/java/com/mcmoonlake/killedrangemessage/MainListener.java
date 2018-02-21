/*
 * Copyright (C) 2016-Present The MoonLake (mcmoonlake@hotmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mcmoonlake.killedrangemessage;

import com.minecraft.moonlake.MoonLakeAPI;
import com.minecraft.moonlake.api.fancy.FancyMessage;
import com.minecraft.moonlake.api.packet.wrapper.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MainListener implements Listener {

    private final Main main;
    private final Set<String> forceOne;

    public MainListener(Main main) {
        this.main = main;
        this.forceOne = Collections.synchronizedSet(new HashSet<String>());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        forceOne.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        forceOne.remove(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if(killer == null)
            return;
        if(forceOne.contains(player.getName())) {
            event.setDeathMessage(null); // clean
            return;
        }
        forceOne.add(player.getName());
        Collection<? extends Player> players;
        if(main.getConfiguration().isUseRadius())
            players = getRangePlayers(killer.getLocation(), main.getConfiguration().getRadius());
        else
            players = Bukkit.getServer().getOnlinePlayers();
        String message = main.getConfiguration().getMessage();
        message = message.replace("$killer", killer.getName());
        message = message.replace("$player", player.getName());
        Object messageFormat = formatMessage(message, killer.getItemInHand());
        if(messageFormat instanceof String) {
            for(Player target : players)
                target.sendMessage(messageFormat.toString());
        } else if(messageFormat instanceof FancyMessage) {
            PacketPlayOutChat packet = new PacketPlayOutChat((FancyMessage) messageFormat);
            packet.send(players.toArray(new Player[players.size()]));
        }
        if(messageFormat != null)
            event.setDeathMessage(null); // clean
    }

    private Object formatMessage(String format, ItemStack weapon) {
        if(!format.contains("$item"))
            return ChatColor.translateAlternateColorCodes('&', format);
        if(weapon == null || weapon.getType() == Material.AIR)
            return ChatColor.translateAlternateColorCodes('&', format.replace("$item", "空气"));
        String[] arr = ChatColor.translateAlternateColorCodes('&', format).split("\\$item");
        FancyMessage fm = MoonLakeAPI.newFancyMessage("");
        FancyMessage item = MoonLakeAPI.newFancyMessage(formatDisplayName(weapon)).tooltip(weapon).color(ChatColor.AQUA);
        for(int i = 0; i < arr.length; i++)
            if(i + 1 != arr.length) fm.then(arr[i]).join(item);
            else fm.then(arr[i]);
        return fm;
    }

    private String formatDisplayName(ItemStack weapon) {
        String displayName = weapon.getItemMeta().hasDisplayName() ? weapon.getItemMeta().getDisplayName() : weapon.getType().name();
        if(weapon.getAmount() > 1)
            displayName += "x" + weapon.getAmount();
        return displayName;
    }

    // notnull
    private List<Player> getRangePlayers(Location location, double radius) {
        radius = Math.max(1.0d, radius);
        double squared = radius * radius;
        List<Player> result = new ArrayList<>();
        for(Player player : location.getWorld().getPlayers()) {
            Location pLocation = player.getLocation();
            if(pLocation.distanceSquared(location) <= squared)
                result.add(player);
        }
        return result;
    }
}
