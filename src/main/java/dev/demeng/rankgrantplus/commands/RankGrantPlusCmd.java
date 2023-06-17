/*
 * MIT License
 *
 * Copyright (c) 2023 Demeng Chen
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
import dev.demeng.pluginbase.text.Text;
import dev.demeng.rankgrantplus.RankGrantPlus;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

/**
 * The main command of RankGrant+.
 */
@RequiredArgsConstructor
@Command({"rankgrantplus", "rgp"})
public class RankGrantPlusCmd {

  private final RankGrantPlus i;

  @DefaultFor({"rankgrantplus", "rgp"})
  @Description("Displays information for RankGrant+.")
  public void runDefault(CommandSender sender) {
    Text.coloredTell(sender, "&a&lRunning RankGrant+ v" + Common.getVersion() + " by Demeng.");
    Text.coloredTell(sender, "&aLink: &fhttps://spigotmc.org/resources/63403/");
    Text.coloredTell(sender, "&6Enjoying RG+? Check out GrantX! &fdemeng.dev/grantx");
  }

  @Subcommand({"reload", "rl"})
  @Description("Reloads configuration files.")
  @CommandPermission("rankgrantplus.reload")
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

    i.updateBaseSettings();

    Text.tell(sender, i.getMessages().getString("reloaded"));
  }

  @Subcommand("import")
  @Description("Imports ranks from your permissions plugin.")
  @CommandPermission("rankgrantplus.import")
  public void runImport(CommandSender sender) {

    int slot = 1;

    for (String group : i.getPermissionHook().getGroups()) {

      if (slot > i.getSettings().getInt("menus.rank-select.size")) {
        break;
      }

      final ConfigurationSection section = i.getRanks().createSection("ranks." + group);
      section.set("slot", slot);
      section.set("material", "BEACON");
      section.set("name", "&2&l" + group);
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

    Text.tell(sender, i.getMessages().getString("imported"));
  }
}
