package io.github.risu729.browumpus;

import io.github.risu729.browumpus.util.FileUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
@ComponentScan({"io.github.risu729.browumpus", "com.linecorp.bot.spring.boot"})
public class BrowumpusApplication {

  @SuppressWarnings("AccessOfSystemProperties")
  public static final Path TEMP_DIR = Path.of(System.getProperty("java.io.tmpdir"), "browumpus");
  @SuppressWarnings({"WeakerAccess", "StaticMethodOnlyUsedInOneClass"})
  public static final Path RESOURCES_DIR = Path.of("src", "main", "resources");

  public static void main(String[] args) {
    FileUtil.deleteQuietly(TEMP_DIR);
    try {
      Files.createDirectory(TEMP_DIR);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    Runtime.getRuntime().addShutdownHook(new Thread(BrowumpusApplication::shutdown));

    SpringApplication.run(BrowumpusApplication.class, args);
  }

  private static void shutdown() {
    FileUtil.deleteQuietly(TEMP_DIR);
  }
}
