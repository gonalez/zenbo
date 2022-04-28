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

import org.immutables.value.Value;

import java.util.Optional;

/**
 * Represents a request being sent to a Mojang API.
 *
 * @param <T> the type of response expected by this request.
 * @author Gaston Gonzalez (Gonalez)
 */
public interface Request<T extends Response> {
  /** Listeners to execute when the response for this request is obtained. */
  interface RequestListener<T extends Response> {
    /** Called when the response was successfully obtained. */
    void onSuccess(T t);

    /** Called when failed to get the response. */
    void onFailure(Throwable throwable);
  }

  /** Options to be used not in the response but for (optional) extra logic in the code itself. */
  @Value.Immutable
  interface RequestOptions {
    /** @return {@code true} if the request can be cached. */
    boolean cacheable();

    /** @return whether the request will not use the cached result for the given request if is available. */
    default boolean ignoreCache() {
      return false;
    }
  }

  /** @return a {@code Optional} of the request listener. */
  Optional<RequestListener<T>> listener();

  /** @return a {@code Optional} of the request options. */
  Optional<RequestOptions> options();
}
