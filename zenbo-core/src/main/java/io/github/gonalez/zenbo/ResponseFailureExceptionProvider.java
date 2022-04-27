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
package io.github.gonalez.zenbo;

import io.github.gonalez.zenbo.internal.DefaultResponseFailureExceptionBuilder;

/**
 * Provides {@link ResponseFailureException} for a response code.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
@FunctionalInterface
public interface ResponseFailureExceptionProvider {
  /** @return a new {@link ResponseFailureExceptionProvider} builder. */
  static Builder newBuilder() {
    return new DefaultResponseFailureExceptionBuilder();
  }

  /** @return a {@link ResponseFailureException} for the given response code. */
  ResponseFailureException provide(int responseCode);

  /** Builder to create {@link ResponseFailureExceptionProvider}s. */
  interface Builder {
    /** Sets a response exception for the given response code. */
    Builder withException(int responseCode, ResponseFailureException exception);

    /** @return a new {@link ResponseFailureExceptionProvider} from this builder. */
    ResponseFailureExceptionProvider build();
  }
}
