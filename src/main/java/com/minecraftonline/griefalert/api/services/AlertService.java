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

package com.minecraftonline.griefalert.api.services;

import com.minecraftonline.griefalert.api.alerts.Alert;
import java.util.Collection;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageReceiver;

public interface AlertService {

  enum Sort {
    CHRONOLOGICAL,
    REVERSE_CHRONOLOGICAL,
    INDEX,
    REVERSE_INDEX
  }

  /**
   * Save this alert in a local cache and broadcast it to the necessary in-game players.
   *
   * @param alert the alert to submit
   * @return the index used to retrieve the alert, using @{@link #getAlert(int)}
   */
  int submit(@Nonnull Alert alert);

  /**
   * Retrieve an {@link Alert} by the code which was returned when it was submitted
   * with {@link #submit(Alert)}.
   *
   * @param index the retrieval code
   * @return the corresponding {@link Alert}
   * @throws IllegalArgumentException if the given index is invalid
   */
  @Nonnull
  Alert getAlert(int index) throws IllegalArgumentException;

  /**
   * Send the given {@link Player} to the location of the {@link Alert} found
   * with the given retrieval code. The {@link Player} will receive the necessary
   * information and tools to respond to an {@link Alert}.
   *
   * @param index the {@link Alert} retrieval code
   * @param officer the inspecting player
   * @param force true if the player should teleport to the location, even if it's unsafe
   * @return true if the inspection succeeded
   * @throws IllegalArgumentException if the given index is invalid
   */
  boolean inspect(int index, @Nonnull Player officer, boolean force) throws IllegalArgumentException;

  /**
   * Undo the inspection done by the officer by returning them back to their previous location.
   *
   * @param officer the inspecting player
   * @return true if the player was returned
   */
  boolean unInspect(@Nonnull Player officer);

  /**
   * Clear all information held in the {@link AlertService}.
   */
  void reset();

  /**
   * Give all the receivers all {@link Alert}s which match the filters given in the
   * {@link Request}.
   * @param receivers the receivers of all the information
   * @param filters the holder for all filters
   * @param sort how the {@link Alert}s will be sorted for presentation
   * @param spread true will send all {@link Alert}s individually. False will collapse similar
   *               {@link Alert}s into singular lines.
   */
  void lookup(@Nonnull Collection<MessageReceiver> receivers, @Nonnull Request filters, Sort sort, boolean spread);

}
