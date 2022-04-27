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

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.gonalez.zenbo.ResponseFailureException;
import io.github.gonalez.zenbo.ResponseFailureExceptionProvider;
import io.github.gonalez.zenbo.OkResponses;
import io.github.gonalez.zenbo.Responses;
import io.github.gonalez.zenbo.username.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * A basic implementation of {@link UsernameApi}.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
public class DefaultUsernameApi implements UsernameApi {
  private final OkHttpClient httpClient;
  private final Executor executor;
  private final ResponseFailureExceptionProvider failureExceptionProvider;

  public DefaultUsernameApi(
      OkHttpClient httpClient,
      Executor executor,
      ResponseFailureExceptionProvider failureExceptionProvider) {
    this.httpClient = httpClient;
    this.executor = executor;
    this.failureExceptionProvider = failureExceptionProvider;
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
            ResponseFailureException failureException = failureExceptionProvider.provide(response.code());
            if (failureException != null) {
              return Futures.immediateFailedFuture(failureException);
            }
            return Responses.addListenersIfPresent(Futures.immediateFuture(
                ImmutableUsernameToUuidResponse.builder()
                    .uuid(StringUuids.uuidFromString(
                        OkResponses.responseToJson(response).getAsJsonObject().get("id").getAsString()))
                    .build()), request, executor);
          } catch (IOException e) {
            return Futures.immediateFailedFuture(e);
          }
        }, executor);
  }

  @Override
  public ListenableFuture<UsernamesToUuidsResponse> usernameToUuids(UsernamesToUuidsRequest request) {
    return Futures.submitAsync(
        () -> {
          ImmutableList<ListenableFuture<UsernameToUuidResponse>> responses = request.usernames().stream()
              .map(s -> usernameToUuid(
                  ImmutableUsernameToUuidRequest.builder()
                      .username(s)
                      .build()))
              .collect(toImmutableList());
          return Futures.whenAllComplete(responses)
              .callAsync(
                  () -> {
                    Set<UUID> uuids = new HashSet<>();
                    for (ListenableFuture<UsernameToUuidResponse> response : responses) {
                      UsernameToUuidResponse getDone = Futures.getDone(response);
                      if (getDone == null) {
                        continue;
                      }
                      uuids.add(getDone.uuid());
                    }
                    if (uuids.size() != request.usernames().size()) {
                      return Futures.immediateFailedFuture(new ResponseFailureException(
                          String.format("Failed to get complete list of usernames, got %d, expected %d",
                              uuids.size(),
                              request.usernames().size())));
                    }
                    return Responses.addListenersIfPresent(Futures.immediateFuture(
                        ImmutableUsernamesToUuidsResponse.builder()
                            .uuid(uuids)
                            .build()), request, executor);
                  }, executor);
        }, executor);
  }

  @Override
  public ListenableFuture<UuidToNameHistoryResponse> uuidToNameHistory(UuidToNameHistoryRequest request) {
    return Futures.submitAsync(
        () -> {
          try {
            Response response = httpClient.newCall(
                new Request.Builder()
                    .url("https://api.mojang.com/user/profiles/" + request.uuid().toString() + "/names")
                    .build())
                .execute();
            ResponseFailureException failureException = failureExceptionProvider.provide(response.code());
            if (failureException != null) {
              return Futures.immediateFailedFuture(failureException);
            }
            JsonArray responseJsonArray = OkResponses.responseToJson(response).getAsJsonArray();
            Set<String> usernames = new HashSet<>();
            for (int i = 0 ; i < responseJsonArray.size(); i++) {
              JsonObject jsonObject = responseJsonArray.get(i).getAsJsonObject();
              usernames.add(jsonObject.get("name").getAsString());
            }
            return Responses.addListenersIfPresent(Futures.immediateFuture(
                ImmutableUuidToNameHistoryResponse.builder()
                    .usernames(usernames)
                    .build()), request, executor);
          } catch (IOException e) {
            return Futures.immediateFailedFuture(e);
          }
        }, executor);
  }
}
