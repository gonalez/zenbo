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

import static com.google.common.collect.ImmutableList.toImmutableList;
import static io.github.gonalez.zenbo.Responses.buildCachingFutureForRequest;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.gonalez.zenbo.*;
import io.github.gonalez.zenbo.username.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * A basic implementation of {@link UsernameApi}.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
public class DefaultUsernameApi implements UsernameApi {
  private final OkHttpClient httpClient;
  private final Executor executor;
  private final ResponseFutureCache responseCache;

  public DefaultUsernameApi(
      OkHttpClient httpClient,
      Executor executor,
      ResponseFutureCache responseCache) {
    this.httpClient = httpClient;
    this.executor = executor;
    this.responseCache = responseCache;
  }

  @Override
  public ListenableFuture<UsernameToUuidResponse> usernameToUuid(UsernameToUuidRequest request) {
    return buildCachingFutureForRequest(request, responseCache, executor,
        () -> {
          Response response = httpClient.newCall(
              new Request.Builder()
                  .url("https://api.mojang.com/users/profiles/minecraft/" + request.username())
                  .build())
              .execute();
          if (!response.isSuccessful()) {
            return Futures.immediateFailedFuture(new ResponseFailureException());
          }
          return Futures.immediateFuture(
              ImmutableUsernameToUuidResponse.builder()
                  .uuid(StringUuids.uuidFromString(
                      OkResponses.responseToJson(response).getAsJsonObject().get("id").getAsString()))
                  .build());
        });
  }

  @Override
  public ListenableFuture<UsernamesToUuidsResponse> usernamesToUuids(UsernamesToUuidsRequest request) {
    return buildCachingFutureForRequest(request, responseCache, executor,
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
                        return Futures.immediateFailedFuture(new ResponseFailureException(
                            String.format("Failed to get complete list of usernames, got %d, expected %d",
                                uuids.size(),
                                request.usernames().size())));
                      }
                      uuids.add(getDone.uuid());
                    }
                    return Futures.immediateFuture(
                        ImmutableUsernamesToUuidsResponse.builder()
                            .uuid(uuids)
                            .build());
                  }, executor);
        });
  }

  @Override
  public ListenableFuture<UuidToNameHistoryResponse> uuidToNameHistory(UuidToNameHistoryRequest request) {
    return buildCachingFutureForRequest(request, responseCache, executor,
        () -> {
          Response response = httpClient.newCall(
              new Request.Builder()
                  .url("https://api.mojang.com/user/profiles/" + request.uuid().toString() + "/names")
                  .build())
              .execute();
          if (!response.isSuccessful()) {
            return Futures.immediateFailedFuture(new ResponseFailureException());
          }
          JsonArray responseJsonArray = OkResponses.responseToJson(response).getAsJsonArray();
          Set<String> usernames = new HashSet<>();
          for (int i = 0 ; i < responseJsonArray.size(); i++) {
            JsonObject jsonObject = responseJsonArray.get(i).getAsJsonObject();
            usernames.add(jsonObject.get("name").getAsString());
          }
          return Futures.immediateFuture(
              ImmutableUuidToNameHistoryResponse.builder()
                  .usernames(usernames)
                  .build());
        });
  }

  @Override
  public ListenableFuture<UuidToProfileAndSkinCapeResponse> uuidToProfileAndSkinCape(UuidToProfileAndSkinCapeRequest request) {
    return buildCachingFutureForRequest(request, responseCache, executor,
        () -> {
          Response response = httpClient.newCall(
              new Request.Builder()
                  .url("https://sessionserver.mojang.com/session/minecraft/profile/" + request.uuid().toString())
                  .build())
              .execute();
          if (!response.isSuccessful()) {
            return Futures.immediateFailedFuture(new ResponseFailureException());
          }
          JsonObject jsonObjectResponse = OkResponses.responseToJson(response).getAsJsonObject();
          ImmutableUuidToProfileAndSkinCapeResponse.Builder builder =
              ImmutableUuidToProfileAndSkinCapeResponse.builder()
                  .username(jsonObjectResponse.get("name").getAsString());
          if (jsonObjectResponse.has("properties")) {
            JsonObject jsonObject = JsonParser.parseString(new String(
                Base64.getDecoder().decode(jsonObjectResponse.getAsJsonArray("properties")
                    .get(0)
                    .getAsJsonObject()
                    .get("value")
                    .getAsString())))
                .getAsJsonObject();
            if (jsonObject.has("textures")) {
              JsonObject texturesJsonObject = jsonObject.get("textures").getAsJsonObject();
              if (texturesJsonObject.has("SKIN")) {
                builder.skinUrl(texturesJsonObject.get("SKIN").getAsJsonObject().get("url").getAsString());
              }
              if (texturesJsonObject.has("CAPE")) {
                builder.capeUrl(texturesJsonObject.get("CAPE").getAsJsonObject().get("url").getAsString());
              }
            }
          }
          return Futures.immediateFuture(builder.build());
        });
  }
}
