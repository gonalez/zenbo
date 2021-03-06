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

import com.google.common.util.concurrent.*;

import java.util.Optional;
import java.util.concurrent.Callable;
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

  public static <T extends Response> ListenableFuture<T> buildCachingFutureForRequest(
      Request<T> request, ResponseFutureCache cache,
      Executor executor, Callable<ListenableFuture<T>> listenableFuture) {
    return FluentFuture.from(cache.get(request))
        .transformAsync(input -> {
          Optional<Request.RequestOptions> optionsOptional = request.options();
          if (input != null
              && !(optionsOptional.isPresent()
              && optionsOptional.get().ignoreCache())) {
            return Futures.immediateFuture(input);
          }
          ListenableFuture<T> future = listenableFuture.call();
          if (optionsOptional.isPresent()
              && optionsOptional.get().cacheable()) {
            cache.put(request, future);
          }
          addListenersIfPresent(future, request, executor);
          return listenableFuture.call();
        }, executor);
  }
}
