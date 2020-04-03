/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.alerts.GeneralAlert;
import com.minecraftonline.griefalert.api.commands.GeneralCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.util.enums.Permissions;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;


public class InfoCommand extends GeneralCommand {

  InfoCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_INFO,
        Text.of("Get itemized information about an Alert")
    );
    addAlias("info");
    addAlias("i");
    setCommandElement(GenericArguments.onlyOne(
        GenericArguments.integer(CommandKeys.ALERT_INDEX.get())));
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {
    args.<Integer>getOne(CommandKeys.ALERT_INDEX.get()).ifPresent(index -> {
      try {
        Alert alert = GriefAlert.getInstance().getAlertManager().getAlertCache().get(index);
        if (alert instanceof GeneralAlert) {
          GeneralAlert generalAlert = (GeneralAlert) alert;
          PaginationList.builder()
              .title(Text.of(
                  TextColors.YELLOW,
                  "Alert ",
                  Format.bonus(alert.getCacheIndex()), " Info"))
              .padding(Format.bonus("="))
              .contents(generalAlert.getDetails().stream()
                  .map(detail -> detail.get(generalAlert))
                  .flatMap(optional -> optional.map(Stream::of).orElseGet(Stream::empty))
                  .map(text -> Text.of(Format.GRIEF_ALERT_THEME, " - ", TextColors.RESET, text))
                  .collect(Collectors.toList()))
              .build()
              .sendTo(src);
        } else {
          src.sendMessage(Format.heading("Alert Info: ", Format.bonus(alert.getCacheIndex())));
          src.sendMessage(alert.getSummary());
        }
      } catch (IndexOutOfBoundsException e) {
        src.sendMessage(Format.error("That alert could not be found."));
      }
    });
    return CommandResult.success();
  }
}
