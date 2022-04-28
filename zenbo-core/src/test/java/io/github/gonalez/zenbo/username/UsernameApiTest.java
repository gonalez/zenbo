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

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import io.github.gonalez.zenbo.ImmutableRequestOptions;
import io.github.gonalez.zenbo.ResponseFailureException;
import io.github.gonalez.zenbo.ResponseFutureCache;
import io.github.gonalez.zenbo.internal.DefaultResponseCache;
import io.github.gonalez.zenbo.username.internal.DefaultUsernameApi;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
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
  private static final UUID NOTCH_UUID = StringUuids.uuidFromString("069a79f444e94726a5befca90e38aaf5");

  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  private final ResponseFutureCache futureCache = new DefaultResponseCache();
  private final UsernameApi usernameApi = new DefaultUsernameApi(new OkHttpClient(), executor, futureCache);

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
  @Disabled
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

  @Test
  public void testUuidToProfileAndSkinCape() throws Exception {
    assertEquals("http://textures.minecraft.net/texture/beb81f3bf4cc08c4b2038c900d5b32401a9bc7935acfed6c5d3498b566ba20c3",
        usernameApi.uuidToProfileAndSkinCape(
            ImmutableUuidToProfileAndSkinCapeRequest.builder()
                .uuid(QENTIN_UUID)
                .build())
            .get().skinUrl().get());
  }

  @Test
  public void testUsernameToUuidWithCache() throws Exception {
    ImmutableUsernameToUuidRequest.Builder builder = ImmutableUsernameToUuidRequest.builder().username("Qentin");

    ImmutableUsernameToUuidRequest build = builder.build();
    usernameApi.usernameToUuid(build).get();
    assertNull(futureCache.get(build).get());

    ImmutableRequestOptions.Builder optionsBuilder = ImmutableRequestOptions.builder()
        .cacheable(true);

    build = builder.options(optionsBuilder.build()).build();
    usernameApi.usernameToUuid(build).get();
    assertNotNull(futureCache.get(build));
  }
}
