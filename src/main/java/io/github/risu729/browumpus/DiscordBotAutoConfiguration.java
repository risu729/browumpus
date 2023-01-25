/*
 * Copyright (c) 2023 Risu
 *
 *  This source code is licensed under the MIT license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package io.github.risu729.browumpus;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import com.google.common.collect.MoreCollectors;
import io.github.risu729.browumpus.util.UnmodifiableJDA;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.UncheckedIOException;

import static com.google.common.base.Preconditions.checkNotNull;

@Configuration
public class DiscordBotAutoConfiguration {

  private static final String WEBHOOK_NAME = "Browumpus";

  @Bean
  public @NotNull UnmodifiableJDA jda(@NotNull DiscordListener listener)
      throws InterruptedException {
    var jda = JDABuilder.createLight(Envs.getEnv("DISCORD_TOKEN"),
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.GUILD_MESSAGES)
        .setActivity(Activity.competing("LINE"))
        .addEventListeners(listener)
        .setEnableShutdownHook(false) // disable because Spring Boot will handle it
        .build();
    jda.awaitReady();
    return UnmodifiableJDA.of(jda);
  }

  @Bean
  public @NotNull JDAWebhookClient webhookClient(@NotNull JDA jda) {
    var discordChannel = checkNotNull(jda.getChannelById(IWebhookContainer.class,
        Envs.getEnv("LINE_TRANSFER_CHANNEL_ID")));
    return JDAWebhookClient.from(discordChannel.retrieveWebhooks()
        .complete()
        .stream()
        .filter(webhook -> webhook.getName().equals(WEBHOOK_NAME))
        .collect(MoreCollectors.toOptional())
        .orElseGet(() -> {
          try {
            return discordChannel.createWebhook(WEBHOOK_NAME)
                .setAvatar(Icon.from(BrowumpusApplication.RESOURCES_DIR.resolve("browumpus.png")
                    .toFile()))
                .complete();
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        }));
  }
}
