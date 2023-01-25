/*
 * Copyright (c) 2023 Risu
 *
 *  This source code is licensed under the MIT license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package io.github.risu729.browumpus;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.TextMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class LineSender implements MessageSender {

  @NotNull LineMessagingClient messagingClient;

  @Override
  public void send(@NotNull Message message) {
    messagingClient.pushMessage(new PushMessage(Envs.getEnv("LINE_GROUP_ID"),
        convertToLineMessages(message)));
  }

  @Contract(pure = true)
  private @NotNull List<com.linecorp.bot.model.message.@NotNull Message> convertToLineMessages(
      @NotNull Message message) {
    var sender = com.linecorp.bot.model.message.sender.Sender.builder()
        .name(message.author().name())
        .iconUrl(message.author().uri().orElse(null))
        .build();
    return Stream.concat(message.content()
            .map(content -> TextMessage.builder().text(content).sender(sender).build())
            .stream(),
        message.attachments().stream().map(attachment -> switch (attachment.extension()) {
          case "jpg", "jpeg", "png" -> ImageMessage.builder()
              .originalContentUrl(attachment.uri().orElseThrow())
              .previewImageUrl(attachment.previewURI().or(attachment::uri).orElseThrow())
              .sender(sender)
              .build();
          case "mp4" -> throw new UnsupportedOperationException("Video is not supported yet");
          case "m4a" -> throw new UnsupportedOperationException("Audio is not supported yet");
          default -> throw new UnsupportedOperationException(
              "Unsupported extension: " + attachment.extension());
        })).toList();
  }
}
