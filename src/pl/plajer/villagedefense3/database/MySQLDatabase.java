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

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.utils.MessageUtils;

public class MySQLDatabase {

  private MySQLConnectionManager manager;
  private JavaPlugin plugin;

  public MySQLDatabase(JavaPlugin javaPlugin) {
    this.plugin = javaPlugin;
    this.manager = new MySQLConnectionManager(plugin);
    Main.debug("Configuring MySQL connection", System.currentTimeMillis());
    manager.configureConnPool();

    try {
      Connection connection = manager.getConnection();
      if (connection == null) {
        Main.debug("Failed to connect to database", System.currentTimeMillis());
        return;
      }
      connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `playerstats` (\n"
              + "  `UUID` text NOT NULL,\n"
              + "  `name` text NOT NULL DEFAULT 'Unknown Player',\n"
              + "  `kills` int(11) NOT NULL DEFAULT '0',\n"
              + "  `deaths` int(11) NOT NULL DEFAULT '0',\n"
              + "  `highestwave` int(11) NOT NULL DEFAULT '0',\n"
              + "  `gamesplayed` int(11) NOT NULL DEFAULT '0',\n"
              + "  `level` int(11) NOT NULL DEFAULT '0',\n"
              + "  `xp` int(11) NOT NULL DEFAULT '0',\n"
              + "  `orbs` int(11) NOT NULL DEFAULT '0'\n"
              + ");");

      //temporary workaround
      try {
        connection.createStatement().executeUpdate("ALTER TABLE playerstats ADD name text NOT NULL DEFAULT 'Unknown Player'");
      } catch (MySQLSyntaxErrorException e) {
        if (!e.getMessage().contains("Duplicate column name")) {
          e.printStackTrace();
        }
      }
      manager.closeConnection(connection);
    } catch (SQLException e) {
      e.printStackTrace();
      MessageUtils.errorOccured();
      Bukkit.getConsoleSender().sendMessage("Cannot save contents to MySQL database!");
      Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
    }
    // Table exists
  }


  public void executeUpdate(String query) {
    try {
      Connection connection = manager.getConnection();
      Statement statement = connection.createStatement();
      statement.executeUpdate(query);
      manager.closeConnection(connection);
    } catch (SQLException e) {
      plugin.getLogger().warning("Failed to execute update: " + query);
    }

  }

  public ResultSet executeQuery(String query) {
    try {
      Connection connection = manager.getConnection();
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);
      manager.closeConnection(connection);
      return rs;
    } catch (SQLException exception) {
      exception.printStackTrace();
      plugin.getLogger().warning("Failed to execute request: " + query);
      return null;
    }
  }

  public void insertPlayer(String UUID) {
    executeUpdate("INSERT INTO playerstats (UUID,xp) VALUES ('" + UUID + "',0)");
  }

  public void closeDatabase() {
    manager.shutdownConnPool();
  }

  public void addStat(String UUID, String stat, int amount) {
    executeUpdate("UPDATE playerstats SET " + stat + "=" + stat + "+" + amount + " WHERE UUID='" + UUID + "'");
  }

  public void setStat(String UUID, String stat, int number) {
    executeUpdate("UPDATE playerstats SET " + stat + "=" + number + " WHERE UUID='" + UUID + "';");
  }

  public int getStat(String UUID, String stat) {
    ResultSet set = executeQuery("SELECT " + stat + " FROM playerstats WHERE UUID='" + UUID + "'");
    try {
      if (!set.next()) {
        return 0;
      }
      return (set.getInt(1));
    } catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  public Map<UUID, Integer> getColumn(String player) {
    ResultSet set = executeQuery("SELECT UUID, " + player + " FROM playerstats ORDER BY " + player + " ASC;");
    Map<UUID, Integer> column = new LinkedHashMap<>();
    try {
      while (set.next()) {
        column.put(java.util.UUID.fromString(set.getString("UUID")), set.getInt(player));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return column;
  }

}