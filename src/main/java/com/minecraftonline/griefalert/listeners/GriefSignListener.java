package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;

import javax.annotation.Nonnull;
import java.util.Optional;

public class GriefSignListener implements EventListener<ChangeSignEvent> {
    private final AlertTracker tracker;
    private final GriefAlert plugin;

    public GriefSignListener(GriefAlert plugin, AlertTracker tracker) {
    	this.plugin = plugin;
        this.tracker = tracker;
    }

    @Override
    public void handle(@Nonnull ChangeSignEvent event) {
        if (event.getCause().root() instanceof Player) {
            if (GriefAlert.getConfigBoolean("logSignsContent")) {
                Optional<Player> poption = event.getCause().first(Player.class);
                poption.ifPresent(player -> tracker.logSign(player, event.getTargetTile(), event.getText()));
            }
        }
    }
}
