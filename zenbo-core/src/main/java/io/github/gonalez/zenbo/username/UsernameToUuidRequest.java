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

import io.github.gonalez.zenbo.Request;
import org.immutables.value.Value;

/**
 * Represents the request for the <a href="https://wiki.vg/Mojang_API#Username_to_UUID">Username to UUID</a>.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
@Value.Immutable
public interface UsernameToUuidRequest extends Request<UsernameToUuidResponse> {
  /** @return the name for which to get the uuid for. */
  String username();
}
