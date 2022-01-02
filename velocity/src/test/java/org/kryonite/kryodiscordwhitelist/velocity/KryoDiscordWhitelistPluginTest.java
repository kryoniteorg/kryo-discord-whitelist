package org.kryonite.kryodiscordwhitelist.velocity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

  @Test
  void shouldNotRegisterListener_WhenDatabaseNotAvailable() {
    // Arrange
    ProxyInitializeEvent proxyInitializeEvent = new ProxyInitializeEvent();

    // Act
    testee.onInitialize(proxyInitializeEvent);

    // Assert
    verify(proxyServerMock.getEventManager(), never()).register(any(), any());
  }
}
