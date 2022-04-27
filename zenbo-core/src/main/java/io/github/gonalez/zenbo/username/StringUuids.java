/*
 * Copyright 2022 - Gaston Gonzalez (Gonalez)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.gonalez.zenbo.username;

import java.util.UUID;

/**
 * Static methods to work with strings as uuids.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
public final class StringUuids {
  private StringUuids() {}

  private static final char UUID_SYMBOL = '-';

  private static final int UUID_LENGTH_WITHOUT_SYMBOL = 32;
  private static final int[] HYPHENS_AT = new int[]{8, 4, 4, 4};

  public static UUID uuidFromString(String string) {
    if ((string = string.replace("-", "")).length() != UUID_LENGTH_WITHOUT_SYMBOL) {
      throw new IllegalArgumentException(String.format(
          "Unexpected string-uuid length: %s, got %s", UUID_LENGTH_WITHOUT_SYMBOL, string.length()));
    }
    int at = 0;
    StringBuilder stringBuilder = new StringBuilder(string);
    for (int j : HYPHENS_AT) {
      stringBuilder.insert(at+=j, UUID_SYMBOL); at++;
    }
    return UUID.fromString(stringBuilder.toString());
  }
}
