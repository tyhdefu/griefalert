/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertReloadCommand extends AbstractCommand {

  public GriefAlertReloadCommand() {
    super(Permissions.GRIEFALERT_COMMAND_RELOAD, Text.of(
        "Reload all Grief Profiles from host"
    ));
    addAlias("reload");
  }

  @NonnullByDefault
  @Override
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    GriefAlert.getInstance().reload();
    src.sendMessage(Format.success("Grief Alert reloaded!"));
    return CommandResult.success();
  }

}
