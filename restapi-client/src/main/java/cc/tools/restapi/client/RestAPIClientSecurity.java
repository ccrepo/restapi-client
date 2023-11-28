package cc.tools.restapi.client;

import java.lang.reflect.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import javax.crypto.*;

import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpRequest;

/**
 * This class implements security functionality to talk to an ActiveMQServer
 * instance.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class RestAPIClientSecurity {

  /**
   * Constructor {@link RestAPIClientSecurity};
   * 
   * @param config {@link RestAPIClientConfig} object containing program
   *               configuration values.
   */
  public RestAPIClientSecurity(RestAPIClientConfig config) {

    _config = config;

    if (_config == null || !_config.isValid()) {
      
      _logger.error("security failed");

      return;
    }

    if (!setKeyFactory()) {
      
      _logger.error("key factory create failed");

      return;
    }

    if (_keyFactory == null) {
      
      _logger.error("key factory init failed");
      
      return;
    }

    _isValid = true;
  }

  /**
   * Method Url encodes data to BodyPublisher transmission formst..
   * 
   * @param data name value pairs in Map to be encoded by method.
   * @return {@link HttpRequest.BodyPublisher} of encoded data.
   */
  public HttpRequest.BodyPublisher encodeData(Map<String, String> data) {

    StringBuilder buffer = new StringBuilder();

    for (Map.Entry<String, String> entry : data.entrySet()) {

      if (!buffer.isEmpty()) {
        buffer.append("&");
      }

      buffer.append(URLEncoder.encode(entry.getKey().toString(), 
          StandardCharsets.UTF_8));
      
      buffer.append("=");
      
      buffer.append(URLEncoder.encode(entry.getValue().toString(), 
          StandardCharsets.UTF_8));
    }

    return HttpRequest.BodyPublishers.ofString(buffer.toString());
  }
  
  /**
   * Method generates and returns random value. It uses {@link Math#random()} and
   * {@link java.time.Instant#toEpochMilli}.
   * 
   * @return String containing random value.
   */
  static public String getRandom() {
  
    StringBuilder buffer = new StringBuilder();
  
    buffer.append(Long.toString(Instant.now().toEpochMilli()));
    buffer.append("_");
    buffer.append(Double.toString(Math.random()));
  
    return buffer.toString();
  }

  /**
   * Method returns boolean indicating whether {@link RestAPIClientSecurity} object is in
   * a valid state.
   * 
   * @return boolean where true indicates {@link RestAPIClientSecurity} object is valid,
   *         false otherwise.
   */
  public boolean isValid() {
    return _isValid;
  }

  /**
   * Method Url prepares data in BodyPublisher transmission formst.
   * 
   * @param data String value be encoded by method.
   * @return {@link HttpRequest.BodyPublisher} of encoded data.
   */
  public HttpRequest.BodyPublisher prepareData(String data) {
  
    return HttpRequest.BodyPublishers.ofString(data);
  }

  /**
   * Method performs data encryption and returns {@link java.util.Base64} encoded
   * result.
   * 
   * @param publicKey {@link java.security.PublicKey} to be used to encrypt
   * @param text      text to be encrypted.
   * @return String containing {@link java.util.Base64} encoded text result of
   *         encryption if success, else null.
   */
  @SuppressWarnings("unused")
  private String encryptData(PublicKey publicKey, String text) {

    if (text == null || text.isBlank()) {

      _logger.error("no data to encrypt");

      return null;
    }

    try {

      Cipher cipher = Cipher.getInstance(_SECURITY_CYPHER_TRANSFORMATION);

      cipher.init(Cipher.ENCRYPT_MODE, publicKey);

      byte[] bytes = text.getBytes();

      byte[] finalized = cipher.doFinal(bytes);

      return Base64.getEncoder().encodeToString(finalized);

    } catch (Exception e) {

      _logger.exception(e);
    }

    return null;
  }


  /**
   * Method performs data decryption and returns plain-text using
   * {@link java.security.PrivateKey} parameter {@link java.security.PrivateKey}.
   * 
   * @param data       encrypted {@link java.util.Base64} text to be decrypted.
   * @param privateKey {@link java.security.PrivateKey} to be used to decrypt.
   * @return String containing plain text result of decryption if success, else
   *         null.
   */
  @SuppressWarnings("unused")
  private String decryptData(String data, PrivateKey privateKey) {

    if (data == null || data.isBlank()) {

      _logger.error("no data in deceypt data");

      return null;
    }

    try {

      Cipher cipher = Cipher.getInstance(_SECURITY_CYPHER_TRANSFORMATION);

      cipher.init(Cipher.DECRYPT_MODE, privateKey);

      return new String(cipher.doFinal(Base64.getDecoder().decode(data)));

    } catch (Exception e) {

      _logger.exception(e);

    }

    return null;
  }

  /**
   * Method {@link java.util.Base64} decodes data and returns name/value pairs in
   * {@link java.util.Map}. Values are the result of decoding parameter
   * base64EncodedData.
   * 
   * @param encoded {@link java.util.Base64} encoded name/value pairs.
   * @return {@link java.util.Map} containing decoded name/value pairs.
   */
  @SuppressWarnings("unused")
  private Map<String, String> decodeData(String encoded) {

    Map<String, String> data = new HashMap<String, String>();

    String[] pairs = encoded.split("&");

    for (String pair : pairs) {

      String[] tuple = pair.split("=");

      if (tuple.length == 1) {

        data.put(tuple[0], "");

      } else if (tuple.length == 2) {

        data.put(tuple[0], URLDecoder.decode(tuple[1], StandardCharsets.UTF_8));

      } else {

        _logger.info("skipped '",
            pair,
            "'");
      }
    }

    return data;
  }

  /**
   * Method loads {@link java.security.Key} from file based on parameter values.
   * 
   * @param text           buffer containing encoded key
   *                       {@link java.security.Key}.
   * @param keySpecClass   class indicating key type to create.
   * @param pattern        value to strip from header/footer of buffer.
   * @param generateMethod method name to use for generation.
   * @return created {@link java.security.Key} value if success, else null.
   */
  private Key getKeyFromText(String text, Class<?> keySpecClass, String pattern, String generateMethod) {

    byte[] decoded = Base64.getDecoder()
        .decode(text.replaceAll("\\n", "").replaceAll("-----BEGIN " + pattern + " KEY-----", "")
            .replaceAll("-----END " + pattern + " KEY-----", "").trim());

    try {
      Constructor<?> constructor = keySpecClass.getDeclaredConstructor(decoded.getClass());

      Object object = constructor.newInstance(new Object[] { decoded });

      if (object instanceof KeySpec) {

        KeySpec keySpec = (KeySpec) object;

        Method method = _keyFactory.getClass().getDeclaredMethod(generateMethod, KeySpec.class);

        Object object2 = method.invoke(_keyFactory, keySpec);

        if (object2 instanceof Key) {
          return (Key) object2;
        }
      }

    } catch (Exception e) {

      _logger.exception(e);
    }

    return null;
  }

  /**
   * Method reads and returns {@link java.security.PrivateKey} value from text
   * parameter.
   * 
   * @param text encoded {@link java.security.PrivateKey}.
   * @return {@link java.security.PublicKey} object created from text if success,
   *         else null.
   */
  @SuppressWarnings("unused")
  private PrivateKey getPrivateKeyFromText(String text) {

    if (text == null) {
      
      return null;
    }

    Key privateKey = getKeyFromText(text, PKCS8EncodedKeySpec.class, "PRIVATE", "generatePrivate");

    if (privateKey != null && 
        (privateKey instanceof PrivateKey)) {
      
      return (PrivateKey) privateKey;
    }

    StringBuilder buffer = new StringBuilder();

    buffer.append("could not create private key from '");
    buffer.append(text);
    buffer.append("'");

    _logger.error(buffer.toString());

    return null;
  }

  /**
   * Method reads and returns {@link java.security.PublicKey} value from text
   * parameter.
   * 
   * @param text encoded {@link java.security.PublicKey}.
   * @return {@link java.security.PublicKey} object created from text if success,
   *         els
   *         e null.
   */
  @SuppressWarnings("unused")
  private PublicKey getPublicKeyFromText(String text) {

    if (text == null) {
      return null;
    }

    Key publicKey = getKeyFromText(text, X509EncodedKeySpec.class, "PUBLIC", "generatePublic");

    if (publicKey != null && 
        (publicKey instanceof PublicKey)) {
      
      return (PublicKey) publicKey;
    }

    StringBuilder buffer = new StringBuilder();

    buffer.append("could not create public key from '");
    buffer.append(text);
    buffer.append("'");

    _logger.error(buffer.toString());

    return null;
  }

  /**
   * Method sets object's KeyFactory.
   * 
   * @return boolean if success, or null otherwise.
   */
  private boolean setKeyFactory() {
    try {

      _keyFactory = KeyFactory.getInstance(_SECURITY_KEY_ALGORITHM_RSA);

      return _keyFactory != null;
      
    } catch (NoSuchAlgorithmException e) {

      _logger.exception(e);
    }

    return false;
  }

  /**
   * Configuration object containing parameter settings.
   */
  private RestAPIClientConfig _config = null;

  /**
   * Boolean indicating whether this {@link RestAPIClientSecurity} object is in a
   * valid state.
   */
  private boolean _isValid = false;

  /**
   * KeyFactory for key based operations.
   */
  private KeyFactory _keyFactory = null;

  /**
   * {@link javax.crypto.Cipher} transformation type
   * '{@value _SECURITY_CYPHER_TRANSFORMATION}'.
   */
  final private static String _SECURITY_CYPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

  /**
   * {@link java.security.KeyFactory} algorithm
   * '{@value _SECURITY_KEY_ALGORITHM_RSA}'.
   */
  @SuppressWarnings("unused")
  final private static String _SECURITY_KEY_ALGORITHM_RSA = "RSA";
  
  /**
   * Local logger reference for logging operations.
   */
  final private static RestAPIClientLogger _logger = new RestAPIClientLogger(RestAPIClientSecurity.class.getName());
  
}
