package cc.tools.restapi.client;

import java.util.*;

/**
 * This class manages program command line parameters and configuration values
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class RestAPIClientConfig extends RestAPIClientValues {

  /**
   * Enum VALUES_STATUS is used to indicate the status of the value object during
   * processing.
   */
  static public enum VALUES_STATUS {
    /**
     * Status value {@link VALUES_STATUS_OK} means 'Ok'
     */
    VALUES_STATUS_OK,
    /**
     * Status value {@link VALUES_STATUS_HELP} means 'Help'
     */
    VALUES_STATUS_HELP,
    /**
     * Status value {@link VALUES_STATUS_ERROR} means 'Error'
     */
    VALUES_STATUS_ERROR
  };

  /**
   * Constructor for {@link RestAPIClientConfig}.
   * 
   * @param args command line array containing parameter values.
   */
  public RestAPIClientConfig(String[] args) {

    super();

    super.load(args);

    if (getStatus() == RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_OK) {
      _isValid = true;
    }

  }

  /**
   * Method displays table showing configured values.
   */
  public void dump() {
    
    _logger.info("configuration:");
    _logger.info("-protocol:           " + getProtocol());
    _logger.info("-hostname:           " + getHostname());
    _logger.info("-port:               " + getPort());
    _logger.info("-url:                " + getURL());
    _logger.info("-payload:            " + getPayload());
    _logger.info("-action:             " + getAction());
    _logger.info("-entity:             " + getEntity());
    _logger.info("-count:              " + getCount());
    _logger.info("-sleep:              " + getSleep());
    _logger.info("-log:                " + getLog());
    _logger.info("-unique:             " + (getUnique() ? "true" : "false"));

  }

  /**
   * Method displays 'usage' help information showing command-line options.
   */
  public void help() {
  
    _logger.info("usage:");
    _logger.info(RestAPIClient.class.getName() + " [-protocol (http|https)] -hostname (ip|domain) [-port <port>] [-url <path>] -payload <payload> \\");
    _logger.info("                                     -action (delete|get|post|put) -log (debug|info|error|off) [-count <ms>] [-sleep <ms>] [-unique] ");
    _logger.info("-protocol:           optional.  set to http or https. default https.");
    _logger.info("-hostname:           mandatory. server host. can be an ip address or name.");
    _logger.info("-port:               optional.  server port. default 80/443 based on protocol.");
    _logger.info("-url:                mandatory. url server endpoint prefix.");
    _logger.info("-payload:            mandatory. payload to send to server.");
    _logger.info("-action:             mandatory. REST action. one of 'delete', 'get', 'post', 'put'.");
    _logger.info("-entity:             mandatory. REST entity. no spaces allowed.");
    _logger.info("-count:              optional.  number of payloads to be sent to server.");
    _logger.info("-sleep:              optional.  sleep pause (in ms) between log commands to server. default is 1000ms.");
    _logger.info("-log:                optional.  adjust output level. one of 'debug', 'info', 'error', 'off'. default 'info'");
    _logger.info("-unique:             optional.  toggle flag to generate individual payloads based on value in -payload field. default false.");
  }

  /**
   * Method returns {@code List<String>} containing configuration errors.
   * 
   * @return {@code List<String>} containing configuration errors.
   */
  public List<String> getErrors() {
    
    return _errors;
  }

  /**
   * Method returns boolean indicating whether command line parameters include
   * help '-h' flag.
   * 
   * @return boolean indicating whether help command line option is present.
   */
  public boolean isHelp() {
    
    return getStatus() == RestAPIClientConfig.VALUES_STATUS.VALUES_STATUS_HELP;
  }

  /**
   * Method returns {@link #_isValid} value.
   * 
   * @return boolean indicating whether configuration is valid or not.
   */
  public boolean isValid() {
    
    return _isValid;
  }

  /**
   * Configured flag indicating whether {@link RestAPIClientConfig} is valid or
   * not.
   */
  private boolean _isValid = false;
  
  /**
   * Local logger reference for logging operations.
   */
  final private static RestAPIClientLogger _logger = new RestAPIClientLogger(RestAPIClientConfig.class.getName());
}
