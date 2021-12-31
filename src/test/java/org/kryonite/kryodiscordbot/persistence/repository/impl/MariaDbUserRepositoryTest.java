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
    User user = User.create(123456L, "Test");
    MariaDbUserRepository testee = new MariaDbUserRepository(connection);

    // Act
    testee.save(user);

    // Assert
    verify(connection).prepareStatement(MariaDbUserRepository.INSERT_USER);
    verify(connection.prepareStatement(anyString())).setLong(1, user.getDiscordId());
    verify(connection.prepareStatement(anyString())).setString(2, user.getMinecraftName());
    verify(connection.prepareStatement(anyString())).setString(3, user.getMinecraftName());
  }
}
