/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.GeneralCommand;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.util.enums.Permissions;

import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Class for handling command which shows a hologram displaying information about
 * a player at the location of grief.
 */
class ShowCommand extends GeneralCommand {

  ShowCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_SHOW,
        Text.of("Create a Hologram at the location of grief"));
    addAlias("show");
    addAlias("s");
    setCommandElement(GenericArguments.onlyOne(
        GenericArguments.integer(CommandKeys.ALERT_INDEX.get())));
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
    if (src instanceof Player) {
      Player player = (Player) src;
      if (args.<Integer>getOne(CommandKeys.ALERT_INDEX.get()).isPresent()) {

        try {

          Alert alert = GriefAlert.getInstance()
              .getAlertManager().getAlertCache()
              .get(args.<Integer>getOne(CommandKeys.ALERT_INDEX.get()).get());

          // Create temporary hologram of grief
          GriefAlert.getInstance().getHologramManager().createTemporaryHologram(alert);

          // Broadcast the attempt at command
          Communication.getStaffBroadcastChannel().send(Format.info(
              Format.userName(player),
              " is taking a closer look at alert ",
              CheckCommand.clickToCheck(alert.getCacheIndex())));

          return CommandResult.success();
        } catch (IndexOutOfBoundsException e) {
          player.sendMessage(Format.error("That alert could not be found."));
          return CommandResult.empty();
        }
      } else {
        player.sendMessage(Format.error(Text.of(
            TextColors.RED,
            "The alert code could not be parsed.")));
        return CommandResult.empty();
      }
    } else {
      Errors.sendPlayerOnlyCommand(src);
      return CommandResult.empty();
    }
  }
}
