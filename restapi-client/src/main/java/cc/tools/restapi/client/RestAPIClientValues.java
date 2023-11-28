package cc.tools.restapi.client;

import java.util.*;

/**
 * This encapsulates Parameter key values
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class RestAPIClientValues {

  /**
   * Constructor for {@link RestAPIClientValues}.
   */
  public RestAPIClientValues() {
  }

  /**
   * Method loads parameter settings from command line arguments.
   * 
   * @param args String array containing command line arguments.
   */
  void load(String[] args) {

    Map<String, String> values = new HashMap<String, String>();

    _status = RestAPIClientParameterKeys.load(args, values, _errors);

    if (_status != RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_OK) {

      return;
    }

    if (!setValues(values)) {

      _status = RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_ERROR;

      return;
    }

    _status = RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_OK;
  }

  /**
   * Method returns the status of {@link RestAPIClientConfig} object.
   * 
   * @return {@link Enum} {@link RestAPIClientConfig.VALUES_STATUS} value
   *         indicating status.
   */
  public RestAPIClientConfig.VALUES_STATUS getStatus() {
    return _status;
  }

  /**
   * Method returns the {@link #_action} configuration value.
   * 
   * @return String action to be sent to server.
   */
  protected String getAction() {
    
    return _action;
  }

  /**
   * Method returns the {@link #_count} configuration value.
   * 
   * @return count of the number of paylaods to send to the server..
   */
  protected long getCount() {
    
    return _count;
  }

  /**
   * Method returns the {@link #_entity} configuration value.
   * 
   * @return String action to be sent to server.
   */
  protected String getEntity() {
    
    return _entity;
  }

  /**
   * Method returns {@link #_hostname} configuration value.
   * 
   * @return {@link String} containing parameter -hostname value.
   */
  protected String getHostname() {
    
    return _hostname;
  }

  /**
   * Method returns {@link #_log} configuration value.
   * 
   * @return String containg log level.
   */
  protected String getLog() {
    
    return _log;
  }

  /**
   * Method returns the {@link #_payload} configuration value.
   * 
   * @return String payload to be sent to server.
   */
  protected String getPayload() {
    
    return _payload;
  }

  /**
   * String Method returns {@link #_port} configuration value.
   * 
   * @return int containing configured -port value.
   */
  protected int getPort() {
    
    return _port;
  }

  /**
   * Method returns {@link #_protocol} configuration value.
   * 
   * @return {@link String} containing -protocol value.
   */
  protected String getProtocol() {
    
    return _protocol;
  }

  /**
   * Method returns the {@link #_sleep} configuration value.
   * 
   * @return int millisecond sleep between server calls..
   */
  protected int getSleep() {
    
    return _sleep;
  }

  /**
   * Method returns the {@link #_unique} configuration value.
   * 
   * @return boolean unique flag to be sent to server.
   */
  protected boolean getUnique() {
    
    return _unique;
  }

  /**
   * Method returns {@link #_url} configuration value.
   * 
   * @return {@link String} containing configured -url value.
   */
  protected String getURL() {
    
    return _url;
  }

  /**
   * Method to store 'mandatory parameter missing' error.
   * 
   * @param parameterName name of parameter to report.
   */
  private void logMissingParameterError(String parameterName) {
  
    StringBuilder buffer = new StringBuilder();
  
    buffer.append("mandatory parameter -");
    buffer.append(parameterName);
    buffer.append(" missing");
  
    _errors.add(buffer.toString());
  }

  /**
   * Method to store 'parameter value invalid' error.
   * 
   * @param parameterName name of parameter to report.
   */
  private void logInvalidParameterError(String parameterName) {
  
    StringBuilder buffer = new StringBuilder();
  
    buffer.append("parameter -");
    buffer.append(parameterName);
    buffer.append(" value invalid");
  
    _errors.add(buffer.toString());
  }

  /**
   * Method sets parameter field {@link _action} from
   * {@link RestAPIClientParameterKeys#_KEY_ACTION}
   *
   * @param values contains all loaded configuration parameter values.
   * @return boolean indicating success or fail.
   */
  private boolean setAction(Map<String, String> values) {
  
    if (values.containsKey(RestAPIClientParameterKeys._KEY_ACTION)) {
  
      _action = values.get(RestAPIClientParameterKeys._KEY_ACTION).toLowerCase();
  
      if (_action.compareTo(RestAPIClientValues._ACTION_DELETE) == 0 ||
          _action.compareTo(RestAPIClientValues._ACTION_GET)    == 0 ||
          _action.compareTo(RestAPIClientValues._ACTION_POST)   == 0 ||
          _action.compareTo(RestAPIClientValues._ACTION_PUT)    == 0) {
       
        return true;
        
      } else {
        
        logInvalidParameterError(RestAPIClientParameterKeys._KEY_ACTION);
      }
    } else {
      
      logMissingParameterError(RestAPIClientParameterKeys._KEY_ACTION);
    }
  
    return false;
  }

  /**
   * Method sets parameter field {@link _count} from
   * {@link RestAPIClientParameterKeys#_KEY_COUNT}
   *
   * @param values contains all loaded configuration parameter values.
   * @return boolean indicating success or fail.
   */
  private boolean setCount(Map<String, String> values) {
  
  long count = -1;
  
    if (values.containsKey(RestAPIClientParameterKeys._KEY_COUNT)) {
  
      String token = values.get(RestAPIClientParameterKeys._KEY_COUNT).trim();
  
      try {
  
        if (!token.isBlank() || token.length() < 8) {
          
          count = Long.parseLong(token);
        }
  
        if (count < 0) {
          
          count = 1;
        }
  
        if (count > 9999999) {
          
          count = 9999999;
        }
  
      } catch (NumberFormatException e) {
  
        _errors.add("-" + RestAPIClientParameterKeys._KEY_COUNT + " is invalid number");
  
        return false;
      }
  
    } else {
      
      count = 1;
  
      values.put(RestAPIClientParameterKeys._KEY_COUNT, "1");
    }
  
    _count = count;
  
    return true;
  }

  /**
   * Method sets parameter field {@link _entity} from
   * {@link RestAPIClientParameterKeys#_KEY_ENTITY}
   * 
   * @param values contains all loaded configuration parameter values.
   * @return boolean indicating success or fail.
   */
  private boolean setEntity(Map<String, String> values) {
  
    String entity = null;
    
    if (values.containsKey(RestAPIClientParameterKeys._KEY_ENTITY)) {
  
      entity = values.get(RestAPIClientParameterKeys._KEY_ENTITY).trim();
  
      if (entity.matches(_pattern_simple_name)) {
        
        _entity = entity;
        
        return true;
      
      } else {
       
        logInvalidParameterError(RestAPIClientParameterKeys._KEY_ENTITY);
      }
      
    } else {
  
      logMissingParameterError(RestAPIClientParameterKeys._KEY_ENTITY);
    }
  
    return false;
  }

  /**
   * Method sets parameter field {@link _hostname} from
   * {@link RestAPIClientParameterKeys#_KEY_HOSTNAME}
   * 
   * @param values contains all loaded configuration parameter values.
   * @return boolean indicating success or fail.
   */
  private boolean setHostname(Map<String, String> values) {
  
    if (values.containsKey(RestAPIClientParameterKeys._KEY_HOSTNAME)) {
  
      _hostname = values.get(RestAPIClientParameterKeys._KEY_HOSTNAME);
  
      return true;
    }
  
    logMissingParameterError(RestAPIClientParameterKeys._KEY_HOSTNAME);
  
    return false;
  }

  /**
   * Method sets parameter field {@link _log} from
   * {@value RestAPIClientParameterKeys#_KEY_LOG}.
   * 
   * @param values contains all loaded configuration parameter values._isValid
   * @return boolean indicating success or fail.
   */
  private boolean setLog(Map<String, String> values) {

    if (values.containsKey(RestAPIClientParameterKeys._KEY_LOG)) {

      String log = values.get(RestAPIClientParameterKeys._KEY_LOG);

      if (!_LOG_VALUES.contains(log)) {
      
        StringBuilder buffer = new StringBuilder();

        buffer.append("parameter -");
        buffer.append(RestAPIClientParameterKeys._KEY_LOG);
        buffer.append(" has invalid value '");
        buffer.append(log);
        buffer.append("'");
        
        _errors.add(buffer.toString());
        
        return false;
      }
      
      _log = log;
            
    } else {

      values.put(RestAPIClientParameterKeys._KEY_LOG, "info");

      _log = "info";
    }

    return true;
  }

  /**
   * Method sets parameter field {@link _payload} from
   * {@link RestAPIClientParameterKeys#_KEY_PAYLOAD}
   *
   * @param values contains all loaded configuration parameter values.
   * @return boolean indicating success or fail.
   */
  private boolean setPayload(Map<String, String> values) {

    boolean inverted = getAction().compareTo(RestAPIClientValues._ACTION_POST) != 0;
    
    boolean present = values.containsKey(RestAPIClientParameterKeys._KEY_PAYLOAD);
    
    if (present) {

      if (!inverted) {
        
        _payload = values.get(RestAPIClientParameterKeys._KEY_PAYLOAD);

        return true;      
      }
      
      logInvalidParameterError(RestAPIClientParameterKeys._KEY_PAYLOAD);
        
      return false;
    }

    if (!inverted) {

      logMissingParameterError(RestAPIClientParameterKeys._KEY_PAYLOAD);
      
      return false;
    }
    
    values.put(RestAPIClientParameterKeys._KEY_PAYLOAD, " ");
    
    return true;
  }

  /**
   * Method sets parameter field {@link _port} from
   * {@value RestAPIClientParameterKeys#_KEY_PORT} Port value must be more than 0.
   * parameter. Port defaults to default port for protocol setting - either '80'
   * or '443'.
   * 
   * @param values contains all loaded configuration parameter values.
   * @return boolean indicating success or fail.
   */
  private boolean setPort(Map<String, String> values) {

    int port = -1;

    if (_protocol.compareTo("http") == 0) {

      port = 80;

    } else if (_protocol.compareTo("https") == 0) {

      port = 443;

    } else {

      StringBuilder buffer = new StringBuilder();

      buffer.append("internal error ");
      buffer.append(RestAPIClientParameterKeys._KEY_PROTOCOL);
      buffer.append(" must be set to 'http' or 'https' before ");
      buffer.append(RestAPIClientParameterKeys._KEY_PORT);
      buffer.append(" can be set");

      _errors.add(buffer.toString());

      return false;
    }

    if (values.containsKey(RestAPIClientParameterKeys._KEY_PORT)) {
      try {

        port = Integer.parseInt(values.get(RestAPIClientParameterKeys._KEY_PORT));

      } catch (NumberFormatException e) {

        _errors.add("-" + RestAPIClientParameterKeys._KEY_PORT + " is invalid number");

        return false;
      }
    } else {
      values.put(RestAPIClientParameterKeys._KEY_PORT, Integer.toString(port));
    }

    _port = port;

    if (_port < 0) {

      StringBuilder buffer = new StringBuilder();

      buffer.append("-");
      buffer.append(RestAPIClientParameterKeys._KEY_PORT);
      buffer.append(" is not a valid port number - found ");
      buffer.append(_port);
      buffer.append(" from '");
      buffer.append(values.get(RestAPIClientParameterKeys._KEY_PORT));
      buffer.append("' and '");
      buffer.append(_port);
      buffer.append("'");

      _errors.add(buffer.toString());

      return false;
    }

    return true;
  }

  /**
   * Method sets parameter field {@link _protocol} from
   * {@value RestAPIClientParameterKeys#_KEY_PROTOCOL}. Can be 'http' or 'https'.
   * 
   * @param values contains all loaded configuration parameter values.
   * @return boolean indicating success or fail.
   */
  private boolean setProtocol(Map<String, String> values) {

    String protocol = null;

    if (values.containsKey(RestAPIClientParameterKeys._KEY_PROTOCOL)) {
      
      protocol = values.get(RestAPIClientParameterKeys._KEY_PROTOCOL);

      if (protocol.compareTo("http") != 0 && protocol.compareTo("https") != 0) {
        
        _errors.add("-" + RestAPIClientParameterKeys._KEY_PROTOCOL + " must be value 'http' or 'https'");
        
        return false;
      }

    } else {
      
      protocol = "https";
      
      values.put(RestAPIClientParameterKeys._KEY_PROTOCOL, protocol);
    }

    _protocol = protocol;

    return true;
  }

  /**
   * Method sets parameter field {@link _sleep} from
   * {@link RestAPIClientParameterKeys#_KEY_SLEEP}
   *
   * @param values contains all loaded configuration parameter values.
   * @return boolean indicating success or fail.
   */
  private boolean setSleep(Map<String, String> values) {
  
    int sleep = -1;
  
    if (values.containsKey(RestAPIClientParameterKeys._KEY_SLEEP)) {
  
      String token = values.get(RestAPIClientParameterKeys._KEY_SLEEP).trim();
  
      try {
  
        if (!token.isBlank() || token.length() < 8) {
          
          sleep = Integer.parseInt(token);
        }
  
        if (sleep < 0) {
          
          sleep = 0;
        }
  
        if (sleep > 9999999) {
          
          sleep = 9999999;
        }
  
      } catch (NumberFormatException e) {
  
        _errors.add("-" + RestAPIClientParameterKeys._KEY_SLEEP + " is invalid number");
  
        return false;
      }
  
    } else {
      
      sleep = 0;
  
      values.put(RestAPIClientParameterKeys._KEY_SLEEP, "1000");
    }
  
    _sleep = sleep;
  
    return true;
  }

  /**
   * Method sets parameter field {@link _unique} from
   * {@value RestAPIClientParameterKeys#_KEY_UNIQUE}.
   * 
   * @param values contains all loaded configuration parameter values._isValid
   * @return boolean indicating success or fail.
   */
  private boolean setUnique(Map<String, String> values) {
  
    if (values.containsKey(RestAPIClientParameterKeys._KEY_UNIQUE)) {
  
      String value = values.get(RestAPIClientParameterKeys._KEY_UNIQUE);
  
      if (!(value == null || value.isBlank() || value.isEmpty())) {
  
        StringBuilder buffer = new StringBuilder();
  
        buffer.append("flag parameter -");
        buffer.append(RestAPIClientParameterKeys._KEY_UNIQUE);
        buffer.append(" should not have a value");
  
        _errors.add(buffer.toString());
  
        return false;
      }
  
      _unique = true;
  
      return true;
  
    } else {
  
      values.put(RestAPIClientParameterKeys._KEY_UNIQUE, "");
  
    }
  
    _unique = false;
  
    return true;
  }

  /**
   * Method sets parameter field {@link _url} from
   * {@value RestAPIClientParameterKeys#_KEY_URL}.
   * 
   * @param values contains all loaded configuration parameter values.
   * @return boolean indicating success or fail.
   */
  private boolean setURL(Map<String, String> values) {

    if (values.containsKey(RestAPIClientParameterKeys._KEY_URL)) {

      _url = values.get(RestAPIClientParameterKeys._KEY_URL);

      return true;
      
    } 
    
    logMissingParameterError(RestAPIClientParameterKeys._KEY_URL);
      
    return false;
  }

  /**
   * Method loads configuration from file and returns boolean indicating result of
   * loading and processing command line arguments.
   * 
   * @param values {@code Map<String, String>} containing command line name/value
   *               pairs.
   * 
   * @return boolean indicating whether load was successful.
   */
  private boolean setValues(Map<String, String> values) {
 
    // NOTE: order is significant. check function content before moving.
    if (!setAction(values) | !setLog(values) | !setHostname(values) | !setProtocol(values) | !setPort(values) | !setPayload(values)
        | !setEntity(values) | !setCount(values) | !setSleep(values) | !setURL(values) | !setUnique(values)) {
  
      return false;
    }
  
    Set<String> inKeys = new HashSet<String>(values.keySet());
    
    Set<String> outKeys = new HashSet<String>(Arrays.asList(RestAPIClientParameterKeys._KEYS));
  
    inKeys.removeAll(outKeys);
    
    outKeys.removeAll(values.keySet());
  
    if (inKeys.isEmpty() && outKeys.isEmpty()) {
      
      return true;
    }
  
    for (String key : inKeys) {
      
      _errors.add("parameter not recognized '" + key + "'");
    }
  
    for (String key : outKeys) {
      
      _errors.add("parameter not set '" + key + "'");
    }
  
    return false;
  }

  /**
   * {@code List<String>} containing a list of errors encountered during parameter
   * processing.
   */
  protected List<String> _errors = new ArrayList<String>();

  /**
   * Valid values for {@link _log} log setting.
   */
  private static List<String> _LOG_VALUES = new ArrayList<String>() { private static final long serialVersionUID = 1L;
  {
    add("debug");
    add("info");
    add("error");
    add("off");
  } };

  /**
   * Action name for server data.
   */
  private String _action = "";

  /**
   * Configured number of payloads to send to server.
   */
  private long _count = -1;

  /**
   * Entity name or server data.
   */
  private String _entity = "";

  /**
   * Configured server name. Use Ip address (IPv4 only) or name.
   */
  private String _hostname = "";

  /**
   * Configured value for additional log output.
   */
  private String _log = "";

  /**
   * Payload to be sent to, and logged, by the remote service.
   */
  private String _payload = "";

  /**
   * Configured server port.
   */
  private int _port = -1;

  /**
   * Configured connection protocol. Must be either 'http' or 'https'.
   */
  private String _protocol = "";

  /**
   * Configured intra-payload send sleep.
   */
  private int _sleep = -1;

  /**
   * Processing status.
   */
  private RestAPIClientConfig.VALUES_STATUS _status = RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_ERROR;

  /**
   * Configured unique parameter. Used to indicate whether payload should be
   * transformed before sending.
   */
  private boolean _unique = false;

  /**
   * Configured servlet Url prefix value.
   */
  private String _url = "";

  /**
   * Pattern for simple name checks.
   */
  private static final String _pattern_simple_name = "^[a-z\\-0-9\\/]+$";
  
  /**
   * Action delete constant.
   */
  static public final String _ACTION_DELETE = "delete";
  
  /**
   * Action get constant.
   */
  static public final String _ACTION_GET    = "get";
  
  /**
   * Action post constant.
   */
  static public final String _ACTION_POST   = "post";
  
  /**
   * Action put constant.
   */
  static public final String _ACTION_PUT    = "put";
  
}
