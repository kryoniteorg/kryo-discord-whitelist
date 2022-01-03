package org.kryonite.kryodiscordwhitelist.velocity.messaging.consumer;

import com.velocitypowered.api.proxy.ProxyServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.kryonite.kryodiscordwhitelist.velocity.messaging.message.PlayerRemovedFromWhitelist;
import org.kryonite.kryomessaging.service.message.Message;
import org.kryonite.kryomessaging.service.message.MessageCallback;

@Slf4j
@RequiredArgsConstructor
public class PlayerRemovedFromWhitelistConsumer implements MessageCallback<PlayerRemovedFromWhitelist> {

  private final ProxyServer server;

  @Override
  public void messageReceived(Message<PlayerRemovedFromWhitelist> message) {
    server.getPlayer(message.getBody().getMinecraftName())
        .ifPresent(value -> value.disconnect(Component.text("Du wurdest von der Whitelist entfernt!")));
  }
}
