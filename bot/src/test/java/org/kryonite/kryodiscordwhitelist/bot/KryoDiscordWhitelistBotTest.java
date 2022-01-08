package org.kryonite.kryodiscordwhitelist.bot;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KryoDiscordWhitelistBotTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private HikariDataSource hikariDataSource;

  @Test
  void shouldSetupInstance() throws SQLException {
    // Arrange - Act
    KryoDiscordWhitelistBot result = new KryoDiscordWhitelistBot(hikariDataSource);

    // Assert
    assertNotNull(result, "Result should not be null");
  }
}
