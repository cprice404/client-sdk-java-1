package momento.sdk;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.ByteBuffer;
import java.time.Duration;
import momento.sdk.auth.CredentialProvider;
import momento.sdk.auth.EnvVarCredentialProvider;
import momento.sdk.config.Configurations;
import momento.sdk.exceptions.InvalidArgumentException;
import momento.sdk.messages.CacheDeleteResponse;
import momento.sdk.messages.CacheGetResponse;
import momento.sdk.messages.CacheSetResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests client side exceptions */
final class CacheDataPlaneClientSideTest extends BaseTestClass {

  private static final Duration DEFAULT_ITEM_TTL_SECONDS = Duration.ofSeconds(60);

  private final CredentialProvider credentialProvider =
      new EnvVarCredentialProvider("TEST_AUTH_TOKEN");
  private final String cacheName = System.getenv("TEST_CACHE_NAME");
  private CacheClient client;

  @BeforeEach
  void setup() {
    client =
        CacheClient.builder(
                credentialProvider, Configurations.Laptop.Latest(), DEFAULT_ITEM_TTL_SECONDS)
            .build();
  }

  @AfterEach
  void teardown() {
    client.close();
  }

  @Test
  public void nullKeyGetReturnsError() {
    final CacheGetResponse stringResponse = client.get(cacheName, (String) null).join();
    assertThat(stringResponse).isInstanceOf(CacheGetResponse.Error.class);
    assertThat((CacheGetResponse.Error) stringResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);

    final CacheGetResponse byteResponse = client.get(cacheName, (byte[]) null).join();
    assertThat(byteResponse).isInstanceOf(CacheGetResponse.Error.class);
    assertThat((CacheGetResponse.Error) byteResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);
  }

  @Test
  public void nullKeyDeleteReturnsError() {
    final CacheDeleteResponse stringKeyResponse = client.delete(cacheName, (String) null).join();
    assertThat(stringKeyResponse).isInstanceOf(CacheDeleteResponse.Error.class);
    assertThat((CacheDeleteResponse.Error) stringKeyResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);

    final CacheDeleteResponse byteKeyResponse = client.delete(cacheName, (byte[]) null).join();
    assertThat(byteKeyResponse).isInstanceOf(CacheDeleteResponse.Error.class);
    assertThat((CacheDeleteResponse.Error) byteKeyResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);
  }

  @Test
  public void nullKeySetReturnsError() {
    final CacheSetResponse stringSetResponse =
        client.set(cacheName, null, "hello", Duration.ofSeconds(10)).join();
    assertThat(stringSetResponse).isInstanceOf(CacheSetResponse.Error.class);
    assertThat((CacheSetResponse.Error) stringSetResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);

    final CacheSetResponse byteBufferSetResponse =
        client.set(cacheName, null, ByteBuffer.allocate(1), Duration.ofSeconds(10)).join();
    assertThat(byteBufferSetResponse).isInstanceOf(CacheSetResponse.Error.class);
    assertThat((CacheSetResponse.Error) byteBufferSetResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);

    final CacheSetResponse byteKeySetResponse =
        client.set(cacheName, null, new byte[] {0x00}, Duration.ofSeconds(10)).join();
    assertThat(byteKeySetResponse).isInstanceOf(CacheSetResponse.Error.class);
    assertThat((CacheSetResponse.Error) byteKeySetResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);
  }

  @Test
  public void nullValueSetReturnsError() {
    final CacheSetResponse stringResponse =
        client.set(cacheName, "hello", (String) null, Duration.ofSeconds(10)).join();
    assertThat(stringResponse).isInstanceOf(CacheSetResponse.Error.class);
    assertThat((CacheSetResponse.Error) stringResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);

    final CacheSetResponse byteBufferResponse =
        client.set(cacheName, "hello", (ByteBuffer) null, Duration.ofSeconds(10)).join();
    assertThat(byteBufferResponse).isInstanceOf(CacheSetResponse.Error.class);
    assertThat((CacheSetResponse.Error) byteBufferResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);

    final CacheSetResponse byteArrayResponse =
        client.set(cacheName, new byte[] {}, null, Duration.ofSeconds(10)).join();
    assertThat(byteArrayResponse).isInstanceOf(CacheSetResponse.Error.class);
    assertThat((CacheSetResponse.Error) byteArrayResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);
  }

  @Test
  public void ttlMustNotBeNegativeReturnsError() {
    final CacheSetResponse stringSetResponse =
        client.set(cacheName, "hello", "", Duration.ofSeconds(-1)).join();
    assertThat(stringSetResponse).isInstanceOf(CacheSetResponse.Error.class);
    assertThat((CacheSetResponse.Error) stringSetResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);

    final CacheSetResponse byteBufferSetResponse =
        client.set(cacheName, "hello", ByteBuffer.allocate(1), Duration.ofSeconds(-1)).join();
    assertThat(byteBufferSetResponse).isInstanceOf(CacheSetResponse.Error.class);
    assertThat((CacheSetResponse.Error) byteBufferSetResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);

    final CacheSetResponse byteArraySetResponse =
        client.set(cacheName, new byte[] {}, new byte[] {}, Duration.ofSeconds(-1)).join();
    assertThat(byteArraySetResponse).isInstanceOf(CacheSetResponse.Error.class);
    assertThat((CacheSetResponse.Error) byteArraySetResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);
  }

  @Test
  public void nullCacheNameReturnsError() {
    final CacheGetResponse getResponse = client.get(null, "").join();
    assertThat(getResponse).isInstanceOf(CacheGetResponse.Error.class);
    assertThat((CacheGetResponse.Error) getResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);

    final CacheDeleteResponse deleteResponse = client.delete(null, "").join();
    assertThat(deleteResponse).isInstanceOf(CacheDeleteResponse.Error.class);
    assertThat((CacheDeleteResponse.Error) deleteResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);

    final CacheSetResponse setResponse = client.set(null, "", "", Duration.ofSeconds(10)).join();
    assertThat(setResponse).isInstanceOf(CacheSetResponse.Error.class);
    assertThat((CacheSetResponse.Error) setResponse)
        .hasCauseInstanceOf(InvalidArgumentException.class);
  }
}
