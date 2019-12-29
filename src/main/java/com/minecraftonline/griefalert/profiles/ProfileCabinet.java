package com.minecraftonline.griefalert.profiles;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.profiles.GriefProfile;
import com.minecraftonline.griefalert.api.profiles.GriefProfileOld;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import com.minecraftonline.griefalert.api.profiles.GriefProfiles;
import com.minecraftonline.griefalert.profiles.io.Exporter;
import com.minecraftonline.griefalert.profiles.io.Importer;
import org.spongepowered.api.world.DimensionType;

public class ProfileCabinet {

  private Table<GriefEvent, String, GriefProfile> storage = HashBasedTable.create();

  /**
   * Constructor for a new museum to hold all Grief Profiles. This museum should be considered
   * immutable, and should only be used as a tool to check possible Grief Events against.
   */
  public ProfileCabinet() {
    this.load();
  }

  /**
   * Load in all data from the Grief Profiles file on the local machine.
   */
  public void reload() {
    storage.clear();
    load();
    GriefAlert.getInstance().getLogger().warn("Grief Profiles were imported to profile museum cache.");
  }

  private void load() {

    // Add all constant GriefProfiles in load as well
    add(GriefProfiles.PLACE_SIGN);
    add(GriefProfiles.BREAK_SIGN);
    // ...

    // Get all other profiles from the onsite profile list
    // importer.retrieve().forEach(this::add);
  }


  public Optional<GriefProfile> getProfileOf(GriefEvent griefEvent, String target, DimensionType dimensionType) {

    Optional<GriefProfile> profileOptional = Optional.ofNullable(storage.get(griefEvent, target));

    // Make sure the dimension is not ignored
    if (profileOptional.isPresent()) {
      if (!profileOptional.get().isIgnoredIn(dimensionType)) {
        return profileOptional;
      } else {
        return Optional.empty();
      }
    }

    return profileOptional;
  }

  /**
   * Add a new Grief Profile to the museum. This will not check if a similar one already exists,
   * so check that it does not before calling this method. An info alert will be sent to the
   * console.
   *
   * @param profile The profile to add
   */
  public boolean add(GriefProfile profile) {
    if (storage.contains(profile.getGriefEvent(), profile.getTarget())) {
      return false;
    } else {
      storage.put(profile.getGriefEvent(), profile.getTarget(), profile);
      return true;
    }
  }

  public void store(GriefProfile profile) throws Exception {
    GriefAlert.getInstance().getProfileStorage().connect();
    GriefAlert.getInstance().getProfileStorage().write(profile);
  }
}
