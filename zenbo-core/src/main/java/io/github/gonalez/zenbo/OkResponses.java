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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.Response;

import java.io.IOException;

/**
 * Static methods to work with OkHttp requests.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
public final class OkResponses {
  private OkResponses() {}

  public static JsonElement responseToJson(Response response) throws IOException {
    return JsonParser.parseString(response.body().string());
  }
}
