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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import io.github.gonalez.zenbo.ResponseFailureException;
import io.github.gonalez.zenbo.ResponseFailureExceptionProvider;
import io.github.gonalez.zenbo.username.internal.DefaultUsernameApi;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link UsernameApi}.
 *
 * @author Gaston Gonzalez (Gonalez)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsernameApiTest {
  private static final UUID QENTIN_UUID = UUID.fromString("51800622-9dae-4b23-84a7-26d6a27c60db");
  private static final UUID NOTCH_UUID = StringUuids.uuidFromString("069a79f444e94726a5befca90e38aaf5");

  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final UsernameApi usernameApi = new DefaultUsernameApi(new OkHttpClient(), executor,
      ResponseFailureExceptionProvider.newBuilder()
          .withException(204, new ResponseFailureException())
          .build());

  @AfterAll
  public void tearDown() {
    executor.shutdownNow();
  }

  @Test
  public void testUsernameToUuid() throws Exception {
    assertEquals(QENTIN_UUID, usernameApi.usernameToUuid(
        ImmutableUsernameToUuidRequest.builder()
            .username("Qentin")
            .build())
        .get().uuid());
  }

  @Test
  public void testInvalidUsername204() throws Exception {
    ExecutionException exception = assertThrows(ExecutionException.class, usernameApi.usernameToUuid(
        ImmutableUsernameToUuidRequest.builder()
            .username("1234a56789")
            .build())::get);
    assertInstanceOf(ResponseFailureException.class, exception.getCause());
  }

  @Test
  public void testUuidToNameHistory() throws Exception {
    assertTrue(usernameApi.uuidToNameHistory(
            ImmutableUuidToNameHistoryRequest.builder()
                .uuid(QENTIN_UUID)
                .build())
        .get().usernames().containsAll(ImmutableSet.of("Dizzin", "Qentin")));
  }

  @Test
  public void testUsernamesToUuids() throws Exception {
    assertEquals(ImmutableSet.of(QENTIN_UUID, NOTCH_UUID),
        usernameApi.usernamesToUuids(
            ImmutableUsernamesToUuidsRequest.builder()
                .usernames(ImmutableList.of("Qentin", "Notch"))
                .build())
            .get().uuid());
  }
}
