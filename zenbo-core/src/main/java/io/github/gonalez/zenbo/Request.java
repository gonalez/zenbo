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

  /** @return a {@code Optional} of the request listener. */
  Optional<RequestListener<T>> listener();
}
