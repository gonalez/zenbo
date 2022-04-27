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
package io.github.gonalez.zenbo.username;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * The API for interactions with usernames.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
public interface UsernameApi {
  /**
   * Returns a response containing the UUID of the requested username.
   *
   * @param request the request to get the response for.
   * @return the request response.
   * @see <a href="https://wiki.vg/Mojang_API#Username_to_UUID">Username to UUID</a>
   */
  ListenableFuture<UsernameToUuidResponse> usernameToUuid(UsernameToUuidRequest request);

  /**
   * Returns a response containing the UUID of the requested username.
   *
   * @param request the request to get the response for.
   * @return the request response.
   * @see <a href="https://wiki.vg/Mojang_API#Usernames_to_UUIDs">Username to UUIDs</a>
   */
  ListenableFuture<UsernamesToUuidsResponse> usernameToUuids(UsernamesToUuidsRequest request);

  /**
   * Returns a response containing all the usernames of the requested uuid.
   *
   * @param request the request to get the response for.
   * @return the request response.
   * @see <a href="https://wiki.vg/Mojang_API#UUID_to_Name_History">UUID to Name History</a>
   */
  ListenableFuture<UuidToNameHistoryResponse> uuidToNameHistory(UuidToNameHistoryRequest request);
}
