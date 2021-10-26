package dev.demeng.rankgrantplus.util;

import dev.demeng.pluginbase.chat.Placeholders;
import dev.demeng.pluginbase.menu.layout.Menu;
import dev.demeng.rankgrantplus.RankGrantPlus;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Utility for building a new menu from config.
 */
public class ConfigMenu extends Menu {

  private static final String BASE_PATH = "menus.";

  @Getter(AccessLevel.PROTECTED) private final String path;

  /**
   * Creates a new menu from config.
   *
   * @param i    The instance of the main class
   * @param name The name of the menu in settings.yml
   */
  protected ConfigMenu(RankGrantPlus i, String name, Placeholders titlePlaceholders) {
    super(i.getSettings().getInt(BASE_PATH + name + ".size"),
        titlePlaceholders.set(
            Objects.requireNonNull(i.getSettings().getString(BASE_PATH + name + ".title"),
                "Menu title is null: " + name)));

    this.path = BASE_PATH + name + ".";
  }
}
