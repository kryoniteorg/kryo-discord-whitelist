package org.kryonite.kryodiscordwhitelist.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.UserRepository;
import org.kryonite.kryodiscordwhitelist.velocity.messaging.MessagingController;

@Slf4j
@RequiredArgsConstructor
public class WhitelistCommand implements SimpleCommand {

  protected static final String PERMISSION = "whitelist";
  protected static final String[] ARGS_OPTIONS = {"add", "remove"};

  private final UserRepository userRepository;
  private final MessagingController messagingController;
  private final ProxyServer server;

  @Override
  public void execute(Invocation invocation) {
    CommandSource source = invocation.source();

    String[] arguments = invocation.arguments();
    if (arguments.length < 2) {
      sendUsage(source);
      return;
    }

    String minecraftName = arguments[1];
    if (arguments[0].equals(ARGS_OPTIONS[0])) {
      addUser(source, minecraftName);
    } else if (arguments[0].equals(ARGS_OPTIONS[1])) {
      removeUser(source, minecraftName);
    } else {
      sendUsage(source);
    }
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    String[] arguments = invocation.arguments();

    if (arguments.length == 0) {
      return List.of(ARGS_OPTIONS);
    }

    if (arguments.length == 1) {
      return Stream.of(ARGS_OPTIONS)
          .filter(argument -> argument.contains(arguments[0]))
          .toList();
    }

    if (arguments.length == 2) {
      return server.getAllPlayers().stream()
          .map(player -> player.getGameProfile().getName())
          .filter(playerName -> playerName.contains(arguments[1]))
          .toList();
    }

    return Collections.emptyList();
  }

  @Override
  public boolean hasPermission(Invocation invocation) {
    return invocation.source().hasPermission(PERMISSION);
  }

  private void sendUsage(CommandSource source) {
    source.sendMessage(Component.text("/whitelist " + ARGS_OPTIONS[0] + " <name>").color(NamedTextColor.AQUA)
        .append(Component.newline())
        .append(Component.text("/whitelist " + ARGS_OPTIONS[1] + " <name>").color(NamedTextColor.AQUA)));
  }

  private void addUser(CommandSource source, String minecraftName) {
    try {
      boolean successful = userRepository.addIfNotPresent(minecraftName);
      if (successful) {
        source.sendMessage(Component.text("Added user " + minecraftName + " to the whitelist."));
      } else {
        source.sendMessage(Component.text("User " + minecraftName + " is already on the whitelist."));
      }
    } catch (SQLException e) {
      log.error("Failed to add user {}", minecraftName);
      source.sendMessage(Component.text("Could not add user " + minecraftName + "!"));
    }
  }

  private void removeUser(CommandSource source, String minecraftName) {
    try {
      boolean successful = userRepository.removeUser(minecraftName);
      if (successful) {
        source.sendMessage(Component.text("Removed user " + minecraftName + " from the whitelist."));
        messagingController.sendRemovedPlayerFromWhitelist(minecraftName);
      } else {
        source.sendMessage(Component.text("User " + minecraftName + " wasn't on the whitelist."));
      }
    } catch (SQLException e) {
      log.error("Failed to remove user {}", minecraftName);
      source.sendMessage(Component.text("Could not remove user " + minecraftName + "!"));
    }
  }
}
