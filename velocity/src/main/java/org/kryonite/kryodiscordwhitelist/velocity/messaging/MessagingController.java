package org.kryonite.kryodiscordwhitelist.velocity.messaging;

import com.rabbitmq.client.BuiltinExchangeType;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.kryonite.kryodiscordwhitelist.velocity.messaging.consumer.PlayerRemovedFromWhitelistConsumer;
import org.kryonite.kryodiscordwhitelist.velocity.messaging.message.PlayerRemovedFromWhitelist;
import org.kryonite.kryomessaging.api.MessagingService;
import org.kryonite.kryomessaging.service.message.Message;

@RequiredArgsConstructor
public class MessagingController {

  protected static final String PLAYER_REMOVED_FROM_WHITELIST_EXCHANGE = "whitelist_player_removed";

  private final MessagingService messagingService;
  private final ProxyServer server;

  public void setupPlayerRemovedFromWhitelist() throws IOException {
    messagingService.setupExchange(PLAYER_REMOVED_FROM_WHITELIST_EXCHANGE, BuiltinExchangeType.FANOUT);

    String queue = UUID.randomUUID().toString();
    messagingService.bindQueueToExchange(queue, PLAYER_REMOVED_FROM_WHITELIST_EXCHANGE);

    messagingService.startConsuming(queue, new PlayerRemovedFromWhitelistConsumer(server),
        PlayerRemovedFromWhitelist.class);
  }

  public void sendRemovedPlayerFromWhitelist(String minecraftName) {
    PlayerRemovedFromWhitelist playerRemovedFromWhitelist = new PlayerRemovedFromWhitelist(minecraftName);
    messagingService.sendMessage(Message.create(PLAYER_REMOVED_FROM_WHITELIST_EXCHANGE, playerRemovedFromWhitelist));
  }
}
