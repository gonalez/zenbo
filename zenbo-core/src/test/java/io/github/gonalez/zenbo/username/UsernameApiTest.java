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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.util.concurrent.ListenableFuture;
import io.github.gonalez.zenbo.username.internal.DefaultUsernameApi;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * Tests for the {@link UsernameApi}.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsernameApiTest {
  private static final UUID QENTIN_UUID = UUID.fromString("51800622-9dae-4b23-84a7-26d6a27c60db");

  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final UsernameApi usernameApi = new DefaultUsernameApi(new OkHttpClient(), executor);

  @AfterAll
  public void tearDown() {
    executor.shutdownNow();
  }

  @Test
  public void testUsernameToUuid() throws Exception {
    ListenableFuture<UsernameToUuidResponse> usernameToUuidResponseListenableFuture =
        usernameApi.usernameToUuid(
            ImmutableUsernameToUuidRequest.builder()
                .username("Qentin")
                .build());
    assertEquals(QENTIN_UUID, usernameToUuidResponseListenableFuture.get().uuid());
  }
}
