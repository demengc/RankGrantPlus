/*
 * MIT License
 *
 * Copyright (c) 2018-2022 Demeng Chen
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

import dev.demeng.pluginbase.Common;
import dev.demeng.pluginbase.chat.ChatUtils;
import dev.demeng.pluginbase.command.CommandBase;
import dev.demeng.pluginbase.command.annotations.Aliases;
import dev.demeng.pluginbase.command.annotations.Command;
import dev.demeng.pluginbase.command.annotations.Default;
import dev.demeng.pluginbase.command.annotations.Description;
import dev.demeng.pluginbase.command.annotations.Permission;
import dev.demeng.pluginbase.command.annotations.SubCommand;
import dev.demeng.rankgrantplus.RankGrantPlus;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * The main command of RankGrant+.
 */
@RequiredArgsConstructor
@Command("rankgrantplus")
@Aliases({"rgp"})
public class RankGrantPlusCmd extends CommandBase {

  private final RankGrantPlus i;

  @Default
  @Description("Displays information for RankGrant+.")
  public void runDefault(CommandSender sender) {
    ChatUtils.coloredTell(
        sender,
        "&a&lRunning RankGrant+ v" + Common.getVersion() + " by Demeng.",
        "&aLink: &fhttps://spigotmc.org/resources/63403/",
        "&6Enjoying RG+? Check out GrantX! &fdemeng.dev/grantx");
  }

  @SubCommand("reload")
  @Description("Reloads configuration files.")
  @Aliases("rl")
  @Permission("rankgrantplus.reload")
  public void runReload(CommandSender sender) {

    try {
      i.getSettingsFile().reload();
      i.getMessagesFile().reload();
      i.getRanksFile().reload();
      i.getDataFile().reload();
    } catch (IOException | InvalidConfigurationException ex) {
      Common.error(ex, "Failed to reload config files.", false, sender);
      return;
    }

    ChatUtils.tell(sender, i.getMessages().getString("reloaded"));
  }

  @SubCommand("import")
  @Description("Imports ranks from your permissions plugin.")
  @Permission("rankgrantplus.import")
  public void runImport(CommandSender sender) {

    int slot = 1;

    for (String group : i.getPermissionHook().getGroups()) {

      if (slot > i.getSettings().getInt("menus.rank-select.size")) {
        break;
      }

      final ConfigurationSection section = i.getRanks().createSection("ranks." + group);
      section.set("slot", slot);
      section.set("material", "BEACON");
      section.set("display-name", "&2&l" + group);
      section.set("lore",
          Arrays.asList("&7This is an imported rank.",
              "&7You can edit this in ranks.yml.",
              "&0",
              "&a&lClick to select this rank."));
      section.set("permission", "none");

      slot++;
    }

    i.getRanks().set("ranks.example", null);

    try {
      i.getRanksFile().save();
    } catch (IOException ex) {
      Common.error(ex, "Failed to import ranks.", false, sender);
      return;
    }

    ChatUtils.tell(sender, i.getMessages().getString("imported"));
  }
}
