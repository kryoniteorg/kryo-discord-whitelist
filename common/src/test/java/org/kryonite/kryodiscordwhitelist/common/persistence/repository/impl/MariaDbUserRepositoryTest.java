package org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryodiscordwhitelist.common.persistence.entity.User;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MariaDbUserRepositoryTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private Connection connection;

  @Test
  void shouldCreateTableOnStartup() throws SQLException {
    // Act
    new MariaDbUserRepository(connection);

    // Assert
    verify(connection).prepareStatement(MariaDbUserRepository.CREATE_USER_TABLE);
  }

  @Test
  void shouldSaveUser() throws SQLException {
    // Arrange
    User user = User.create(123456L, "Test", null);
    MariaDbUserRepository testee = new MariaDbUserRepository(connection);

    // Act
    testee.save(user);

    // Assert
    verify(connection).prepareStatement(MariaDbUserRepository.INSERT_USER);
    verify(connection.prepareStatement(anyString())).setLong(1, user.getDiscordId());
    verify(connection.prepareStatement(anyString())).setString(2, user.getMinecraftName());
    verify(connection.prepareStatement(anyString())).setString(3, user.getMinecraftName());
  }

  @Test
  void shouldGetUser() throws SQLException {
    // Arrange
    UUID minecraftUuid = UUID.randomUUID();
    MariaDbUserRepository testee = new MariaDbUserRepository(connection);
    when(connection.prepareStatement(MariaDbUserRepository.GET_USER).executeQuery().first()).thenReturn(true);
    when(connection.prepareStatement(MariaDbUserRepository.GET_USER).executeQuery().getString(anyString()))
        .thenReturn(minecraftUuid.toString());

    // Act
    Optional<User> result = testee.get(minecraftUuid);

    // Assert
    assertTrue(result.isPresent(), "User was not present");
    assertEquals(minecraftUuid, result.get().getMinecraftUuid(), "Minecraft uuid did not match");
    verify(connection, atLeastOnce()).prepareStatement(MariaDbUserRepository.GET_USER);
    verify(connection.prepareStatement(anyString())).setString(1, minecraftUuid.toString());
  }

  @Test
  void shouldReturnEmptyOptional_WhenUserNotFound() throws SQLException {
    // Arrange
    UUID minecraftUuid = UUID.randomUUID();
    MariaDbUserRepository testee = new MariaDbUserRepository(connection);
    when(connection.prepareStatement(MariaDbUserRepository.GET_USER).executeQuery().first()).thenReturn(false);

    // Act
    Optional<User> result = testee.get(minecraftUuid);

    // Assert
    assertTrue(result.isEmpty(), "User was present but was not expected");
    verify(connection, atLeastOnce()).prepareStatement(MariaDbUserRepository.GET_USER);
    verify(connection.prepareStatement(anyString())).setString(1, minecraftUuid.toString());
  }

  @Test
  void shouldUpdateUser() throws SQLException {
    // Arrange
    UUID minecraftUuid = UUID.randomUUID();
    String minecraftName = "Testee";
    MariaDbUserRepository testee = new MariaDbUserRepository(connection);
    when(connection.prepareStatement(MariaDbUserRepository.UPDATE_USER).executeUpdate()).thenReturn(1);

    // Act
    boolean result = testee.updateIfPresent(minecraftUuid, minecraftName);

    // Assert
    assertTrue(result, "Result was not true");
    verify(connection, atLeastOnce()).prepareStatement(MariaDbUserRepository.UPDATE_USER);
    verify(connection.prepareStatement(anyString())).setString(1, minecraftUuid.toString());
    verify(connection.prepareStatement(anyString())).setString(2, minecraftName);
  }

  @Test
  void shouldReturnFalse_WhenUserNotPresent() throws SQLException {
    // Arrange
    UUID minecraftUuid = UUID.randomUUID();
    String minecraftName = "Testee";
    MariaDbUserRepository testee = new MariaDbUserRepository(connection);
    when(connection.prepareStatement(MariaDbUserRepository.UPDATE_USER).executeUpdate()).thenReturn(0);

    // Act
    boolean result = testee.updateIfPresent(minecraftUuid, minecraftName);

    // Assert
    assertFalse(result, "Result was true");
    verify(connection, atLeastOnce()).prepareStatement(MariaDbUserRepository.UPDATE_USER);
    verify(connection.prepareStatement(anyString())).setString(1, minecraftUuid.toString());
    verify(connection.prepareStatement(anyString())).setString(2, minecraftName);
  }
}
