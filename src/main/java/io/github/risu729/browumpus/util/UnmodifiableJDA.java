/*
 * Copyright (c) 2023 Risu
 *
 *  This source code is licensed under the MIT license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package io.github.risu729.browumpus.util;

import com.google.common.base.MoreObjects;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.sticker.StickerPack;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.entities.sticker.StickerUnion;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.DirectAudioController;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.GuildAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

@SuppressWarnings("OverloadedVarargsMethod")
public final class UnmodifiableJDA implements JDA {

  private final JDA jda;

  private UnmodifiableJDA(@NotNull JDA jda) {
    this.jda = jda;
  }

  public static @NotNull UnmodifiableJDA of(@NotNull JDA jda) {
    return new UnmodifiableJDA(jda);
  }

  @Override
  @Nonnull
  public Status getStatus() {
    return jda.getStatus();
  }

  @Override
  @Nonnull
  public EnumSet<GatewayIntent> getGatewayIntents() {
    return jda.getGatewayIntents();
  }

  @Override
  @Nonnull
  public EnumSet<CacheFlag> getCacheFlags() {
    return jda.getCacheFlags();
  }

  @Override
  public boolean unloadUser(long userId) {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getGatewayPing() {
    return jda.getGatewayPing();
  }

  @Override
  @Nonnull
  public JDA awaitStatus(@NotNull JDA.Status status, @NotNull Status @NotNull ... failOn) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int cancelRequests() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public ScheduledExecutorService getRateLimitPool() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public ScheduledExecutorService getGatewayPool() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public ExecutorService getCallbackPool() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public OkHttpClient getHttpClient() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public DirectAudioController getDirectAudioController() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addEventListener(@NotNull Object @NotNull ... listeners) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeEventListener(@NotNull Object @NotNull ... listeners) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public List<Object> getRegisteredListeners() {
    return jda.getRegisteredListeners();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public RestAction<List<Command>> retrieveCommands(boolean withLocalizations) {
    return jda.retrieveCommands(withLocalizations);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public RestAction<Command> retrieveCommandById(@NotNull String id) {
    return jda.retrieveCommandById(id);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public RestAction<Command> upsertCommand(@NotNull CommandData command) {
    throw new UnsupportedOperationException();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public CommandListUpdateAction updateCommands() {
    throw new UnsupportedOperationException();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public CommandEditAction editCommandById(@NotNull String id) {
    throw new UnsupportedOperationException();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public RestAction<Void> deleteCommandById(@NotNull String commandId) {
    throw new UnsupportedOperationException();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public GuildAction createGuild(@NotNull String name) {
    return jda.createGuild(name);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public RestAction<Void> createGuildFromTemplate(@NotNull String code, @NotNull String name,
      @Nullable Icon icon) {
    return jda.createGuildFromTemplate(code, name, icon);
  }

  @Override
  @Nonnull
  public CacheView<AudioManager> getAudioManagerCache() {
    return jda.getAudioManagerCache();
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<User> getUserCache() {
    return jda.getUserCache();
  }

  @Override
  @Nonnull
  public List<Guild> getMutualGuilds(@NotNull User @NotNull ... users) {
    return jda.getMutualGuilds(users);
  }

  @Override
  @Nonnull
  public List<Guild> getMutualGuilds(@NotNull Collection<User> users) {
    return jda.getMutualGuilds(users);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public CacheRestAction<User> retrieveUserById(long id) {
    return jda.retrieveUserById(id);
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<Guild> getGuildCache() {
    return jda.getGuildCache();
  }

  @Override
  @Nonnull
  public Set<String> getUnavailableGuilds() {
    return jda.getUnavailableGuilds();
  }

  @Override
  public boolean isUnavailable(long guildId) {
    return jda.isUnavailable(guildId);
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<Role> getRoleCache() {
    return jda.getRoleCache();
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<ScheduledEvent> getScheduledEventCache() {
    return jda.getScheduledEventCache();
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<PrivateChannel> getPrivateChannelCache() {
    return jda.getPrivateChannelCache();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public CacheRestAction<PrivateChannel> openPrivateChannelById(long userId) {
    return jda.openPrivateChannelById(userId);
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<RichCustomEmoji> getEmojiCache() {
    return jda.getEmojiCache();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public RestAction<StickerUnion> retrieveSticker(@NotNull StickerSnowflake sticker) {
    return jda.retrieveSticker(sticker);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public RestAction<List<StickerPack>> retrieveNitroStickerPacks() {
    return jda.retrieveNitroStickerPacks();
  }

  @Override
  @Nonnull
  public IEventManager getEventManager() {
    return jda.getEventManager();
  }

  @Override
  public void setEventManager(@Nullable IEventManager manager) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public SelfUser getSelfUser() {
    return jda.getSelfUser();
  }

  @Override
  @Nonnull
  public Presence getPresence() {
    return jda.getPresence();
  }

  @Override
  @Nonnull
  public ShardInfo getShardInfo() {
    return jda.getShardInfo();
  }

  @Override
  @Nonnull
  public String getToken() {
    return jda.getToken();
  }

  @Override
  public long getResponseTotal() {
    return jda.getResponseTotal();
  }

  @Override
  public int getMaxReconnectDelay() {
    return jda.getMaxReconnectDelay();
  }

  @Override
  public void setRequestTimeoutRetry(boolean retryOnTimeout) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAutoReconnect() {
    return jda.isAutoReconnect();
  }

  @Override
  public void setAutoReconnect(boolean reconnect) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isBulkDeleteSplittingEnabled() {
    return jda.isBulkDeleteSplittingEnabled();
  }

  // supported for bean destroy
  @Override
  public void shutdown() {
    jda.shutdown();
  }

  // supported for bean destroy
  @Override
  public void shutdownNow() {
    jda.shutdownNow();
  }

  @Override
  @Nonnull
  public AccountType getAccountType() {
    return jda.getAccountType();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public RestAction<ApplicationInfo> retrieveApplicationInfo() {
    return jda.retrieveApplicationInfo();
  }

  @Override
  @Nonnull
  public JDA setRequiredScopes(@NotNull Collection<String> scopes) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nonnull
  public String getInviteUrl(@Nullable Permission... permissions) {
    return jda.getInviteUrl(permissions);
  }

  @Override
  @Nonnull
  public String getInviteUrl(@Nullable Collection<Permission> permissions) {
    return jda.getInviteUrl(permissions);
  }

  @Override
  @javax.annotation.Nullable
  public ShardManager getShardManager() {
    throw new UnsupportedOperationException();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public RestAction<Webhook> retrieveWebhookById(@NotNull String webhookId) {
    return jda.retrieveWebhookById(webhookId);
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<StageChannel> getStageChannelCache() {
    return jda.getStageChannelCache();
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<ThreadChannel> getThreadChannelCache() {
    return jda.getThreadChannelCache();
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<Category> getCategoryCache() {
    return jda.getCategoryCache();
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<TextChannel> getTextChannelCache() {
    return jda.getTextChannelCache();
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<NewsChannel> getNewsChannelCache() {
    return jda.getNewsChannelCache();
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
    return jda.getVoiceChannelCache();
  }

  @Override
  @Nonnull
  public SnowflakeCacheView<ForumChannel> getForumChannelCache() {
    return jda.getForumChannelCache();
  }

  @Override
  public @NotNull String toString() {
    return MoreObjects.toStringHelper(this).add("jda", jda).toString();
  }
}
