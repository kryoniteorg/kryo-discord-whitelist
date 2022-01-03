package org.kryonite.kryodiscordwhitelist.velocity.messaging;

import static org.kryonite.kryodiscordwhitelist.velocity.messaging.MessagingController.PLAYER_REMOVED_FROM_WHITELIST_EXCHANGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.rabbitmq.client.BuiltinExchangeType;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryodiscordwhitelist.velocity.messaging.consumer.PlayerRemovedFromWhitelistConsumer;
import org.kryonite.kryodiscordwhitelist.velocity.messaging.message.PlayerRemovedFromWhitelist;
import org.kryonite.kryomessaging.api.MessagingService;
import org.kryonite.kryomessaging.service.message.Message;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessagingControllerTest {

  @InjectMocks
  private MessagingController testee;

  @Mock
  private MessagingService messagingService;

  @Mock
  private ProxyServer server;

  @Test
  void shouldSetupExchange() throws IOException {
    // Arrange - Act
    testee.setupPlayerRemovedFromWhitelist();

    // Assert
    verify(messagingService).setupExchange(PLAYER_REMOVED_FROM_WHITELIST_EXCHANGE, BuiltinExchangeType.FANOUT);
    verify(messagingService).bindQueueToExchange(anyString(), eq(PLAYER_REMOVED_FROM_WHITELIST_EXCHANGE));
    verify(messagingService).startConsuming(anyString(), any(PlayerRemovedFromWhitelistConsumer.class),
        eq(PlayerRemovedFromWhitelist.class));
  }

  @Test
  void shouldSendMessage() {
    // Arrange
    String minecraftName = "Test";

    // Act
    testee.sendRemovedPlayerFromWhitelist(minecraftName);

    // Assert
    verify(messagingService).sendMessage(Message.create(PLAYER_REMOVED_FROM_WHITELIST_EXCHANGE,
        new PlayerRemovedFromWhitelist(minecraftName)));
  }
}
