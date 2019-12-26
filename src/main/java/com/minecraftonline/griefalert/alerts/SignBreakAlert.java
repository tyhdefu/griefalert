package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.util.Format;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class SignBreakAlert extends Alert {

  private final TextColor eventColor = Format.ALERT_ACTION_COLOR;
  private final TextColor targetColor = Format.ALERT_TARGET_COLOR;
  private final TextColor dimensionColor = Format.ALERT_DIMENSION_COLOR;

  SignBreakAlert(int cacheCode) {
    super(cacheCode);
  }

  @Override
  public Text getMessageText() {
    // TODO: Write message text for SignBreakAlert
    return Text.of("SignBreakAlert");
//    return Text.of(
//        defaultColor,
//        getSubject(record), " ",
//        getAction(record), " ",
//        getGriefedObject(record), " in ",
//        getDimension(record), ". ",
//        Format.command(String.valueOf(cacheCode),
//            "/g check " + cacheCode,
//            Text.of("CHECK GRIEF ALERT")
//        ),
//        Prism.getBrokenSignLines(record).map((lines) -> Text.of(
//            TextColors.WHITE,
//            "\n", "Line 1: ", lines.get(0),
//            "\n", "Line 2: ", lines.get(1),
//            "\n", "Line 3: ", lines.get(2),
//            "\n", "Line 4: ", lines.get(3)
//        )).orElse(Text.of("No sign data available"))
//    );
  }

  @Override
  public Transform<World> getTransform() {
    // TODO: Write getTransform
    return null;
  }

}
