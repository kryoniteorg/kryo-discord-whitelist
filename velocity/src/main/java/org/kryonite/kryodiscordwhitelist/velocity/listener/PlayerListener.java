package org.kryonite.kryodiscordwhitelist.velocity.listener;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
public class PlayerListener {

  private final UserRepository userRepository;

  @Subscribe
  public void onPlayerLogin(LoginEvent event) {
    Player player = event.getPlayer();

    try {
      if (userRepository.get(player.getUniqueId()).isPresent()) {
        return;
      }

      if (!userRepository.updateIfPresent(player.getUniqueId(), player.getUsername())) {
        denyLogin(event);
      }
    } catch (SQLException exception) {
      log.error("Failed to get or update player", exception);
      denyLogin(event);
    }
  }

  private void denyLogin(LoginEvent event) {
    event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Du bist nicht auf der Whitelist!")));
  }
}
