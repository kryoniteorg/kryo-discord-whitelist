package org.kryonite.kryodiscordwhitelist.velocity.listener;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryodiscordwhitelist.common.persistence.entity.User;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.UserRepository;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerListenerTest {

  @InjectMocks
  private PlayerListener testee;

  @Mock
  private UserRepository userRepositoryMock;

  @Test
  void shouldLoginSuccessfulWhenUuidIsPresent() throws SQLException {
    // Arrange
    UUID playerUniqueId = UUID.randomUUID();

    Player player = mock(Player.class, Answers.RETURNS_DEEP_STUBS);
    when(player.getUniqueId()).thenReturn(playerUniqueId);
    when(userRepositoryMock.get(any())).thenReturn(Optional.of(User.create(123L, "Test", playerUniqueId)));

    LoginEvent loginEvent = new LoginEvent(player);

    // Act
    testee.onPlayerLogin(loginEvent);

    // Assert
    assertTrue(loginEvent.getResult().isAllowed(), "Result should be allowed");
    verify(userRepositoryMock).get(playerUniqueId);
  }

  @Test
  void shouldLoginSuccessfulWhenUuidNotPresentButNamePresent() throws SQLException {
    // Arrange
    UUID playerUniqueId = UUID.randomUUID();
    String playerName = "Testee";

    Player player = mock(Player.class, Answers.RETURNS_DEEP_STUBS);
    when(player.getUniqueId()).thenReturn(playerUniqueId);
    when(player.getUsername()).thenReturn(playerName);
    when(userRepositoryMock.get(any())).thenReturn(Optional.empty());
    when(userRepositoryMock.updateIfPresent(any(), any())).thenReturn(true);

    LoginEvent loginEvent = new LoginEvent(player);

    // Act
    testee.onPlayerLogin(loginEvent);

    // Assert
    assertTrue(loginEvent.getResult().isAllowed(), "Result should be allowed");
    verify(userRepositoryMock, atLeastOnce()).get(playerUniqueId);
    verify(userRepositoryMock).updateIfPresent(playerUniqueId, playerName);
  }

  @Test
  void shouldDenyLoginWhenUuidAndMinecraftNameNotPresent() throws SQLException {
    // Arrange
    UUID playerUniqueId = UUID.randomUUID();
    String playerName = "Testee";

    Player player = mock(Player.class, Answers.RETURNS_DEEP_STUBS);
    when(player.getUniqueId()).thenReturn(playerUniqueId);
    when(player.getUsername()).thenReturn(playerName);
    when(userRepositoryMock.get(any())).thenReturn(Optional.empty());
    when(userRepositoryMock.updateIfPresent(any(), any())).thenReturn(false);

    LoginEvent loginEvent = new LoginEvent(player);

    // Act
    testee.onPlayerLogin(loginEvent);

    // Assert
    assertFalse(loginEvent.getResult().isAllowed(), "Result should be denied");
    verify(userRepositoryMock).get(playerUniqueId);
    verify(userRepositoryMock).updateIfPresent(playerUniqueId, playerName);
  }

  @Test
  void shouldDenyLoginOnException() throws SQLException {
    // Arrange
    UUID playerUniqueId = UUID.randomUUID();

    Player player = mock(Player.class, Answers.RETURNS_DEEP_STUBS);
    when(player.getUniqueId()).thenReturn(playerUniqueId);
    when(userRepositoryMock.get(any())).thenThrow(SQLException.class);

    LoginEvent loginEvent = new LoginEvent(player);

    // Act
    testee.onPlayerLogin(loginEvent);

    // Assert
    assertFalse(loginEvent.getResult().isAllowed(), "Result should be denied");
    verify(userRepositoryMock).get(playerUniqueId);
  }
}
