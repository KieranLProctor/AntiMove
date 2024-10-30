package uk.geren.antiMove;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class FrozenPlayer {
    public Player player;
    public LocalDateTime startDate;
    public LocalDateTime endDate;

    public FrozenPlayer(Player player, LocalDateTime startDate, LocalDateTime endDate) {
        this.player = player;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static boolean removePlayer(ArrayList<FrozenPlayer> frozenPlayers, Player player) {
        return frozenPlayers.removeIf(frozenPlayer -> frozenPlayer.player.getUniqueId() == player.getUniqueId());
    }

    public static boolean addPlayer(ArrayList<FrozenPlayer> frozenPlayers, Player player, LocalDateTime endDate) {
        return frozenPlayers.add(new FrozenPlayer(player, LocalDateTime.now(), endDate));
    }
}

