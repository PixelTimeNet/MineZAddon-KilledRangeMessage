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

import java.io.File;
import java.io.IOException;

public class MainConfiguration {

    private final Main main;
    private double radius;
    private boolean useRadius;
    private String message;

    public MainConfiguration(Main main) {
        this.main = main;
    }

    public void reload() {
        if(!main.getDataFolder().exists())
            main.getDataFolder().mkdirs();
        File config = new File(main.getDataFolder(), "config.yml");
        if(!config.exists()) {
            main.getConfig().addDefault("radius", radius = 150.0d);
            main.getConfig().addDefault("useRadius", useRadius = true);
            main.getConfig().addDefault("message", message = "$killer 使用 [$item] 杀死了 $player.");
            main.getConfig().options().copyDefaults(true);
            try {
                main.getConfig().save(config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            main.reloadConfig();
            radius = main.getConfig().getDouble("radius", 150.0d);
            useRadius = main.getConfig().getBoolean("useRadius", true);
            message = main.getConfig().getString("message", "$killer 使用 [$item] 杀死了 $player.");
        }
    }

    public boolean isUseRadius() {
        return useRadius;
    }

    public double getRadius() {
        return radius;
    }

    public String getMessage() {
        return message;
    }
}
