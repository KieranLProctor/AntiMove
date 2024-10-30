package uk.geren.antiMove;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public final class AntiMove extends JavaPlugin implements Listener {
    private final String prefix = ChatColor.AQUA + "[" + ChatColor.GOLD + ChatColor.BOLD + "MB" + ChatColor.DARK_RED + ChatColor.BOLD + "Freeze" + ChatColor.AQUA + "] ";
    private final String invalidPermissionMessage = prefix + ChatColor.RED + "You do not have permission to use this command!";

    private final Map<String, String> permissionsMap = Map.of(
            "all", "freeze.*",
            "freeze", "freeze.freeze",
            "thaw", "freeze.thaw"
    );

    private final ArrayList<FrozenPlayer> frozenPlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + this.getName());

        // Remove from file all players which frozen status has expired.
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(ChatColor.RED + "Disabled " + this.getName());

        // Get frozen players from file
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check if they are in the frozen file.
        // if they are but time has elapsed => remove.
        // if they should be frozen => add to frozen.


    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

    }

    public boolean getIsPlayerFrozen(Player player) {
        return frozenPlayers.contains(player.getName());
    }

    public boolean freezePlayer(Player player, CommandSender sender, String duration) {
        if (!sender.hasPermission(permissionsMap.get("all")) || !sender.hasPermission(permissionsMap.get("freeze"))) {
            sender.sendMessage(invalidPermissionMessage);
            return false;
        }

        // Check if the player exists.
        if (player == null) {
            sender.sendMessage(prefix + ChatColor.RED + "Couldn't find specified player!");
            return false;
        }

        // Check if the player to be frozen is already.
        if (getIsPlayerFrozen(player)) {
            sender.sendMessage(prefix + ChatColor.RED + "This player is already frozen!");
            return false;
        }

        int minutes = Integer.parseInt(duration);
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(minutes);

        // Freeze the player.
        if (!FrozenPlayer.addPlayer(frozenPlayers, player, endDate)) {
            sender.sendMessage(prefix + ChatColor.RED + "Something went wrong freezing " + player.getName() + "!");
            return false;
        }

        sender.sendMessage(prefix + ChatColor.GREEN + "You have frozen " + player.getName() + "!");
        player.sendMessage(prefix + ChatColor.GREEN + "You have been frozen by " + sender.getName() + "!");

        return true;
    }

    public boolean unfreezePlayer(Player player, CommandSender sender) {
        if (!sender.hasPermission(permissionsMap.get("all")) || !sender.hasPermission(permissionsMap.get("unfreeze"))) {
            sender.sendMessage(invalidPermissionMessage);
            return false;
        }

        // Check if the player exists.
        if (player == null) {
            sender.sendMessage(prefix + ChatColor.RED + "Couldn't find specified player!");
            return false;
        }

        // Check if the player to be frozen is already.
        if (!getIsPlayerFrozen(player)) {
            sender.sendMessage(prefix + ChatColor.RED + "This player isn't frozen!");
            return false;
        }

        if (!FrozenPlayer.removePlayer(frozenPlayers, player)) {
            sender.sendMessage(prefix + ChatColor.RED + "Something went wrong unfreezing " + player.getName() + "!");
            return false;
        }

        sender.sendMessage(prefix + ChatColor.GREEN + "You have unfrozen " + player.getName() + "!");
        player.sendMessage(prefix + ChatColor.GREEN + "You have been unfrozen by " + sender.getName() + "!");

        return true;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName = command.getName().toLowerCase();

        // Check if the command is even for us.
        if ((!commandName.equals("freeze")) && (!commandName.equals("unfreeze"))) {
            return false;
        }

        // Check if the correct number of args have been passed.
        if (commandName.equals("freeze")) {
            if (args.length != 2) {
                sender.sendMessage(prefix + ChatColor.RED + "Usage: /freeze <player> [minutes]");
                return false;
            }
        }

        if (commandName.equals("unfreeze")) {
            if (args.length != 1) {
                sender.sendMessage(prefix + ChatColor.RED + "Usage: /unfreeze <player>");
                return false;
            }
        }

        // Get the player to perform the action on.
        Player player = Bukkit.getPlayer(args[0]);

        boolean commandSuccess = false;
        if (commandName.equals("freeze")) {
            commandSuccess = freezePlayer(player, sender, args[1]);
        }

        if (commandName.equals("freeze")) {
            commandSuccess = unfreezePlayer(player, sender);
        }

        return commandSuccess;
    }
}