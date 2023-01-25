
/*
 * Copyright (c) 2023 Risu
 *
 *  This source code is licensed under the MIT license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package io.github.risu729.browumpus;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

// wrap env variables to keep it consistent during runtime and for null checking
final class Envs {

  private static final Map<String, String> ENV = System.getenv();

  @Contract(" -> fail")
  private Envs() {
    throw new AssertionError();
  }

  @Contract(pure = true)
  static @NotNull String getEnv(@NotNull String key) {
    return checkNotNull(ENV.get(key));
  }
}
