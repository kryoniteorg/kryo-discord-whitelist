package org.kryonite.kryodiscordwhitelist.velocity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryodiscordwhitelist.velocity.command.WhitelistCommand;
import org.kryonite.kryodiscordwhitelist.velocity.listener.PlayerListener;
import org.kryonite.kryomessaging.api.MessagingService;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KryoDiscordWhitelistPluginTest {

  @InjectMocks
  private KryoDiscordWhitelistPlugin testee;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProxyServer proxyServerMock;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private Connection connection;

  @Mock
  private MessagingService messagingService;

  @Test
  void shouldRegisterListenerAndCommand() {
    // Arrange
    ProxyInitializeEvent proxyInitializeEvent = new ProxyInitializeEvent();

    // Act
    testee.onInitialize(proxyInitializeEvent);

    // Assert
    verify(proxyServerMock.getEventManager()).register(eq(testee), any(PlayerListener.class));
    verify(proxyServerMock.getCommandManager()).register(any(), any(WhitelistCommand.class));
  }

  @Test
  void shouldNotRegisterListener_WhenDatabaseNotAvailable() throws SQLException {
    // Arrange
    ProxyInitializeEvent proxyInitializeEvent = new ProxyInitializeEvent();
    when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);

    // Act
    testee.onInitialize(proxyInitializeEvent);

    // Assert
    verify(proxyServerMock.getEventManager(), never()).register(any(), any());
  }
}
