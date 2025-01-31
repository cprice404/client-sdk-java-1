package momento.sdk.messages;

import momento.sdk.exceptions.SdkException;

/**
 * Parent response type for a list remove value request. The response object is resolved to a
 * type-safe object of one of the following subtypes:
 *
 * <p>{Success}, {Error}
 */
public interface CacheListRemoveValueResponse {

  /** A successful list remove value operation. */
  class Success implements CacheListRemoveValueResponse {}

  /**
   * A failed list remove value operation. The response itself is an exception, so it can be
   * directly thrown, or the cause of the error can be retrieved with {@link #getClass()} ()}. The
   * message is a copy of the message of the cause.
   */
  class Error extends SdkException implements CacheListRemoveValueResponse {

    /**
     * Constructs a list remove value error with a cause.
     *
     * @param cause the cause.
     */
    public Error(SdkException cause) {
      super(cause);
    }
  }
}
