package org.kryonite.kryodiscordbot.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryodiscordbot.persistence.entity.User;
import org.kryonite.kryodiscordbot.persistence.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageListenerTest {

  @InjectMocks
  private MessageListener testee;

  @Mock
  private UserRepository userRepositoryMock;

  @Test
  void shouldPersistUser() {
    // Arrange
    long discordId = 123456789123L;
    String discordMessage = "Test1234";

    MessageReceivedEvent messageReceivedEvent = mock(MessageReceivedEvent.class);

    net.dv8tion.jda.api.entities.User user = mock(net.dv8tion.jda.api.entities.User.class);
    when(messageReceivedEvent.getAuthor()).thenReturn(user);
    when(user.isBot()).thenReturn(false);
    when(user.getIdLong()).thenReturn(discordId);

    MessageChannel messageChannel = mock(MessageChannel.class);
    when(messageReceivedEvent.getChannel()).thenReturn(messageChannel);
    when(messageChannel.getName()).thenReturn("whitelist");

    Message message = mock(Message.class);
    when(messageReceivedEvent.getMessage()).thenReturn(message);
    when(message.getContentStripped()).thenReturn(discordMessage);

    AuditableRestAction<Void> auditableRestAction = mock(AuditableRestAction.class);
    when(message.delete()).thenReturn(auditableRestAction);
    when(auditableRestAction.and(any())).thenReturn(mock(RestAction.class));

    // Act
    testee.onMessageReceived(messageReceivedEvent);

    // Assert
    verify(userRepositoryMock).save(User.create(discordId, discordMessage));
  }

  @Test
  void shouldNotProcessBotMessages() {
    // Arrange
    MessageReceivedEvent messageReceivedEvent = mock(MessageReceivedEvent.class);

    net.dv8tion.jda.api.entities.User user = mock(net.dv8tion.jda.api.entities.User.class);
    when(messageReceivedEvent.getAuthor()).thenReturn(user);
    when(user.isBot()).thenReturn(true);

    // Act
    testee.onMessageReceived(messageReceivedEvent);

    // Assert
    verify(userRepositoryMock, never()).save(any());
  }
}
