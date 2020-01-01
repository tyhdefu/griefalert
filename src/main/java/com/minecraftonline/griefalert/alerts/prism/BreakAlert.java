/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.GriefEvents;

public class BreakAlert extends PrismAlert {

  BreakAlert(GriefProfile griefProfile, PrismRecordArchived prismRecord) {
    super(griefProfile, prismRecord);
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.BREAK;
  }

}
