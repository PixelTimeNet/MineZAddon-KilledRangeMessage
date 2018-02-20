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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin {

    private MainConfiguration configuration;

    public Main() {
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        if(!setupMoonLake()) {
            this.getLogger().log(Level.SEVERE, "前置月色之湖核心 API 插件加载失败.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.configuration = new MainConfiguration(this);
        this.configuration.reload();
        this.getServer().getPluginManager().registerEvents(new MainListener(this), this);
        this.getLogger().info("击杀范围消息 KilledRangeMessage 插件 v" + getDescription().getVersion() + " 成功加载.");
    }

    @Override
    public void onDisable() {
        this.configuration = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("moonlake.krm.use")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
            printlnHelp(sender);
        } else if(args[0].equalsIgnoreCase("reload")) {
            getConfiguration().reload();
            sender.sendMessage(ChatColor.GOLD + "[KRM] " + ChatColor.GREEN + "The configuration file has been reloaded...");
        } else {
            printlnHelp(sender);
        }
        return true;
    }

    private void printlnHelp(CommandSender sender) {
        sender.sendMessage(new String[] {
                ChatColor.AQUA.toString() + ChatColor.STRIKETHROUGH + "     " +
                ChatColor.GOLD + "KilledRangeMessage by lgou2w v" + getDescription().getVersion() +
                ChatColor.AQUA.toString() + ChatColor.STRIKETHROUGH + "     ",
                "/krm help - View plugin command help.",
                "/krm reload - Reloaded plugin configuration file."
        });
    }

    public MainConfiguration getConfiguration() {
        return configuration;
    }

    private boolean setupMoonLake() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("MoonLake");
        return plugin != null && plugin.isEnabled();
    }
}
