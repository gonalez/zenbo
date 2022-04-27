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

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.github.gonalez.zenbo.OkResponses;
import io.github.gonalez.zenbo.username.*;
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

  public DefaultUsernameApi(
      OkHttpClient httpClient,
      Executor executor) {
    this.httpClient = httpClient;
    this.executor = executor;
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
            return Futures.immediateFuture(
                ImmutableUsernameToUuidResponse.builder()
                    .uuid(StringUuids.uuidFromString(
                        OkResponses.responseToJson(response).getAsJsonObject().get("id").getAsString()))
                    .build());
          } catch (IOException e) {
            return Futures.immediateFailedFuture(e);
          }
        }, executor);
  }
}
