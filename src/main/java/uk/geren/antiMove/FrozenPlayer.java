package uk.geren.antiMove;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class FrozenPlayer {
    private Player player;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public FrozenPlayer(Player player, LocalDateTime startDate, LocalDateTime endDate) {
        this.player = player;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Player getPlayer() {
        return this.player;
    }

    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    public LocalDateTime getEndDate() {
        return this.endDate;
    }
}

