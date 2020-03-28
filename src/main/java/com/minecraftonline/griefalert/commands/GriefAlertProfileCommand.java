/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.enums.GriefEvents;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import com.minecraftonline.griefalert.util.enums.Permissions;

import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class GriefAlertProfileCommand extends AbstractCommand {

  GriefAlertProfileCommand() {
    super(Permissions.GRIEFALERT_COMMAND_PROFILE,
        Text.of("Perform alterations to the profiles used for flagging alerts. Event types: ",
    Text.joinWith(
        Format.bonus(", "),
        GriefEvents.REGISTRY_MODULE.getAll()
            .stream()
            .map(griefEvent ->
                Format.hover(griefEvent.getId(), griefEvent.getDescription()))
            .collect(Collectors.toList()))));
    addAlias("profile");
    addAlias("p");
    addChild(new AddCommand());
    addChild(new RemoveCommand());
    addChild(new CountCommand());
    addChild(new ListCommand());
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
    sendHelp(src);
    return CommandResult.success();
  }

  public static class AddCommand extends AbstractCommand {

    AddCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Add a profile to the database."));
      addAlias("add");
      addAlias("a");
      setCommandElement(GenericArguments.seq(
          GenericArguments.catalogedElement(Text.of("event"), GriefEvent.class),
          GenericArguments.string(Text.of("target")),
          GenericArguments.flags()
              .valueFlag(GenericArguments.dimension(
                  Text.of("dimension")),
                  "-ignore", "i")
              .valueFlag(GenericArguments.catalogedElement(
                  Text.of("event_color"), TextColor.class),
                  "-event_color")
              .valueFlag(GenericArguments.catalogedElement(
                  Text.of("target_color"), TextColor.class),
                  "-target_color")
              .valueFlag(GenericArguments.catalogedElement(
                  Text.of("dimension_color"), TextColor.class),
                  "-dimension_color")
              .buildWith(GenericArguments.none())));
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
      DataContainer dataContainer = DataContainer.createNew();

      dataContainer.set(
          GriefProfileDataQueries.EVENT,
          ((GriefEvent) args.getOne("event").get()).getId());
      dataContainer.set(
          GriefProfileDataQueries.TARGET,
          args.getOne("target").map((s) -> General.ensureIdFormat((String) s)).get());

      if (args.getOne("event_color").isPresent()) {
        dataContainer.set(GriefProfileDataQueries.EVENT_COLOR,
            args.getOne("event_color").get());
      }

      if (args.getOne("target_color").isPresent()) {
        dataContainer.set(GriefProfileDataQueries.TARGET_COLOR,
            args.getOne("target_color").get());
      }

      if (args.getOne("dimension_color").isPresent()) {
        dataContainer.set(GriefProfileDataQueries.DIMENSION_COLOR,
            args.getOne("dimension_color").get());
      }

      dataContainer.set(GriefProfileDataQueries.IGNORED,
          Lists.newArrayList(args.getAll("dimension")));

      GriefProfile toAdd = GriefProfile.of(dataContainer);
      if (!toAdd.isValid()) {
        src.sendMessage(Format.error("It looks like you're missing some necessary components!"));
        return CommandResult.empty();
      }

      try {
        if (GriefAlert.getInstance().getProfileStorage().write(toAdd)) {
          src.sendMessage(Format.success("GriefProfile added"));
          GriefAlert.getInstance().getProfileCache().reload();
          return CommandResult.success();
        } else {
          src.sendMessage(Format.error("GriefProfile addition failed. "
              + "Maybe this format already exists?"));
          return CommandResult.empty();
        }
      } catch (Exception e) {
        GriefAlert.getInstance().getLogger().error("An Exception thrown when trying to "
            + "add a profile: "
            + toAdd.toContainer().getValues(true).toString());
        General.printStackTraceToDebugLogger(e);
        return CommandResult.empty();
      }

    }

  }

  public static class RemoveCommand extends AbstractCommand {

    RemoveCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Remove a profile from the database"));
      addAlias("remove");
      addAlias("r");
      setCommandElement(GenericArguments.seq(
          GenericArguments.catalogedElement(Text.of("event"), GriefEvent.class),
          GenericArguments.string(Text.of("target"))));
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {

      GriefEvent griefEvent = (GriefEvent) args.getOne("event").get();
      String target = args.getOne("target").map((s) -> General.ensureIdFormat((String) s)).get();

      try {
        if (GriefAlert.getInstance().getProfileStorage().remove(griefEvent, target)) {
          GriefAlert.getInstance().getProfileCache().reload();
          src.sendMessage(Format.success("Removed a Grief Profile"));
          return CommandResult.success();
        } else {
          src.sendMessage(Format.error("No Grief Profile was found with those parameters"));
          return CommandResult.empty();
        }
      } catch (Exception e) {
        GriefAlert.getInstance().getLogger().error("An Exception was thrown when trying to "
            + "remove a profile: "
            + griefEvent.getId() + ", "
            + target);
        General.printStackTraceToDebugLogger(e);
        return CommandResult.empty();
      }

    }

  }

  public static class CountCommand extends AbstractCommand {

    CountCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of(
          "Count how many profiles there are in use"));
      addAlias("count");
      addAlias("c");
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {

      src.sendMessage(Format.success(String.format(
          "There are %s Grief Profiles in use",
          GriefAlert.getInstance().getProfileCache().getProfiles().size())));
      return CommandResult.success();

    }

  }

  public static class ListCommand extends AbstractCommand {

    ListCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("List every profile in use"));
      addAlias("list");
      addAlias("l");
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
      if (src instanceof ConsoleSource) {

        ConsoleSource console = (ConsoleSource) src;
        console.sendMessage(Format.heading("=== Grief Profiles ==="));
        for (GriefProfile profile : GriefAlert.getInstance().getProfileCache().getProfiles()) {
          console.sendMessage(Format.bonus(profile.toContainer().getValues(true).toString()));
        }
      } else {
        PaginationList.builder()
            .header(Text.of(TextColors.YELLOW, "Grief Profiles"))
            .padding(Text.of(TextColors.GRAY, "="))
            .contents(GriefAlert.getInstance()
                .getProfileCache()
                .getProfiles()
                .stream()
                .map(profile -> Text.of(
                    Format.bonus(profile.toContainer().getValues(true))))
                .collect(Collectors.toList()))
            .footer(Text.of(TextColors.YELLOW, "Formatting for list command is in progress"))
            .build()
            .sendTo(src);
      }
      return CommandResult.success();
    }
  }

}
