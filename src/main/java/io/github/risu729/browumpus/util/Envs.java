
/*
 * Copyright (c) 2023 Risu
 *
 *  This source code is licensed under the MIT license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package io.github.risu729.browumpus.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

// wrap env variables for null checking
public final class Envs {

  @Contract(" -> fail")
  private Envs() {
    throw new AssertionError();
  }

  @SuppressWarnings("CallToSystemGetenv")
  @Contract(pure = true)
  public static @NotNull String getEnv(@NotNull String key) {
    return checkNotNull(System.getenv(key));
  }
}
