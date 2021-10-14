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
