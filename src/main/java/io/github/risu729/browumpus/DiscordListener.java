/*
 * Copyright (c) 2023 Risu
 *
 *  This source code is licensed under the MIT license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package io.github.risu729.browumpus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.util.Optional;

@Slf4j
@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DiscordListener extends ListenerAdapter {

  @NotNull LineSender lineSender;

  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    if (!event.getChannel().getId().equals(Envs.getEnv("LINE_TRANSFER_CHANNEL_ID"))) {
      return;
    }
    // ignore self messages or webhook messages (which might be sent by this bot)
    if (event.getJDA().getSelfUser().getId().equals(event.getAuthor().getId())
        || event.isWebhookMessage()) {
      return;
    }
    // ignore system messages
    if (event.getAuthor().isSystem()) {
      return;
    }
    // ignore ephemeral messages which can only be seen by the bot
    if (event.getMessage().isEphemeral()) {
      return;
    }

    var message = event.getMessage();
    log.info("Received message from Discord: {}", message);

    lineSender.send(convertToMessage(message));
  }

  @Contract(pure = true)
  private @NotNull Message convertToMessage(@NotNull net.dv8tion.jda.api.entities.Message message) {
    return new Message(message.getContentStripped(),
        message.getAttachments().stream().map(this::convertToAttachment).toList(),
        convertToAuthor(message.getMember(), message.getAuthor()));
  }

  @Contract(pure = true)
  private @NotNull Message.Attachment convertToAttachment(
      @NotNull net.dv8tion.jda.api.entities.Message.Attachment attachment) {
    return new Message.Attachment(attachment.getFileName(),
        URI.create(attachment.getUrl()),
        () -> attachment.getProxy().download().join());
  }

  @Contract(pure = true)
  private @NotNull Message.Author convertToAuthor(@Nullable Member member, @NotNull User user) {
    var optionalMember = Optional.ofNullable(member);
    return new Message.Author(optionalMember.map(Member::getEffectiveName).orElse(user.getName()),
        URI.create(optionalMember.map(Member::getEffectiveAvatarUrl)
            .orElse(user.getEffectiveAvatarUrl())));
  }
}