package org.kryonite.kryodiscordbot.persistence.repository.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryodiscordbot.persistence.entity.User;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MariaDbUserRepositoryTest {

  @Mock
  private Connection connection;

  @Test
  void shouldCreateTableOnStartup() throws SQLException {
    // Arrange
    when(connection.prepareStatement(anyString())).thenReturn(mock(PreparedStatement.class));

    // Act
    new MariaDbUserRepository(connection);

    // Assert
    verify(connection).prepareStatement(MariaDbUserRepository.CREATE_USER_TABLE);
  }

  @Test
  void shouldSaveUser() throws SQLException {
    // Arrange
    User user = User.create(123456L, "Test");

    PreparedStatement preparedStatement = mock(PreparedStatement.class);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    MariaDbUserRepository testee = new MariaDbUserRepository(connection);

    // Act
    testee.save(user);

    // Assert
    verify(connection).prepareStatement(MariaDbUserRepository.INSERT_USER);
    verify(preparedStatement).setLong(1, user.getDiscordId());
    verify(preparedStatement).setString(2, user.getMinecraftName());
    verify(preparedStatement).setString(3, user.getMinecraftName());
  }
}
