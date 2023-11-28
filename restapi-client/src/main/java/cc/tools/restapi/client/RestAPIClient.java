package cc.tools.restapi.client;

/**
 * This is a REST command line utility.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class RestAPIClient {

  /**
   * Main method.
   * 
   * @param args program arguments.
   */
  public static void main(String[] args) {

    RestAPIClientConfig config = new RestAPIClientConfig(args);

    if (config.isHelp()) {
      
      config.help();

      return;
    }

    if (!config.isValid()) {

      RestAPIClient.logger().error("invalid parameters ", config.getErrors().size() + " errors");

      for (String error : config.getErrors()) {

        RestAPIClient.logger().error("invalid parameters ", error);
      }

      RestAPIClient.logger().unbuffer_errors();
      
      return;
    }

    RestAPIClient.logger(config).info("start pid " + RestAPIClient.getPid());

    config.dump();

    RestAPIClient client = new RestAPIClient(config);

    if (!client.isValid()) {
      
      RestAPIClient.logger().error("client failed to initialise");    
    
    } else if (!client.sendToServer()) {

      RestAPIClient.logger().error("client send to server failed");
    }

    RestAPIClient.logger().info("stop");
  }

  /**
   * Constructor {@link RestAPIClient}.
   * 
   * @param config {@link RestAPIClientConfig} object containing program
   *               configuration values.
   */
  public RestAPIClient(RestAPIClientConfig config) {

    _config = config;

    if (_config == null || !_config.isValid()) {
      
      _logger.error("config not value");
      
      return;
    }

    _security = new RestAPIClientSecurity(_config);

    if (!_security.isValid()) {
      
      _logger.error("security not value");
      
      return;
    }

    _isValid = true;
  }

  /**
   * Method to get process PID as a string.
   * 
   * @return String containing process pid.
   */
  static public String getPid() {
    return _pid;
  }

  /**
   * Method returns boolean indicating whether {@link RestAPIClient} object is in
   * a valid state.
   * 
   * @return boolean where true indicates {@link RestAPIClient} object is valid,
   *         false otherwise.
   */
  public boolean isValid() {

    return _isValid;
  }

  /**
   * Method returns reference to logger object.
   * 
   * @return RestAPIClientLogger logger.
   */
  static public RestAPIClientLogger logger() {
    return _logger;
  }

  /**
   * Method returns reference to logger object.
   * 
   * @param config configuration object.
   * @return RestAPIClientLogger logger.
   */
  static public RestAPIClientLogger logger(RestAPIClientConfig config) {
    
    if (config.isValid()) {
    
      _logger.unbuffer(config.getLog());
    }
    
    return _logger;
  }

  /**
   * Method to send data to server.
   * 
   * @return boolean if success, fail otherwise.
   */
  public boolean sendToServer() {
  
    String action = _config.getAction();
    
    if (action.compareTo(RestAPIClientValues._ACTION_DELETE) == 0) {
  
      //if (send(new RestAPIClientActionDelete(_config, _security))) {
      //
      //  return true;
      //}
  
    } else if (action.compareTo(RestAPIClientValues._ACTION_GET) == 0) {
      
      if (send(new RestAPIClientActionGet(_config, _security))) {
        
        return true;
      }      
  
    } else if (action.compareTo(RestAPIClientValues._ACTION_POST) == 0) {
      
      if (send(new RestAPIClientActionPost(_config, _security))) {
  
        return true;
      }      
  
    } else if (action.compareTo(RestAPIClientValues._ACTION_PUT) == 0) {
      
      //if (send(new RestAPIClientActionPut(_config, _security))) {
      //
      //  return true;
      //}
  
    } else {
    
      RestAPIClient.logger().error("send to server failed");
    }
    
    RestAPIClient.logger().error(action + " to server failed");
  
    return false;
  }

  /**
   * Method to sleep in production loop.
   */
  private void pause() {
  
    try {
  
      Thread.sleep(_config.getSleep());
      
    } catch (Exception e) {
  
      _logger.exception(e);
    }
  }

  /**
   * Method sends configured message to the server vis action parameter.
   * 
   * @param actionI RestAPIClientActionI interface object used to peform action.
   * @return boolean value set to true for success, false otherwise.
   */
  private boolean send(RestAPIClientActionI actionI) {
  
    long count = _config.getCount();
  
    if (!actionI.isValid()) {

      _logger.error("actionI invalid");
      
      return false;
    }
    
    sendToServerHeader(count);
        
    long i = 0;
    
    if (i < count || count == 0) {
  
      while (true) {
  
        _logger.debug("[",
            i + 1,
            "] start");
  
        if (!actionI.send(i+1)) {
  
          _logger.error(actionI.action() + " to server failed");
  
          return false;
        }
  
        sendToServerUpdate(i+1, count, "ok");
        
        if (++i >= count && count != 0) {
          
          break;
        }
        
        pause();
      }
    }
    
    return true;    
  }

  /**
   * Method prints footer for send server action.
   * 
   * @param i send number.
   * @param count target number of sends.
   * @param status output string.
   */
  private void sendToServerUpdate(long i, long count, String status) {
    
    if (_logger.isOn(RestAPIClientLoggerImpl._LOG_LEVEL._LOG_DEBUG)) { 
        
      if (count == 0) {
  
        _logger.debug("[",
            i,
            "] sent ",
            status);
  
      } else {
  
        _logger.debug("[",
            i,
            "/",
            count,
            "] sent ",
            status);
      }      
    } else {
      
      _logger.info('.');
    }
  }

  /**
   * Method prints header for send server action.
   * 
   * @param count number of messages to be sent. 0 indicates continuous.
   */
  private void sendToServerHeader(long count) {
  
    _logger.info("sending to server ..");
  
    if (count == 0) {
  
      _logger.info("will send continuously every ", 
          _config.getSleep(),
          " ms");
  
    } else {
  
      if (count == 1) {
        
        _logger.info("will send ",
            count);
        
      } else {
        
        _logger.info("will send ",
            count,
            " total. 1 every ",
            _config.getSleep(),
            " ms");
      }
    }    
  }

  /**
   * Configuration object containing parameter settings.
   */
  private RestAPIClientConfig _config = null;

  /**
   * Boolean indicating whether this {@link RestAPIClient} object is in a valid
   * state.
   */
  private boolean _isValid = false;

  /**
   * Security object for key based operations.
   */
  private RestAPIClientSecurity _security = null;

  /**
   * Local logger reference for logging operations.
   */
  final private static RestAPIClientLogger _logger = new RestAPIClientLogger(RestAPIClient.class.getName());

  /**
   * Stores PID of current process.
   */
  private static final String _pid = Long.toString(ProcessHandle.current().pid());
}
