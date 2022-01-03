package org.kryonite.kryodiscordwhitelist.velocity.messaging.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryodiscordwhitelist.velocity.messaging.message.PlayerRemovedFromWhitelist;
import org.kryonite.kryomessaging.service.message.Message;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerRemovedFromWhitelistConsumerTest {

  @InjectMocks
  private PlayerRemovedFromWhitelistConsumer testee;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProxyServer server;

  @Test
  void shouldDisconnectPlayerIfPresent() {
    // Arrange
    String minecraftName = "Tom";
    Message<PlayerRemovedFromWhitelist> message = Message.create("test", new PlayerRemovedFromWhitelist(minecraftName));
    Player player = mock(Player.class);
    when(server.getPlayer(minecraftName)).thenReturn(Optional.of(player));

    // Act
    testee.messageReceived(message);

    // Assert
    verify(player).disconnect(any());
  }
}
