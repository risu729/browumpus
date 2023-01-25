/*
 * Copyright (c) 2023 Risu
 *
 *  This source code is licensed under the MIT license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package io.github.risu729.browumpus;

import com.google.common.base.Suppliers;
import io.github.risu729.browumpus.util.FileUtil;
import lombok.Value;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;


@Accessors(fluent = true)
@Value
public class Message {

  @Nullable String content;
  @Nullable List<Attachment> attachments;
  @NotNull Author author;

  public Message(@NotNull String content, @NotNull Author author) {
    this(content, (List<Attachment>) null, author);
  }


  public Message(@NotNull Attachment attachment, @NotNull Author author) {
    this(null, List.of(attachment), author);
  }

  public Message(@NotNull List<Attachment> attachments, @NotNull Author author) {
    this(null, attachments, author);
  }

  public Message(@NotNull String content, @NotNull Attachment attachment, @NotNull Author author) {
    this(null, List.of(attachment), author);
  }

  public Message(@Nullable String content, @Nullable List<Attachment> attachments,
      @NotNull Author author) {
    checkArgument(content != null || attachments != null);
    this.content = content == null || content.isBlank() ? null : content;
    this.attachments = attachments == null ? null : List.copyOf(attachments);
    this.author = author;
  }

  @Contract(pure = true)
  public @NotNull Optional<String> content() {
    return Optional.ofNullable(content);
  }

  @Contract(pure = true)
  public @NotNull List<@NotNull Attachment> attachments() {
    return Optional.ofNullable(attachments).orElse(Collections.emptyList());
  }

  @SuppressWarnings({"PublicInnerClass", "ClassWithTooManyConstructors"})
  @Accessors(fluent = true)
  @Value
  public static class Attachment {

    @NotNull String filename;
    @NotNull String extension;
    @Nullable URI uri;
    @Nullable Supplier<InputStream> stream;
    @Nullable URI previewURI;
    @Nullable Supplier<InputStream> previewStream;

    public Attachment(@NotNull String filename, @NotNull URI uri) {
      this(filename, uri, null, null, null);
    }

    public Attachment(@NotNull String filename, @NotNull URI uri, @NotNull URI previewURI) {
      this(filename, uri, null, previewURI, null);
    }

    public Attachment(@NotNull String filename, @NotNull Supplier<InputStream> streamSupplier) {
      this(filename, null, streamSupplier, null, null);
    }

    public Attachment(@NotNull String filename, @NotNull Supplier<InputStream> streamSupplier,
        @NotNull Supplier<InputStream> previewStreamSupplier) {
      this(filename, null, streamSupplier, null, previewStreamSupplier);
    }

    public Attachment(@NotNull String filename, @NotNull URI uri,
        @NotNull Supplier<InputStream> streamSupplier) {
      this(filename, uri, streamSupplier, null, null);
    }


    private Attachment(@NotNull String filename, @Nullable URI uri,
        @Nullable Supplier<InputStream> streamSupplier, @Nullable URI previewURI,
        @Nullable Supplier<InputStream> previewStreamSupplier) {
      checkArgument(uri != null || streamSupplier != null);
      this.filename = filename;
      this.extension = FileUtil.getFileExtension(filename);
      this.uri = uri;
      this.stream = streamSupplier == null ? null : Suppliers.memoize(streamSupplier::get);
      this.previewURI = previewURI;
      this.previewStream =
          previewStreamSupplier == null ? null : Suppliers.memoize(previewStreamSupplier::get);
    }

    @Contract(pure = true)
    public @NotNull String extension() {
      return extension;
    }

    @Contract(pure = true)
    public @NotNull Optional<URI> uri() {
      return Optional.ofNullable(uri);
    }

    @Contract(pure = true)
    public @NotNull Optional<InputStream> stream() {
      return Optional.ofNullable(stream).map(Supplier::get);
    }

    @Contract(pure = true)
    public @NotNull Optional<URI> previewURI() {
      return Optional.ofNullable(previewURI);
    }

    @Contract(pure = true)
    public @NotNull Optional<InputStream> previewStream() {
      return Optional.ofNullable(previewStream).map(Supplier::get);
    }
  }

  @SuppressWarnings("PublicInnerClass")
  @Accessors(fluent = true)
  @Value
  public static class Author {

    @NotNull String name;
    @Nullable URI uri;
    @Nullable Supplier<InputStream> stream;

    public Author(@NotNull String name, @NotNull URI uri) {
      this(name, uri, null);
    }

    public Author(@NotNull String name, @NotNull Supplier<InputStream> streamSupplier) {
      this(name, null, streamSupplier);
    }

    private Author(@NotNull String name, @Nullable URI uri,
        @Nullable Supplier<InputStream> streamSupplier) {
      checkArgument(uri != null || streamSupplier != null);
      this.name = name;
      this.uri = uri;
      this.stream = streamSupplier == null ? null : Suppliers.memoize(streamSupplier::get);
    }

    @Contract(pure = true)
    public @NotNull Optional<URI> uri() {
      return Optional.ofNullable(uri);
    }

    @Contract(pure = true)
    public @NotNull Optional<InputStream> stream() {
      return Optional.ofNullable(stream).map(Supplier::get);
    }
  }
}
