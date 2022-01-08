package org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl.MariaDbUserRepository.CREATE_USER_TABLE;
import static org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl.MariaDbUserRepository.DELETE_USER_BY_MINECRAFT_NAME;
import static org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl.MariaDbUserRepository.GET_USER_BY_DISCORD_ID;
import static org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl.MariaDbUserRepository.GET_USER_BY_MINECRAFT_UUID;
import static org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl.MariaDbUserRepository.GET_USER_BY_NAME;
import static org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl.MariaDbUserRepository.INSERT_USER;
import static org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl.MariaDbUserRepository.UPDATE_USER;
import static org.kryonite.kryodiscordwhitelist.common.persistence.repository.impl.MariaDbUserRepository.UPDATE_USER_WHERE_DISCORD_ID;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zaxxer.hikari.HikariDataSource;
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
  private HikariDataSource dataSource;

  @Test
  void shouldCreateTableOnStartup() throws SQLException {
    // Act
    new MariaDbUserRepository(dataSource);

    // Assert
    verify(dataSource.getConnection()).prepareStatement(CREATE_USER_TABLE);
  }

  @Test
  void shouldAddUserIfNotPresent() throws SQLException {
    // Arrange
    String minecraftName = "Testee";

    MariaDbUserRepository testee = new MariaDbUserRepository(dataSource);
    when(dataSource.getConnection().prepareStatement(GET_USER_BY_NAME).executeQuery().first()).thenReturn(false);

    // Act
    boolean result = testee.addIfNotPresent(minecraftName);

    // Assert
    assertTrue(result, "Result should be true");
    verify(dataSource.getConnection(), atLeastOnce()).prepareStatement(GET_USER_BY_NAME);
    verify(dataSource.getConnection().prepareStatement(GET_USER_BY_NAME)).setString(1, minecraftName);

    verify(dataSource.getConnection()).prepareStatement(INSERT_USER);
    verify(dataSource.getConnection().prepareStatement(INSERT_USER)).setLong(1, -1);
    verify(dataSource.getConnection().prepareStatement(INSERT_USER)).setString(2, minecraftName);
  }

  @Test
  void shouldNotAddUserIfAlreadyPresent() throws SQLException {
    // Arrange
    String minecraftName = "Testee";

    MariaDbUserRepository testee = new MariaDbUserRepository(dataSource);
    when(dataSource.getConnection().prepareStatement(GET_USER_BY_NAME).executeQuery().first()).thenReturn(true);

    // Act
    boolean result = testee.addIfNotPresent(minecraftName);

    // Assert
    assertFalse(result, "Result should be false");
    verify(dataSource.getConnection(), atLeastOnce()).prepareStatement(GET_USER_BY_NAME);
    verify(dataSource.getConnection().prepareStatement(GET_USER_BY_NAME)).setString(1, minecraftName);

    verify(dataSource.getConnection(), never()).prepareStatement(INSERT_USER);
  }

  @Test
  void shouldSaveUser() throws SQLException {
    // Arrange
    User user = User.create(123456L, "Test", null);
    MariaDbUserRepository testee = new MariaDbUserRepository(dataSource);
    when(dataSource.getConnection().prepareStatement(GET_USER_BY_DISCORD_ID).executeQuery().first()).thenReturn(false);

    // Act
    testee.save(user);

    // Assert
    verify(dataSource.getConnection(), atLeastOnce()).prepareStatement(GET_USER_BY_DISCORD_ID);
    verify(dataSource.getConnection().prepareStatement(GET_USER_BY_DISCORD_ID)).setLong(1, user.getDiscordId());

    verify(dataSource.getConnection()).prepareStatement(INSERT_USER);
    verify(dataSource.getConnection().prepareStatement(INSERT_USER)).setLong(1, user.getDiscordId());
    verify(dataSource.getConnection().prepareStatement(INSERT_USER)).setString(2, user.getMinecraftName());
  }

  @Test
  void shouldSaveAndUpdateUser_WhenAlreadyPresent() throws SQLException {
    // Arrange
    User user = User.create(123456L, "Test", null);
    MariaDbUserRepository testee = new MariaDbUserRepository(dataSource);
    when(dataSource.getConnection().prepareStatement(GET_USER_BY_DISCORD_ID).executeQuery().first()).thenReturn(true);

    // Act
    testee.save(user);

    // Assert
    verify(dataSource.getConnection(), atLeastOnce()).prepareStatement(GET_USER_BY_DISCORD_ID);
    verify(dataSource.getConnection().prepareStatement(GET_USER_BY_DISCORD_ID)).setLong(1, user.getDiscordId());

    verify(dataSource.getConnection()).prepareStatement(UPDATE_USER_WHERE_DISCORD_ID);
    verify(dataSource.getConnection().prepareStatement(UPDATE_USER_WHERE_DISCORD_ID))
        .setString(1, user.getMinecraftName());
    verify(dataSource.getConnection().prepareStatement(UPDATE_USER_WHERE_DISCORD_ID)).setLong(2, user.getDiscordId());
  }

  @Test
  void shouldRemoveUser() throws SQLException {
    // Arrange
    String minecraftName = "Testee";
    MariaDbUserRepository testee = new MariaDbUserRepository(dataSource);
    when(dataSource.getConnection().prepareStatement(DELETE_USER_BY_MINECRAFT_NAME).executeUpdate()).thenReturn(1);

    // Act
    boolean result = testee.removeUser(minecraftName);

    // Assert
    assertTrue(result, "Result should be true");
    verify(dataSource.getConnection(), atLeastOnce()).prepareStatement(DELETE_USER_BY_MINECRAFT_NAME);
    verify(dataSource.getConnection().prepareStatement(DELETE_USER_BY_MINECRAFT_NAME)).setString(1, minecraftName);
  }

  @Test
  void shouldGetUser() throws SQLException {
    // Arrange
    UUID minecraftUuid = UUID.randomUUID();
    MariaDbUserRepository testee = new MariaDbUserRepository(dataSource);
    when(dataSource.getConnection().prepareStatement(GET_USER_BY_MINECRAFT_UUID).executeQuery().first())
        .thenReturn(true);
    when(dataSource.getConnection().prepareStatement(GET_USER_BY_MINECRAFT_UUID).executeQuery().getString(anyString()))
        .thenReturn(minecraftUuid.toString());

    // Act
    Optional<User> result = testee.get(minecraftUuid);

    // Assert
    assertTrue(result.isPresent(), "User was not present");
    assertEquals(minecraftUuid, result.get().getMinecraftUuid(), "Minecraft uuid did not match");
    verify(dataSource.getConnection(), atLeastOnce()).prepareStatement(GET_USER_BY_MINECRAFT_UUID);
    verify(dataSource.getConnection().prepareStatement(anyString())).setString(1, minecraftUuid.toString());
  }

  @Test
  void shouldReturnEmptyOptional_WhenUserNotFound() throws SQLException {
    // Arrange
    UUID minecraftUuid = UUID.randomUUID();
    MariaDbUserRepository testee = new MariaDbUserRepository(dataSource);
    when(dataSource.getConnection().prepareStatement(GET_USER_BY_MINECRAFT_UUID).executeQuery().first())
        .thenReturn(false);

    // Act
    Optional<User> result = testee.get(minecraftUuid);

    // Assert
    assertTrue(result.isEmpty(), "User was present but was not expected");
    verify(dataSource.getConnection(), atLeastOnce()).prepareStatement(GET_USER_BY_MINECRAFT_UUID);
    verify(dataSource.getConnection().prepareStatement(anyString())).setString(1, minecraftUuid.toString());
  }

  @Test
  void shouldUpdateUser() throws SQLException {
    // Arrange
    UUID minecraftUuid = UUID.randomUUID();
    String minecraftName = "Testee";
    MariaDbUserRepository testee = new MariaDbUserRepository(dataSource);
    when(dataSource.getConnection().prepareStatement(UPDATE_USER).executeUpdate()).thenReturn(1);

    // Act
    boolean result = testee.updateIfPresent(minecraftUuid, minecraftName);

    // Assert
    assertTrue(result, "Result was not true");
    verify(dataSource.getConnection(), atLeastOnce()).prepareStatement(UPDATE_USER);
    verify(dataSource.getConnection().prepareStatement(anyString())).setString(1, minecraftUuid.toString());
    verify(dataSource.getConnection().prepareStatement(anyString())).setString(2, minecraftName);
  }

  @Test
  void shouldReturnFalse_WhenUserNotPresent() throws SQLException {
    // Arrange
    UUID minecraftUuid = UUID.randomUUID();
    String minecraftName = "Testee";
    MariaDbUserRepository testee = new MariaDbUserRepository(dataSource);
    when(dataSource.getConnection().prepareStatement(UPDATE_USER).executeUpdate()).thenReturn(0);

    // Act
    boolean result = testee.updateIfPresent(minecraftUuid, minecraftName);

    // Assert
    assertFalse(result, "Result was true");
    verify(dataSource.getConnection(), atLeastOnce()).prepareStatement(UPDATE_USER);
    verify(dataSource.getConnection().prepareStatement(anyString())).setString(1, minecraftUuid.toString());
    verify(dataSource.getConnection().prepareStatement(anyString())).setString(2, minecraftName);
  }
}
