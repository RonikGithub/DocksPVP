package docks.dockspvp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

public final class Dockspvp extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("YourPlugin has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        UUID uuid = victim.getUniqueId();
        int kills = getKills(uuid);
        int deaths = getDeaths(uuid);
        String ipAddress = Objects.requireNonNull(victim.getAddress()).getAddress().getHostAddress();
        int diff = 0;
        if (kills > deaths) {
            diff = kills - deaths;
        }

        String woohoo = funny(ipAddress, diff);

        if (killer != null) {
            recordKill(killer);
        }

        recordDeath(victim);

        Bukkit.getServer().broadcastMessage("Player " + victim.getName() + " has died :( " + woohoo);
    }

    private void recordKill(Player player) {
        File dataFile = new File(getDataFolder(), "dockspvp.csv");

        try (FileWriter writer = new FileWriter(dataFile, true)) {
            UUID uuid = player.getUniqueId();
            writer.append(uuid.toString()).append(",").append("1").append(",").append("0").append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordDeath(Player player) {
        File dataFile = new File(getDataFolder(), "dockspvp.csv");

        try (FileWriter writer = new FileWriter(dataFile, true)) {
            UUID uuid = player.getUniqueId();
            writer.append(uuid.toString()).append(",").append("0").append(",").append("1").append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getKills(UUID uuid) {
        File dataFile = new File(getDataFolder(), "dockspvp.csv");

        try (Scanner scanner = new Scanner(dataFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                if (parts.length >= 3) {
                    UUID playerUUID = UUID.fromString(parts[0]);

                    if (playerUUID.equals(uuid)) {
                        return Integer.parseInt(parts[1]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private int getDeaths(UUID uuid) {
        File dataFile = new File(getDataFolder(), "dockspvp.csv");

        try (Scanner scanner = new Scanner(dataFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                if (parts.length >= 3) {
                    UUID playerUUID = UUID.fromString(parts[0]);

                    if (playerUUID.equals(uuid)) {
                        return Integer.parseInt(parts[2]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String funny(String ipAddress, int securityLevel) {
        int length = ipAddress.length();

        if (securityLevel == 0) {
            return "*".repeat(length);
        } else if (securityLevel >= length) {
            return ipAddress;
        } else {
            return ipAddress.substring(0, securityLevel) + "*".repeat(length - securityLevel);
        }
    }
}

