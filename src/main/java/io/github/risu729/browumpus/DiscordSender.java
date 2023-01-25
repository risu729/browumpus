/*
 * Copyright (c) 2023 Risu
 *
 *  This source code is licensed under the MIT license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package io.github.risu729.browumpus;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class DiscordSender implements MessageSender {

  @NotNull JDAWebhookClient webhookClient;

  public void send(@NotNull Message message) {
    webhookClient.send(convertToWebhookMessage(message));
  }

  @Contract(pure = true)
  private @NotNull WebhookMessage convertToWebhookMessage(@NotNull Message message) {
    var builder = new WebhookMessageBuilder().setUsername(message.author().name())
        .setAvatarUrl(message.author().uri().map(URI::toString).orElse(null))
        .setAllowedMentions(AllowedMentions.none())
        .setContent(message.content().orElse(null));
    for (var attachment : message.attachments()) {
      try (var stream = attachment.stream().orElseThrow()) {
        builder.addFile(attachment.filename(), stream);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
    return builder.build();
  }
}
