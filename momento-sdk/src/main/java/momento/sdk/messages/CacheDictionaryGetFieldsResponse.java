package momento.sdk.messages;

import com.google.protobuf.ByteString;
import grpc.cache_client.ECacheResult;
import grpc.cache_client._DictionaryGetResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import momento.sdk.exceptions.CacheServiceExceptionMapper;
import momento.sdk.exceptions.SdkException;
import momento.sdk.internal.StringHelpers;

/** Response for a dictionary get fields operation */
public interface CacheDictionaryGetFieldsResponse {
  /**
   * A successful dictionary get fields operation that found the keys with values in the dictionary.
   */
  class Hit implements CacheDictionaryGetFieldsResponse {
    public final List<CacheDictionaryGetFieldResponse> responsesList = new ArrayList<>();

    /**
     * Constructs a dictionary get fields hit with a list of encoded keys and values.
     *
     * @param responses the retrieved dictionary.
     */
    public Hit(
        List<ByteString> fields,
        List<_DictionaryGetResponse._DictionaryGetResponsePart> responses) {

      int counter = 0;
      for (_DictionaryGetResponse._DictionaryGetResponsePart element : responses) {
        if (element.getResult() == ECacheResult.Hit) {
          responsesList.add(
              new CacheDictionaryGetFieldResponse.Hit(fields.get(counter), element.getCacheBody()));
        } else if (element.getResult() == ECacheResult.Miss) {
          responsesList.add(new CacheDictionaryGetFieldResponse.Miss(fields.get(counter)));
        } else {
          responsesList.add(
              new CacheDictionaryGetFieldResponse.Error(
                  CacheServiceExceptionMapper.convert(
                      new Exception(element.getResult().toString())),
                  fields.get(counter)));
        }
        counter++;
      }
    }

    /**
     * Gets the retrieved dictionary of string keys and string values.
     *
     * @return the dictionary.
     */
    public Map<String, String> valueDictionaryStringString() {
      return responsesList.stream()
          .filter(r -> r instanceof CacheDictionaryGetFieldResponse.Hit)
          .collect(
              Collectors.toMap(
                  r -> ((CacheDictionaryGetFieldResponse.Hit) r).fieldString(),
                  r -> ((CacheDictionaryGetFieldResponse.Hit) r).valueString()));
    }

    /**
     * Gets the retrieved dictionary of string keys and string values.
     *
     * @return the dictionary.
     */
    public Map<String, String> valueDictionary() {
      return valueDictionaryStringString();
    }

    /**
     * Gets the retrieved dictionary of string keys and byte array values.
     *
     * @return the dictionary.
     */
    public Map<String, byte[]> valueDictionaryStringBytes() {
      return responsesList.stream()
          .filter(r -> r instanceof CacheDictionaryGetFieldResponse.Hit)
          .collect(
              Collectors.toMap(
                  r -> ((CacheDictionaryGetFieldResponse.Hit) r).fieldString(),
                  r -> ((CacheDictionaryGetFieldResponse.Hit) r).valueByteArray()));
    }

    /**
     * Gets the retrieved dictionary of byte array keys and string values.
     *
     * @return the dictionary.
     */
    public Map<byte[], String> valueDictionaryBytesString() {
      return responsesList.stream()
          .filter(r -> r instanceof CacheDictionaryGetFieldResponse.Hit)
          .collect(
              Collectors.toMap(
                  r -> ((CacheDictionaryGetFieldResponse.Hit) r).fieldByteArray(),
                  r -> ((CacheDictionaryGetFieldResponse.Hit) r).valueString()));
    }

    /**
     * Gets the retrieved dictionary of byte array keys and byte array values.
     *
     * @return the dictionary.
     */
    public Map<byte[], byte[]> valueDictionaryBytesBytes() {
      return responsesList.stream()
          .filter(r -> r instanceof CacheDictionaryGetFieldResponse.Hit)
          .collect(
              Collectors.toMap(
                  r -> ((CacheDictionaryGetFieldResponse.Hit) r).fieldByteArray(),
                  r -> ((CacheDictionaryGetFieldResponse.Hit) r).valueByteArray()));
    }

    @Override
    public String toString() {
      final String stringStringRepresentation =
          valueDictionaryStringString().entrySet().stream()
              .map(e -> e.getKey() + ":" + e.getValue())
              .limit(5)
              .map(StringHelpers::truncate)
              .collect(Collectors.joining(", ", "\"", "\"..."));

      final String bytesBytesRepresentation =
          valueDictionaryBytesBytes().entrySet().stream()
              .map(
                  e ->
                      Base64.getEncoder().encodeToString(e.getKey())
                          + ":"
                          + Base64.getEncoder().encodeToString(e.getValue()))
              .limit(5)
              .map(StringHelpers::truncate)
              .collect(Collectors.joining(", ", "\"", "\"..."));

      final String stringBytesRepresentation =
          valueDictionaryStringBytes().entrySet().stream()
              .map(e -> e.getKey() + ":" + Base64.getEncoder().encodeToString(e.getValue()))
              .limit(5)
              .map(StringHelpers::truncate)
              .collect(Collectors.joining(", ", "\"", "\"..."));

      final String bytesStringRepresentation =
          valueDictionaryBytesString().entrySet().stream()
              .map(e -> Base64.getEncoder().encodeToString(e.getKey()) + ":" + e.getValue())
              .limit(5)
              .map(StringHelpers::truncate)
              .collect(Collectors.joining(", ", "\"", "\"..."));

      return super.toString()
          + ": valueStringString: "
          + stringStringRepresentation
          + " valueByteBytes: "
          + bytesBytesRepresentation
          + " valueStringBytes: "
          + stringBytesRepresentation
          + " valueBytesString: "
          + bytesStringRepresentation;
    }
  }

  /**
   * A successful cache dictionary get fields operation for a key that does not exist in the
   * dictionary.
   */
  class Miss implements CacheDictionaryGetFieldsResponse {}

  /**
   * A failed cache dictionary get fields operation. The response itself is an exception, so it can
   * be directly thrown, or the cause of the error can be retrieved with {@link #getCause()}. The
   * message is a copy of the message of the cause.
   */
  class Error extends SdkException implements CacheDictionaryGetFieldsResponse {

    /**
     * Constructs a cache dictionary get fields error with a cause.
     *
     * @param cause the cause.
     */
    public Error(SdkException cause) {
      super(cause);
    }
  }
}
