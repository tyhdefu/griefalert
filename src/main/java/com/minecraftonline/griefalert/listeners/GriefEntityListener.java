package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.core.GriefInstance;
import com.minecraftonline.griefalert.core.GriefAction.GriefType;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.world.DimensionType;

import javax.annotation.Nonnull;
//TODO: PietElite: Fix
public class GriefEntityListener implements EventListener<InteractEntityEvent> {
	
	/** The main plugin object. */
    private final GriefAlert plugin;

    /**
     * The generic constructor.
     * @param plugin The main plugin object
     */
    public GriefEntityListener(GriefAlert plugin) {
    	this.plugin = plugin;
    }

    @Override
    public void handle(@Nonnull InteractEntityEvent event) {
    	// Make sure the event was caused by a player
    	// TODO Simplify
        if (event.getCause().root() instanceof Player) {
            Entity target = event.getTargetEntity();
            DimensionType dType = target.getLocation().getExtent().getDimension().getType();
            if (target instanceof Hanging) {
                if (event instanceof InteractEntityEvent.Primary) {
                    if (event.getCause().first(Player.class).isPresent()) {
                        Player player = event.getCause().first(Player.class).get();
                        String blockID = target instanceof Painting ? "minecraft:painting" : target instanceof ItemFrame ? "minecraft:item_frame" : "minecraft:leash_knot";
                        if (plugin.isGriefAction(GriefType.INTERACTED, blockID, dType)) {
                            GriefInstance instance = new GriefInstance(plugin.getGriefAction(GriefType.INTERACTED, blockID, dType),
                            		target,
                            		player);
                            if (!instance.isDenied()) {
                                plugin.getRealtimeGriefInstanceManager().processGriefInstance(instance);
                            } else {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            } else if (target instanceof ArmorStand) {
                Player player = event.getCause().first(Player.class).get();
                String blockID = "minecraft:armor_stand";
                if (event instanceof InteractEntityEvent.Primary) {
                    if (plugin.isGriefAction(GriefType.INTERACTED, blockID, dType)) {
                    	GriefInstance instance = new GriefInstance(plugin.getGriefAction(GriefType.INTERACTED, blockID, dType),
                    			target,
                    			player);
                        if (!instance.isDenied()) {
                            plugin.getRealtimeGriefInstanceManager().processGriefInstance(instance);
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
