package org.kryonite.kryodiscordwhitelist.velocity.command;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.GameProfile;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryodiscordwhitelist.common.persistence.repository.UserRepository;
import org.kryonite.kryodiscordwhitelist.velocity.messaging.MessagingController;
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

  @Mock
  private MessagingController messagingController;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProxyServer proxyServerMock;

  @Test
  void shouldSendUsage_WhenNoArgumentGiven() {
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
  void shouldSendUsage_WhenWrongArgumentGiven() {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"wrongarg"});

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
  void shouldSendMessage_WhenPlayerIsAlreadyWhitelisted() throws SQLException {
    /// Arrange
    String minecraftName = "Testee";

    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"add", minecraftName});
    when(userRepository.addIfNotPresent(any())).thenReturn(false);

    // Act
    testee.execute(invocation);

    // Assert
    verify(userRepository).addIfNotPresent(minecraftName);
    verify(invocation.source()).sendMessage(any());
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

    // Act
    testee.execute(invocation);

    // Assert
    verify(userRepository).removeUser(minecraftName);
    verify(messagingController).sendRemovedPlayerFromWhitelist(minecraftName);
  }

  @Test
  void shouldSendMessage_WhenPlayerIsNotWhitelisted() throws SQLException {
    // Arrange
    String minecraftName = "Testee";

    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"remove", minecraftName});
    when(userRepository.removeUser(any())).thenReturn(false);

    // Act
    testee.execute(invocation);

    // Assert
    verify(userRepository).removeUser(minecraftName);
    verify(invocation.source()).sendMessage(any());
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

  @Test
  void shouldListAllWhitelistedPlayers() throws SQLException {
    // Arrange
    List<String> usernames = List.of("lusu007", "GitKev", "testee");

    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"list"});
    when(userRepository.getAllUsernames()).thenReturn(Optional.of(usernames));

    // Act
    testee.execute(invocation);

    // Assert
    verify(userRepository).getAllUsernames();

    verify(invocation.source()).sendMessage(any());
  }

  @Test
  void shouldHandleExceptions_WhenTryingToListAllWhitelistedPlayers() throws SQLException {
    // Arrange
    List<String> usernames = List.of("lusu007", "GitKev", "testee");

    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"list"});
    when(userRepository.getAllUsernames()).thenThrow(SQLException.class);

    // Act
    testee.execute(invocation);

    // Assert
    verify(userRepository).getAllUsernames();
    verify(invocation.source()).sendMessage(any());
  }

  @Test
  void shouldCheckPermission() {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.source().hasPermission(WhitelistCommand.PERMISSION)).thenReturn(true);

    // Act
    boolean result = testee.hasPermission(invocation);

    // Assert
    assertTrue(result, "Result was not true");
  }

  @Test
  void shouldSuggestNothing() {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"add", "lusu007", ""});

    // Act
    List<String> suggestions = testee.suggest(invocation);

    // Assert
    Assertions.assertEquals(Collections.emptyList(), suggestions);
  }

  @Test
  void shouldSuggestOptions_WhenNoOptionIsGiven() {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {});

    // Act
    List<String> suggestions = testee.suggest(invocation);

    // Assert
    Assertions.assertEquals(List.of("add", "remove", "list"), suggestions);
  }

  @Test
  void shouldSuggestOptions_WhenPartOfOptionIsGiven() {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"dd"});

    // Act
    List<String> suggestions = testee.suggest(invocation);

    // Assert
    Assertions.assertEquals(List.of("add"), suggestions);
  }

  @Test
  void shouldSuggestPlayerName_WhenNoNameIsGiven() {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"add", ""});

    List<Player> fakePlayers = getFakePlayers();
    when(proxyServerMock.getAllPlayers()).thenReturn(fakePlayers);

    List<String> suggestedNames = getFakePlayers().stream()
        .map(player -> player.getGameProfile().getName()).toList();

    // Act
    List<String> suggestions = testee.suggest(invocation);

    // Assert
    Assertions.assertEquals(suggestedNames, suggestions);
  }

  @Test
  void shouldSuggestPlayerName_WhenPartOfNameIsGiven() {
    // Arrange
    SimpleCommand.Invocation invocation = mock(SimpleCommand.Invocation.class, Answers.RETURNS_DEEP_STUBS);
    when(invocation.arguments()).thenReturn(new String[] {"add", "test"});

    List<Player> fakePlayers = getFakePlayers();
    when(proxyServerMock.getAllPlayers()).thenReturn(fakePlayers);

    List<String> suggestedNames = List.of("testee");

    // Act
    List<String> suggestions = testee.suggest(invocation);

    // Assert
    Assertions.assertEquals(suggestedNames, suggestions);
  }

  private List<Player> getFakePlayers() {
    Player player1 = mock(Player.class, Answers.RETURNS_DEEP_STUBS);
    when(player1.getGameProfile())
        .thenReturn(new GameProfile(UUID.randomUUID(), "lusu007", Collections.emptyList()));

    Player player2 = mock(Player.class, Answers.RETURNS_DEEP_STUBS);
    when(player2.getGameProfile())
        .thenReturn(new GameProfile(UUID.randomUUID(), "GitKev", Collections.emptyList()));

    Player player3 = mock(Player.class, Answers.RETURNS_DEEP_STUBS);
    when(player3.getGameProfile())
        .thenReturn(new GameProfile(UUID.randomUUID(), "testee", Collections.emptyList()));

    return List.of(player1, player2, player3);
  }
}
