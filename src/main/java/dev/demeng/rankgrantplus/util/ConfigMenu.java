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
