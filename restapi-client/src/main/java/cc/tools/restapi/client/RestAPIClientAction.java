package cc.tools.restapi.client;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * This class implements common http delete|get|post|put actions and data.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
abstract class RestAPIClientAction implements RestAPIClientActionI {

  /**
   * Constructor
   * 
   * @param config configualtion object for common data setup.
   * @param security functions and data object.
   */
  protected RestAPIClientAction(RestAPIClientConfig config, RestAPIClientSecurity security) {
    
    _payload = config.getPayload();

    _entity = config.getEntity();

    _baseURL = getBaseUrl(config);
    
    _security = security;
  }
  
  /**
   * Method returns baseURL value.
   * 
   * @return String containing base URL.
   */
  protected String baseURL() {
    
    return _baseURL;
  }

  /**
   * Method returns payload value.
   * 
   * @return String containing payload.
   */
  protected String payload() {
    
    return _payload;
  }

  /**
   * Method returns entity value.
   * 
   * @return String containing entity name/
   */
  protected String entity() {
    
    return _entity;
  }

  /**
   * Method to get security function object.
   * 
   * @return RestAPIClientSecurity object.
   */
  protected RestAPIClientSecurity security() {
  
    return _security;
  }
  
  /**
   * Method returns server's base Url value.
   * 
   * @param config configuration object.
   * @return String base URL value.
   */
  private String getBaseUrl(RestAPIClientConfig config) {
  
    StringBuilder buffer = new StringBuilder();
  
    buffer.append(config.getProtocol());
    buffer.append("://");
    buffer.append(config.getHostname());
    buffer.append(":");
    buffer.append(Integer.toString(config.getPort()));
    buffer.append(config.getURL());
  
    return buffer.toString();
  }

  /**
   * Method HttpClient access function.
   * @return HttpClient.
   */
  public HttpClient httpClient() {
    
    return _httpClient;
  }

  /**
   * Server's base URI from configuration.
   */
  private final String _baseURL;

  /**
   * Message payload.
   */
  private final String _payload;

  /**
   * Target entity on server.
   */
  private final String _entity;

  /**
   * Security function object.
   */
  private final RestAPIClientSecurity _security;
  
  /**
   * {@link java.net.http.HttpClient} object for calls to server.
   */
  private final HttpClient _httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
      .connectTimeout(Duration.ofSeconds(_HTTP_TIMEOUT_SECONDS)).build();

  /**
   * HTTP timeout '{@value _HTTP_TIMEOUT_SECONDS}' seconds.
   **/
  private final static int _HTTP_TIMEOUT_SECONDS = 10;

}


