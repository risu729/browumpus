/*
 * Copyright (c) 2023 Risu
 *
 *  This source code is licensed under the MIT license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package io.github.risu729.browumpus.util;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import io.github.risu729.browumpus.BrowumpusApplication;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkArgument;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class FileUtil {

  @Contract(" -> fail")
  private FileUtil() {
    throw new AssertionError();
  }

  public static void deleteQuietly(@NotNull Path path) {
    if (Files.exists(path)) {
      try {
        MoreFiles.deleteRecursively(path, RecursiveDeleteOption.ALLOW_INSECURE);
      } catch (IOException ignored) {
      }
    }
  }

  public static void deleteIfEmptyQuietly(@NotNull Path dir) {
    if (Files.exists(dir)) {
      checkArgument(Files.isDirectory(dir));
      try (var stream = Files.list(dir)) {
        if (stream.findAny().isEmpty()) {
          Files.delete(dir);
        }
      } catch (IOException ignored) {
      }
    }
  }

  public static @NotNull Path copyToDir(@NotNull Path source, @NotNull Path targetDir)
      throws IOException {
    return Files.copy(source, targetDir.resolve(source.getFileName()));
  }

  public static @NotNull Path createDirectoriesAndCopy(@NotNull Path source, @NotNull Path target)
      throws IOException {
    var parent = target.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    return Files.copy(source, target);
  }

  public static @NotNull Path createDirectoriesAndWriteString(@NotNull Path path,
      @NotNull String str) throws IOException {
    var parent = path.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    return Files.writeString(path, str);
  }

  @Contract(pure = true)
  public static @NotNull String getFileExtension(@NotNull String filename) {
    return MoreFiles.getFileExtension(Path.of(filename));
  }

  @Contract(pure = true)
  public static @NotNull String getFilenameWithoutExtension(@NotNull String filename) {
    return MoreFiles.getNameWithoutExtension(Path.of(filename));
  }

  @SuppressWarnings({"MethodCallInLoopCondition", "ReassignedVariable"})
  @CheckReturnValue
  public static @NotNull Path generateUniquePathInDir(@NotNull Path path) {
    if (!Files.exists(path)) {
      return path;
    }

    var filename = path.getFileName();
    var nameWithoutExtension = MoreFiles.getNameWithoutExtension(filename);
    var extension = "." + MoreFiles.getFileExtension(filename);

    var generatedPath = path;
    var dir = path.getParent();
    for (int i = 1; Files.exists(generatedPath); i++) {
      generatedPath = dir.resolve(nameWithoutExtension + "_" + i + extension);
    }
    return path;
  }

  @CheckReturnValue
  public static @NotNull Path createTempDir() {
    try {
      return Files.createTempDirectory(BrowumpusApplication.TEMP_DIR, null);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @SuppressWarnings("OverloadedVarargsMethod")
  @Contract(pure = true)
  public static boolean isExtension(@NotNull Path path, @NotNull String @NotNull ... extensions) {
    return isExtension(path.getFileName().toString(), extensions);
  }

  @SuppressWarnings("OverloadedVarargsMethod")
  @Contract(pure = true)
  public static boolean isExtension(@NotNull String filename,
      @NotNull String @NotNull ... extensions) {
    var extension = MoreFiles.getFileExtension(Path.of(filename)).toLowerCase(Locale.ENGLISH);
    return Arrays.asList(extensions).contains(extension);
  }

  @Contract(pure = true)
  public static @NotNull Path appendExtension(@NotNull Path path, @NotNull String extension) {
    return Path.of(path + "." + extension);
  }
}
