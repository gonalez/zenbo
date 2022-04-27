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

import com.google.common.util.concurrent.ListenableFuture;

/**
 * A cache of {@link ListenableFuture} for {@link Request}s.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
public interface ResponseFutureCache {
  /** Puts in the cache the specified future for the given request. */
  <T extends Response> ListenableFuture<T> put(Request<T> request, ListenableFuture<T> future);

  /** Gets the future in the cache for the given request. */
  <T extends Response> ListenableFuture<T> get(Request<T> request);

  /** Removes the future linked to the given request from the cache if it exists. */
  <T extends Response> void remove(Request<T> request);

  /** @return {@code true} if a future is cached the given request. */
  <T extends Response> boolean contains(Request<T> request);
}
