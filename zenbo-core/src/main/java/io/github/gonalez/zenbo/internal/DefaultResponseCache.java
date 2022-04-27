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

import com.google.common.util.concurrent.ListenableFuture;
import io.github.gonalez.zenbo.Request;
import io.github.gonalez.zenbo.Response;
import io.github.gonalez.zenbo.ResponseFutureCache;

import java.util.HashMap;

/**
 * A simple implementation of {@link ResponseFutureCache}.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
public class DefaultResponseCache implements ResponseFutureCache {
  private final HashMap<Request<?>, ListenableFuture<? extends Response>> futures = new HashMap<>();

  @Override
  public <T extends Response> ListenableFuture<T> put(Request<T> request, ListenableFuture<T> future) {
    futures.put(request, future);
    return future;
  }

  @Override
  public <T extends Response> ListenableFuture<T> get(Request<T> request) {
    if (contains(request)) {
      return null;
    }
    return (ListenableFuture<T>) futures.get(request);
  }

  @Override
  public <T extends Response> void remove(Request<T> request) {
    futures.remove(request);
  }

  @Override
  public <T extends Response> boolean contains(Request<T> request) {
    return futures.containsKey(request);
  }
}
