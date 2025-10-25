/*
 * MIT License
 *
 * Copyright (c) 2025 Demeng Chen
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

package dev.demeng.rankgrantplus.commands;

import dev.demeng.pluginbase.lib.lamp.annotation.Command;
import dev.demeng.pluginbase.lib.lamp.annotation.Description;
import dev.demeng.pluginbase.lib.lamp.annotation.Usage;
import dev.demeng.pluginbase.lib.lamp.bukkit.annotation.CommandPermission;
import dev.demeng.rankgrantplus.RankGrantPlus;
import dev.demeng.rankgrantplus.menus.RankSelectMenu;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * The command used for opening the grant GUI.
 */
@RequiredArgsConstructor
public class GrantCmd {

  private final RankGrantPlus i;

  @Command("grant")
  @Description("Grants a player a rank.")
  @CommandPermission("rankgrantplus.grant")
  @Usage("grant <player>")
  public void runDefault(Player sender, OfflinePlayer target) {
    new RankSelectMenu(i, sender, target).open(sender);
  }
}
