package dev.picheta.tamartp;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.security.SecureRandom;

public final class TamaRTP extends JavaPlugin {
    ArrayList<Material> badBlockList = new ArrayList<>();

    SecureRandom rnd = new SecureRandom();

    int mapRadius = 15000;

    public void onEnable() {
        fillBadBlockList();
    }

    public void onDisable() {}

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            //do nothing, only execute from the console
            //player = (Player)sender;
            return true;

        } else if (sender instanceof org.bukkit.command.ConsoleCommandSender && args.length > 0) {
            player = Bukkit.getServer().getPlayer(args[0]);
        }
        if (label.equalsIgnoreCase("rtp"))
            teleportToCalculatedPosition(player);
        return true;
    }

    public void teleportToCalculatedPosition(Player player) {
        Location finalLocation = generateLocationForPlayer(player);
        int tries = 0;
        while (!isLocationSafe(finalLocation) && tries < 10) {
            finalLocation = generateLocationForPlayer(player);
            tries++;
        }
        if (tries == 10) {
            player.sendMessage(ChatColor.RED + "Couldn't find an appropriate spot");
        } else if (player.getLocation().getWorld().toString().toLowerCase().contains("end") || player.getLocation().getWorld().toString().toLowerCase().contains("nether")) {
            player.sendMessage(ChatColor.RED + "Can only be used in the Overworld!");
        } else {
            player.teleport(finalLocation);
        }
    }

    public void fillBadBlockList() {
        this.badBlockList.add(Material.LAVA);
        this.badBlockList.add(Material.FIRE);
        this.badBlockList.add(Material.WATER);
    }

    public int generateRandomCoordinate() {
        int randomLocation = this.rnd.nextInt(mapRadius*2);
        return -mapRadius + randomLocation;
    }

    public Location generateLocationForPlayer(Player p) {
        double x = generateRandomCoordinate() + 0.5D;
        double z = generateRandomCoordinate() + 0.5D;
        Location location = new Location(p.getWorld(), x, 100.0D, z);
        int y = location.getWorld().getHighestBlockYAt(location);
        location.setY(y + 1.0D);
        return location;
    }

    public boolean isLocationSafe(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        Block below = location.getWorld().getBlockAt(x, y - 1, z);
        return !this.badBlockList.contains(below.getType());
    }
}
