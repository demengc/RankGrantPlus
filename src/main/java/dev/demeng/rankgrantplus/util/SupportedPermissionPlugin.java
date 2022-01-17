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

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The registry of all permission plugins that RankGrant+ is able to perform automatic initial setup
 * on.
 */
@RequiredArgsConstructor
@Getter
public enum SupportedPermissionPlugin {

  LUCKPERMS(
      "LuckPerms",
      "lp user %target% parent set %rank%",
      "lp user %target% parent set default"),
  ULTRAPERMISSIONS(
      "UltraPermissions",
      "upc setGroups %target% %rank%",
      "upc setGroups %target% default"),
  PERMISSIONSEX(
      "PermissionsEx",
      "pex user %target% group set %rank%",
      "pex user %target% group set default"),
  GROUPMANAGER(
      "GroupManager",
      "manuadd %target% %rank%",
      "manudel %target%");

  /**
   * The name of the plugin.
   */
  private final String name;
  /**
   * The commands to execute on grant activation.
   */
  private final List<String> activationCommands;
  /**
   * The commands to execute on grant expiration.
   */
  private final List<String> expirationCommands;

  /**
   * Creates a new supported permission plugin with a single command for activation and a single
   * command for expiration.
   *
   * @param name              The name of the plugin
   * @param activationCommand The command to execute on grant activation
   * @param expirationCommand The command to execute on grant expiration
   */
  SupportedPermissionPlugin(
      String name,
      String activationCommand,
      String expirationCommand) {
    this.name = name;
    this.activationCommands = Collections.singletonList(activationCommand);
    this.expirationCommands = Collections.singletonList(expirationCommand);
  }
}
