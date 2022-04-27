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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * Static methods to work with requests.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
public final class Responses {
  private Responses() {}

  public static <T extends Response> ListenableFuture<T> addListenersIfPresent(
      ListenableFuture<T> future, Request<T> request, Executor executor) {
    if (!request.listener().isPresent()) {
      return future;
    }
    Request.RequestListener<T> listener = request.listener().get();
    Futures.addCallback(future, new FutureCallback<>() {
      @Override
      public void onSuccess(T result) {
        listener.onSuccess(result);
      }

      @Override
      public void onFailure(Throwable throwable) {
        listener.onFailure(throwable);
      }
    }, executor);
    return future;
  }

  public static <T extends Response> ListenableFuture<T> findFromCacheOrNull(
      Request<T> request,
      ResponseFutureCache futureCache) {
    if (!futureCache.contains(request)) {
      return null;
    }
    Optional<Request.RequestOptions> optional = request.options();
    if (optional.isPresent()) {
      Request.RequestOptions options = optional.get();
      if (options.ignoreCache()) {
        return null;
      }
    }
    return futureCache.get(request);
  }
}
