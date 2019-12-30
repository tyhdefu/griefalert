package com.minecraftonline.griefalert.alerts;

import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.Prism;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public abstract class PrismAlert extends Alert {

  private final DataContainer prismDataContainer;
  private Transform<World> grieferTransform;

  protected PrismAlert(GriefProfile griefProfile, DataContainer prismDataContainer) {
    super(griefProfile);
    this.prismDataContainer = prismDataContainer;

    grieferTransform = null;
    prismDataContainer.getString(DataQueries.Player).ifPresent(
        (s) ->
            grieferTransform = Sponge.getServer()
                .getPlayer(UUID.fromString(s))
                .map(Player::getTransform).orElse(null)
    );
  }

  public static Optional<PrismAlert> of(GriefProfile griefProfile, PrismRecordArchived prismRecord) {
    Optional<String> targetOptional = Prism.getTarget(prismRecord);
    if (!targetOptional.isPresent()) {
      return Optional.empty();
    }

    if (prismRecord.getEvent().equals(PrismEvents.BLOCK_BREAK.getId())) {

      if (targetOptional.get().contains("sign")) {

        // Condition for a SignBreakAlert
        return Optional.of(new SignBreakAlert(griefProfile, prismRecord.getDataContainer()));
      } else {

        // Condition for a BreakAlert
        return Optional.of(new BreakAlert(griefProfile, prismRecord.getDataContainer()));
      }
    } else if (prismRecord.getEvent().equals(PrismEvents.BLOCK_PLACE.getId())) {
      if (targetOptional.get().contains("sign")) {

        // Condition for a SignPlaceAlert
        return Optional.of(new SignPlaceAlert(griefProfile, prismRecord.getDataContainer()));
      } else {

        // Condition for a PlaceAlert
        return Optional.of(new PlaceAlert(griefProfile, prismRecord.getDataContainer()));
      }
    } else if (prismRecord.getEvent().equals(PrismEvents.ENTITY_DEATH.getId())) {

      // Condition for a DeathAlert
      return Optional.of(new DeathAlert(griefProfile, prismRecord.getDataContainer()));
    }

    return Optional.empty();
  }

  @Override
  public Player getGriefer() {
    Optional<String> uuidOptional = prismDataContainer.getString(DataQueries.Player);
    if (!uuidOptional.isPresent()) {
      GriefAlert.getInstance().getLogger().error("Could not get griefer UUID from PrismRecord in a PrismAlert");
      GriefAlert.getInstance().getLogger().error(prismDataContainer.toString());
    }

    Optional<Player> playerOptional = Sponge.getServer().getPlayer(UUID.fromString(uuidOptional.get()));
    if (!playerOptional.isPresent()) {
      GriefAlert.getInstance().getLogger().error("Could not find player using UUID: " + uuidOptional.get());
    }

    return playerOptional.get();
  }

  @Override
  public Optional<Transform<World>> getTransform() {
    return Optional.ofNullable(grieferTransform);
  }

}
