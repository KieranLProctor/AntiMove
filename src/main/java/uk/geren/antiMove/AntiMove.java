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
import java.util.HashMap;
import java.util.Map;

public final class AntiMove extends JavaPlugin implements Listener {
    // TODO: Move these to localisation.
    private final String prefix = ChatColor.AQUA + "[" + ChatColor.GOLD + ChatColor.BOLD + "MB" + ChatColor.DARK_RED + ChatColor.BOLD + "Freeze" + ChatColor.AQUA + "] ";
    private final String invalidPermissionMessage = prefix + ChatColor.RED + "You do not have permission to use this command!";

    private final Map<Permission, String> permissionsMap = Map.of(Permission.ALL, "freeze.*", Permission.FREEZE, "freeze.freeze", Permission.FREEZE_FOREVER, "freeze.freeze.forever", Permission.UNFREEZE, "freeze.thaw");

    private final Map<String, FrozenPlayer> frozenPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getLogger().info(ChatColor.GREEN + "[" + this.getName() + "]: Enabled");

        // Get frozen players from file.
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(ChatColor.RED + "[" + this.getName() + "]: Disabled");

        // Store frozen players in file.
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check if they are in the frozen file.
        // if they are but time has elapsed => remove.
        // if they should be frozen => add to frozen.
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        // Should remove this from memory because no need to have them there yanno
    }

    public boolean getIsPlayerFrozen(Player player) {
        return frozenPlayers.containsKey(player.getName());
    }

    public boolean freezePlayer(Player player, CommandSender sender, String duration) {
        // Check if the player is frozen is already.
        if (getIsPlayerFrozen(player)) {
            sender.sendMessage(prefix + ChatColor.RED + "This player is already frozen!");
            return false;
        }

        int minutes = Integer.parseInt(duration);
        LocalDateTime endDate = LocalDateTime.now().plusMinutes(minutes);
        FrozenPlayer frozenPlayer = new FrozenPlayer(player, LocalDateTime.now(), endDate);

        // Freeze the player.
        frozenPlayers.put(player.getName(), frozenPlayer);
        if (!getIsPlayerFrozen(player)) {
            sender.sendMessage(prefix + ChatColor.RED + "Something went wrong freezing " + player.getName() + "!");
            return false;
        }

        sender.sendMessage(prefix + ChatColor.GREEN + "You have frozen " + player.getName() + "!");
        player.sendMessage(prefix + ChatColor.GREEN + "You have been frozen by " + sender.getName() + "!");

        return true;
    }

    public boolean unfreezePlayer(Player player, CommandSender sender) {
        // Check if the player is frozen.
        if (!getIsPlayerFrozen(player)) {
            sender.sendMessage(prefix + ChatColor.RED + "This player isn't frozen!");
            return false;
        }

        frozenPlayers.remove(player.getName());
        if (getIsPlayerFrozen(player)) {
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

        // Get the player to perform the action on.
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(prefix + ChatColor.RED + "Couldn't find player " + args[0]);
            return false;
        }

        // Check if the correct number of args have been passed.
        if (commandName.equals("freeze")) {
            if (args.length < 1 || (args.length > 2)) {
                sender.sendMessage(prefix + ChatColor.RED + "Usage: /freeze <player> [minutes]");
                return false;
            }

            // Check if the sender has permission to perform the action.
            if (!sender.hasPermission(permissionsMap.get(Permission.ALL))
                    || !sender.hasPermission(permissionsMap.get(Permission.FREEZE))
                    || !sender.hasPermission(permissionsMap.get(Permission.FREEZE_FOREVER))) {
                sender.sendMessage(invalidPermissionMessage);
                return false;
            }

            if (!sender.hasPermission(permissionsMap.get(Permission.FREEZE)) && args.length == 1) {
                sender.sendMessage(invalidPermissionMessage);
            }

            return freezePlayer(player, sender, args[1]);
        }

        if (commandName.equals("unfreeze")) {
            if (args.length != 1) {
                sender.sendMessage(prefix + ChatColor.RED + "Usage: /unfreeze <player>");
                return false;
            }

            if (!sender.hasPermission(permissionsMap.get(Permission.ALL))
                    || !sender.hasPermission(permissionsMap.get(Permission.UNFREEZE))) {
                sender.sendMessage(invalidPermissionMessage);
                return false;
            }

            return unfreezePlayer(player, sender);
        }

        // Nothing useful to us.
        return false;
    }
}