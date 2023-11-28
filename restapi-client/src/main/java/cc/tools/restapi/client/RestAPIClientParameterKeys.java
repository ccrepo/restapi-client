package cc.tools.restapi.client;

import java.util.*;

/**
 * This encapsulates Parameter key values
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class RestAPIClientParameterKeys {

  /**
   * Constructor for {@link RestAPIClientParameterKeys} is private amd not meant
   * to be used.
   */
  private RestAPIClientParameterKeys() {
  }

  /**
   * Method returns boolean indicating argument transformation succeeded or not.
   * Values in command line arguments 'args' variable will be transformed into
   * name/value pairs and deposited in results parameter.
   * 
   * @param args   String array containing command line arguments.
   * @param values Array for return of results.
   * @param errors Array for errors encountered during parameter loading.
   * @return {@link RestAPIClientConfig.VALUES_STATUS} enum designating status of
   *         load operation.
   */
  public static RestAPIClientConfig.VALUES_STATUS load(String[] args, Map<String, String> values,
      List<String> errors) {

    String name = null;

    for (int i = 0; i < args.length; i++) {

      String token = args[i].trim();

      if (token.compareToIgnoreCase("-h") == 0) {
        
        values.clear();
        errors.clear();
        
        return RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_HELP;
      }

      if (name == null || (!token.isEmpty() && token.charAt(0) == '-')) {
        
        token = token.toLowerCase();

        if (token.length() < 2) {
          
          errors.add("empty value at position " + Integer.toString(i) + " '" + args[i] + "'");
          
          return RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_ERROR;
        }

        if (token.charAt(0) != '-') {
          
          errors.add("ill-formed value at position " + Integer.toString(i) + " '" + args[i] + "'");
          
          return RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_ERROR;
        }

        name = token.substring(1);

        if (values.containsKey(name)) {
          
          errors.add("duplicate value at position " + Integer.toString(i) + " '" + args[i] + "'");
          
          return RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_ERROR;
        }

        if (!isValidParameterNameToken(name, errors)) {
          
          errors.add("invalid name at position " + Integer.toString(i) + " '" + args[i] + "'");
        
          return RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_ERROR;
        }

        values.put(name, "");
        
      } else {

        if (!isValidParameterValueToken(token, errors)) {
          
          errors.add("invalid name at position " + Integer.toString(i) + " '" + args[i] + "'");
          
          return RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_ERROR;
        }

        values.put(name, token);
        
        name = null;
      }
    }

    return RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_OK;
  }

  /**
   * Method returns a boolean indicating whether the parameter token is a valid
   * parameter name token.
   *
   * @param token  the String token value to be checked.
   * @param errors any errors are written to this variable.
   * @return boolean indicating whether parameter token is a valid input value or
   *         not.
   */
  private static boolean isValidParameterNameToken(String token, List<String> errors) {

    for (char c : token.toCharArray()) {

      if (!(Character.isLetterOrDigit(c) || c == '-' || c == '.')) {

        errors.add("invalid character '" + c + "' found in token '" + token + "'");

        return false;

      }
    }

    return true;
  }

  /**
   * Method returns a boolean indicating whether the parameter token is a valid
   * parameter value token.
   *
   * @param token  the String token value to be checked.
   * @param errors any errors are written to this variable.
   * @return boolean indicating whether parameter token is a valid input value or
   *         not.
   */
  private static boolean isValidParameterValueToken(String token, List<String> errors) {

    char[] chars = token.toCharArray();

    for (int i = 0; i < chars.length; i++) {

      int ascii = (int) chars[i];

      if (!(Character.isAlphabetic(ascii) || (ascii >= 32 && ascii <= 123) || ascii == 125 || ascii == 9)) {

        StringBuilder buffer = new StringBuilder();

        buffer.append("invalid character found in token at index '");
        buffer.append(i);
        buffer.append("'");

        if (i > 0) {
          buffer.append(" after value '");
          buffer.append(chars[i - 1]);
          buffer.append("'");
        }

        errors.add(buffer.toString());

        return false;
      }
    }

    return true;
  }

  /**
   * Parameter constant '{@value _KEY_ACTION}'.
   */
  final public static String _KEY_ACTION = "action";

  /**
   * HTTP timeout '{@value _KEY_COUNT}' seconds.
   **/
  final public static String _KEY_COUNT = "count";

  /**
   * Parameter constant '{@value _KEY_ENTITY}'.
   */
  final public static String _KEY_ENTITY = "entity";

  /**
   * Parameter constant '{@value _KEY_HOSTNAME}'.
   */
  final public static String _KEY_HOSTNAME = "hostname";

  /**
   * Parameter constant '{@value _KEY_LOG}'.
   */
  final public static String _KEY_LOG = "log";

  /**
   * Parameter constant '{@value _KEY_PAYLOAD}'.
   */
  final public static String _KEY_PAYLOAD = "payload";

  /**
   * Parameter constant '{@value _KEY_PORT}'.
   */
  final public static String _KEY_PORT = "port";

  /**
   * Parameter constant '{@value _KEY_PROTOCOL}'.
   */
  final public static String _KEY_PROTOCOL = "protocol";

  /**
   * HTTP timeout '{@value _KEY_SLEEP}' seconds.
   **/
  final public static String _KEY_SLEEP = "sleep";

  /**
   * Parameter constant '{@value _KEY_UNIQUE}'.
   */
  final public static String _KEY_UNIQUE = "unique";

  /**
   * Parameter constant '{@value _KEY_URL}'.
   */
  final public static String _KEY_URL = "url";

  /**
   * Parameter array containing all parameter constants.
   */
  final public static String[] _KEYS = new String[] { _KEY_PROTOCOL, 
      _KEY_HOSTNAME, 
      _KEY_PORT, 
      _KEY_URL, 
      _KEY_COUNT,
      _KEY_SLEEP, 
      _KEY_PAYLOAD,
      _KEY_ACTION,
      _KEY_ENTITY,
      _KEY_UNIQUE,
      _KEY_LOG };
}
