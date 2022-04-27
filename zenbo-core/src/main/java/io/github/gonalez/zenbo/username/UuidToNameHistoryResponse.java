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

import io.github.gonalez.zenbo.Response;
import org.immutables.value.Value;

import java.util.Set;

/**
 * Represents the response of a {@link UuidToNameHistoryRequest}.
 *
 * @author Gaston Gonzalez (Gonalez)
 * @see UuidToNameHistoryRequest
 */
@Value.Immutable
public interface UuidToNameHistoryResponse extends Response {
  /** @return a list of all usernames the user with the request uuid has used in the past. */
  Set<String> usernames();
}
