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
package io.github.gonalez.zenbo.username.internal;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;
import io.github.gonalez.zenbo.ResponseFailureException;
import io.github.gonalez.zenbo.ResponseFailureExceptionProvider;
import io.github.gonalez.zenbo.OkResponses;
import io.github.gonalez.zenbo.Responses;
import io.github.gonalez.zenbo.username.*;
import io.github.gonalez.zenbo.Request.RequestListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * A basic implementation of {@link UsernameApi}.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
public class DefaultUsernameApi implements UsernameApi {
  private final OkHttpClient httpClient;
  private final Executor executor;
  private final ResponseFailureExceptionProvider interceptorProvider;

  public DefaultUsernameApi(
      OkHttpClient httpClient,
      Executor executor,
      ResponseFailureExceptionProvider interceptorProvider) {
    this.httpClient = httpClient;
    this.executor = executor;
    this.interceptorProvider = interceptorProvider;
  }

  @Override
  public ListenableFuture<UsernameToUuidResponse> usernameToUuid(UsernameToUuidRequest request) {
    return Futures.submitAsync(
        () -> {
          try {
            Response response = httpClient.newCall(
                new Request.Builder()
                    .url("https://api.mojang.com/users/profiles/minecraft/" + request.username())
                    .build())
                .execute();
            ResponseFailureException failureException = interceptorProvider.provide(response.code());
            if (failureException != null) {
              return Futures.immediateFailedFuture(failureException);
            }
            JsonObject responseJsonObject = OkResponses.responseToJson(response).getAsJsonObject();
            ListenableFuture<UsernameToUuidResponse> responseListenableFuture = Futures.immediateFuture(
                ImmutableUsernameToUuidResponse.builder()
                    .uuid(StringUuids.uuidFromString(responseJsonObject.get("id").getAsString()))
                    .build());
            Responses.addListenersIfPresent(responseListenableFuture, request, executor);
            return responseListenableFuture;
          } catch (IOException e) {
            return Futures.immediateFailedFuture(e);
          }
        }, executor);
  }
}
