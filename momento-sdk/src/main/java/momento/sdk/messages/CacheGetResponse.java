package momento.sdk.messages;

import com.google.protobuf.ByteString;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import momento.sdk.exceptions.SdkException;
import momento.sdk.internal.StringHelpers;

/** Response for a cache get operation */
public interface CacheGetResponse {

  /** A successful get operation for a key that has a value. */
  class Hit implements CacheGetResponse {
    private final ByteString value;

    /**
     * Constructs a cache get hit with an encoded value.
     *
     * @param value the retrieved value.
     */
    public Hit(ByteString value) {
      this.value = value;
    }

    /**
     * Gets the retrieved value as a byte array.
     *
     * @return the value.
     */
    public byte[] valueByteArray() {
      return value.toByteArray();
    }

    /**
     * Gets the retrieved value as a UTF-8 {@link String}
     *
     * @return the value.
     */
    public String valueString() {
      return value.toString(StandardCharsets.UTF_8);
    }

    /**
     * Gets the retrieved value as a UTF-8 {@link String}
     *
     * @return the value.
     */
    public String value() {
      return valueString();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Truncates the internal fields to 20 characters to bound the size of the string.
     */
    @Override
    public String toString() {
      return super.toString()
          + ": valueString: \""
          + StringHelpers.truncate(valueString())
          + "\" valueByteArray: \""
          + StringHelpers.truncate(Base64.getEncoder().encodeToString(valueByteArray()))
          + "\"";
    }
  }

  /** A successful get operation for a key that has no value. */
  class Miss implements CacheGetResponse {}

  /**
   * A failed get operation. The response itself is an exception, so it can be directly thrown, or
   * the cause of the error can be retrieved with {@link #getCause()}. The message is a copy of the
   * message of the cause.
   */
  class Error extends SdkException implements CacheGetResponse {

    /**
     * Constructs a cache get error with a cause.
     *
     * @param cause the cause.
     */
    public Error(SdkException cause) {
      super(cause);
    }
  }
}
