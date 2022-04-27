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
package io.github.gonalez.zenbo.internal;

import com.google.common.collect.ImmutableMap;
import io.github.gonalez.zenbo.ResponseFailureException;
import io.github.gonalez.zenbo.ResponseFailureExceptionProvider;

/**
 * A default implementation of {@link ResponseFailureExceptionProvider.Builder}.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
public class DefaultResponseFailureExceptionBuilder implements ResponseFailureExceptionProvider.Builder {
  private final ImmutableMap.Builder</* responseCode*/Integer, ResponseFailureException> builder = ImmutableMap.builder();

  @Override
  public ResponseFailureExceptionProvider.Builder withException(int responseCode, ResponseFailureException exception) {
    builder.put(responseCode, exception);
    return this;
  }

  @Override
  public ResponseFailureExceptionProvider build() {
    return new ResponseFailureExceptionProvider() {
      final ImmutableMap<Integer, ResponseFailureException> map = builder.build();

      @Override
      public ResponseFailureException provide(int responseCode) {
        return map.get(responseCode);
      }
    };
  }
}
