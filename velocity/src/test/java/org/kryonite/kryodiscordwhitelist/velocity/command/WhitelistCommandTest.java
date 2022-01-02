package org.kryonite.kryodiscordwhitelist.velocity.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.UserRepository;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WhitelistCommandTest {

  @InjectMocks
  private WhitelistCommand testee;

  @Mock
  private UserRepository userRepository;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProxyServer server;

  @Test
  void shouldSendUsage() {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {});

    // Act
    testee.execute(invocation);

    // Assert
    verifyNoInteractions(userRepository);
    verify(invocation.source()).sendMessage(any());
  }

  @Test
  void shouldAddPlayerToWhitelist() throws SQLException {
    // Arrange
    String minecraftName = "Testee";

    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"add", minecraftName});
    when(userRepository.addIfNotPresent(any())).thenReturn(true);

    // Act
    testee.execute(invocation);

    // Assert
    verify(userRepository).addIfNotPresent(minecraftName);
  }

  @Test
  void shouldHandleExceptions_WhenTryingToAddPlayer() throws SQLException {
    // Arrange
    String minecraftName = "Testee";

    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"add", minecraftName});
    when(userRepository.addIfNotPresent(any())).thenThrow(SQLException.class);

    // Act
    testee.execute(invocation);

    // Assert
    verify(userRepository).addIfNotPresent(minecraftName);
    verify(invocation.source()).sendMessage(any());
  }

  @Test
  void shouldRemovePlayerFromWhitelist() throws SQLException {
    // Arrange
    String minecraftName = "Testee";

    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"remove", minecraftName});
    when(userRepository.removeUser(any())).thenReturn(true);
    when(server.getPlayer(anyString())).thenReturn(Optional.of(mock(Player.class)));

    // Act
    testee.execute(invocation);

    // Assert
    verify(userRepository).removeUser(minecraftName);
  }

  @Test
  void shouldHandleExceptions_WhenTryingToRemovePlayer() throws SQLException {
    // Arrange
    String minecraftName = "Testee";

    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"remove", minecraftName});
    when(userRepository.removeUser(any())).thenThrow(SQLException.class);

    // Act
    testee.execute(invocation);

    // Assert
    verify(userRepository).removeUser(minecraftName);
    verify(invocation.source()).sendMessage(any());
  }
}
