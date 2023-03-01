/*
 * Copyright (c) 2023 Risu
 *
 *  This source code is licensed under the MIT license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package io.github.risu729.browumpus;

import com.linecorp.bot.client.LineBlobClient;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.exception.NotFoundException;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ContentProvider;
import com.linecorp.bot.model.event.message.FileMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.message.VideoMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.request.SetWebhookEndpointRequest;
import com.linecorp.bot.model.response.GetWebhookEndpointResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import io.github.risu729.browumpus.util.Envs;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkArgument;

@SuppressWarnings("unused")
@Slf4j
@LineMessageHandler
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LineListener {

  @NotNull LineMessagingClient messagingClient;
  @NotNull LineBlobClient blobClient;
  @NotNull DiscordSender discordSender;

  public LineListener(@NotNull LineMessagingClient messagingClient,
      @NotNull LineBlobClient blobClient, @NotNull DiscordSender discordSender,
      @Value("${line.bot.handler.path}") @NotNull String endpointPath) {
    this.messagingClient = messagingClient;
    this.blobClient = blobClient;
    this.discordSender = discordSender;

    setWebhookEndpoint(endpointPath);
  }

  @SuppressWarnings("ReassignedVariable")
  private void setWebhookEndpoint(@NotNull String endpointPath) {

    Optional<GetWebhookEndpointResponse> getResponse;
    try {
      getResponse = Optional.of(messagingClient.getWebhookEndpoint().get());
    } catch (ExecutionException e) {
      if (e.getCause() instanceof NotFoundException) {
        getResponse = Optional.empty();
      } else {
        throw new RuntimeException(e);
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    // throw exception if webhook is not active, which can't be enabled through API
    if (!getResponse.map(GetWebhookEndpointResponse::isActive).orElse(false)) {
      throw new IllegalStateException("Webhook of LINE bot is not active");
    }

    var endpoint = URI.create("https://" + Envs.getEnv("RAILWAY_STATIC_URL") + endpointPath);

    // skip if the endpoint is already set
    if (Objects.equals(endpoint, getResponse.orElseThrow().getEndpoint())) {
      return;
    }

    messagingClient.setWebhookEndpoint(SetWebhookEndpointRequest.builder()
        .endpoint(endpoint)
        .build());
  }

  @SuppressWarnings("rawtypes")
  @EventMapping
  public void onMessageEvent(@NotNull MessageEvent event) {
    if (!(event.getSource() instanceof GroupSource groupSource && groupSource.getGroupId()
        .equals(Envs.getEnv("LINE_GROUP_ID")))) {
      onEvent(event);
      return;
    }

    log.info("Received message from LINE: {}", event);

    discordSender.send(convertToMessage(event));
  }

  @EventMapping
  public void onJoinEvent(@NotNull JoinEvent event) {
    checkArgument(event.getSource() instanceof GroupSource);
    var groupID = ((GroupSource) event.getSource()).getGroupId();
    log.info("Joined group: {}", groupID);
    messagingClient.replyMessage(new ReplyMessage(event.getReplyToken(),
            new TextMessage("このグループのIDは %s です。".formatted(groupID))))
        // leave group to prevent webhooks
        .whenComplete((res, err) -> messagingClient.leaveGroup(groupID));
  }

  @EventMapping
  public void onEvent(@NotNull Event event) {
    log.info("Received event(Ignored): {}", event);
  }

  @Contract(pure = true)
  @SuppressWarnings("resource")
  private @NotNull Message convertToMessage(@NotNull MessageEvent<?> event) {

    var message = event.getMessage();
    var author = convertToAuthor(event.getSource());

    if (message instanceof TextMessageContent textMessage) {
      return new Message(textMessage.getText(), author);
    } else if (message instanceof ImageMessageContent imageMessage) {
      return new Message(convertToAttachment(imageMessage.getId(),
          "jpg",
          imageMessage.getContentProvider()), author);
    } else if (message instanceof AudioMessageContent audioMessage) {
      return new Message(convertToAttachment(audioMessage.getId(),
          "m4a",
          audioMessage.getContentProvider()), author);
    } else if (message instanceof VideoMessageContent videoMessage) {
      return new Message(convertToAttachment(videoMessage.getId(),
          "mp4",
          videoMessage.getContentProvider()), author);
    } else if (message instanceof FileMessageContent fileMessage) {
      return new Message(new Message.Attachment(fileMessage.getFileName(),
          () -> blobClient.getMessageContent(fileMessage.getId()).join().getStream()), author);
    } else if (message instanceof LocationMessageContent locationMessage) {
      throw new UnsupportedOperationException("LocationMessageContent is not supported");
    } else if (message instanceof StickerMessageContent stickerMessage) {
      throw new UnsupportedOperationException("StickerMessageContent is not supported");
    } else {
      throw new IllegalArgumentException("Unknown message type: " + message.getClass());
    }
  }

  @Contract(pure = true)
  @SuppressWarnings("resource")
  private @NotNull Message.Attachment convertToAttachment(@NotNull String messageID,
      @NotNull String extension, @NotNull ContentProvider provider) {
    switch (provider.getType()) {
      case "line" -> {
        return new Message.Attachment(messageID + "." + extension,
            () -> blobClient.getMessageContent(messageID).join().getStream());
      }
      case "external" -> {
        var url = provider.getOriginalContentUrl();
        return new Message.Attachment(Path.of(url).getFileName().toString(),
            url,
            provider.getPreviewImageUrl());
      }
      default -> throw new IllegalArgumentException(
          "Unknown content provider type: " + provider.getType());
    }
  }

  @Contract(pure = true)
  private @NotNull Message.Author convertToAuthor(@NotNull Source source) {
    UserProfileResponse profile;
    if (source instanceof GroupSource groupSource) {
      profile = messagingClient.getGroupMemberProfile(groupSource.getGroupId(),
          groupSource.getUserId()).join();
    } else if (source instanceof RoomSource roomSource) {
      profile = messagingClient.getRoomMemberProfile(roomSource.getRoomId(), roomSource.getUserId())
          .join();
    } else if (source instanceof UserSource userSource) {
      profile = messagingClient.getProfile(userSource.getUserId()).join();
    } else {
      throw new IllegalArgumentException("Unknown source type: " + source.getClass());
    }

    return new Message.Author(profile.getDisplayName(), profile.getPictureUrl());
  }
}