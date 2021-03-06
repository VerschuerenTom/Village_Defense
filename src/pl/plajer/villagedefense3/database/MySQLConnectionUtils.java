/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.villagedefense3.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.user.User;
import pl.plajer.villagedefense3.user.UserManager;
import pl.plajer.villagedefense3.utils.MessageUtils;

/**
 * @author Plajer
 * <p>
 * Created at 18 lis 2017
 */
public class MySQLConnectionUtils {

  public static void loadPlayerStats(Player player, Main plugin) {
    boolean b = false;
    MySQLDatabase database = plugin.getMySQLDatabase();
    ResultSet resultSet = database.executeQuery("SELECT UUID from playerstats WHERE UUID='" + player.getUniqueId().toString() + "'");
    try {
      if (!resultSet.next()) {
        database.insertPlayer(player.getUniqueId().toString());
        b = true;
      }

      int gamesplayed;
      int zombiekills;
      int highestwave;
      int deaths;
      int xp;
      int level;
      int orbs;
      gamesplayed = database.getStat(player.getUniqueId().toString(), "gamesplayed");
      zombiekills = database.getStat(player.getUniqueId().toString(), "kills");
      highestwave = database.getStat(player.getUniqueId().toString(), "highestwave");
      deaths = database.getStat(player.getUniqueId().toString(), "deaths");
      xp = database.getStat(player.getUniqueId().toString(), "xp");
      level = database.getStat(player.getUniqueId().toString(), "level");
      orbs = database.getStat(player.getUniqueId().toString(), "orbs");
      User user = UserManager.getUser(player.getUniqueId());
      user.setInt("gamesplayed", gamesplayed);
      user.setInt("kills", zombiekills);
      user.setInt("highestwave", highestwave);
      user.setInt("deaths", deaths);
      user.setInt("xp", xp);
      user.setInt("level", level);
      user.setInt("orbs", orbs);
      b = true;
    } catch (SQLException e1) {
      System.out.print("CONNECTION FAILED FOR PLAYER " + player.getName());
      e1.printStackTrace();
      MessageUtils.errorOccured();
      Bukkit.getConsoleSender().sendMessage("Cannot save contents to MySQL database!");
      Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
    }
    if (!b) {
      try {
        if (!resultSet.next()) {
          database.insertPlayer(player.getUniqueId().toString());
        }

        int gamesplayed;
        int zombiekills;
        int highestwave;
        int deaths;
        int xp;
        int level;
        int orbs;
        gamesplayed = database.getStat(player.getUniqueId().toString(), "gamesplayed");
        zombiekills = database.getStat(player.getUniqueId().toString(), "kills");
        highestwave = database.getStat(player.getUniqueId().toString(), "highestwave");
        deaths = database.getStat(player.getUniqueId().toString(), "deaths");
        xp = database.getStat(player.getUniqueId().toString(), "xp");
        level = database.getStat(player.getUniqueId().toString(), "level");
        orbs = database.getStat(player.getUniqueId().toString(), "orbs");
        User user = UserManager.getUser(player.getUniqueId());
        user.setInt("gamesplayed", gamesplayed);
        user.setInt("kills", zombiekills);
        user.setInt("highestwave", highestwave);
        user.setInt("deaths", deaths);
        user.setInt("xp", xp);
        user.setInt("level", level);
        user.setInt("orbs", orbs);
      } catch (SQLException e1) {
        System.out.print("CONNECTION FAILED TWICE FOR PLAYER " + player.getName());
        e1.printStackTrace();
        MessageUtils.errorOccured();
        Bukkit.getConsoleSender().sendMessage("Cannot save contents to MySQL database!");
        Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
      }
    }
  }

}
