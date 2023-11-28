package cc.tools.restapi.client;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * This class implements http post action.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class RestAPIClientActionPost extends RestAPIClientAction {
 
  /**
   * Constructor.
   * 
   * @param config configualtion object for common data setup.
   * @param security functions and data object.
   */
  public RestAPIClientActionPost(RestAPIClientConfig config, RestAPIClientSecurity security) {

    super(config, security);
        
    _isValid = true;
  }

  /**
   * Method performs http put call to server and returns status of a http put call
   * and server response data in responseBuffer parameter.
   * 
   * @param url    server url to call.
   * @param buffer buffer to return server response to caller.
   * @param data   String data that should be sent to server.
   * @param i message number. used for output.
   * @throws Exception based on errors encountered formatting/encoding data and.
   *                   communicating with server.
   * @return int containing returned http status.
   *         {@link java.net.HttpURLConnection} (HTTP_OK
   *         {@value java.net.HttpURLConnection#HTTP_OK}, HTTP_BAD_REQUEST
   *         {@value java.net.HttpURLConnection#HTTP_BAD_REQUEST} etc). server
   *         response is returned in parameter buffer
   */
  private int post(String url, StringBuilder buffer, String data, long i) throws Exception {

    _logger.debug("[",
        i,
        "] post to url: ",
        url);

    HttpRequest.BodyPublisher encoded = security().prepareData(data);

    if (encoded == null) {
      return -1;
    }
 
    HttpRequest request = HttpRequest.newBuilder().POST(encoded).uri(URI.create(url))
        .setHeader("User-Agent", this.getClass().getSimpleName() + "command line program")
        .header("Content-Type", "application/json").build();

    HttpResponse<String> httpResponse = httpClient().send(request, HttpResponse.BodyHandlers.ofString());

    String response = httpResponse.body();
    
    int code = httpResponse.statusCode();
    
    if (code == HttpURLConnection.HTTP_OK) { 
      
      _logger.debug("[",
          i,
          "] response: ",
          response);
    
      _logger.debug("[",
          i,
          "] code: ",
          code);

    } else {
      
      _logger.error("[",
          i,
          "] response: ",
          response);

      _logger.error("[",
          i,
          "] code: ",
          code);
    }

    buffer.append(response);

    return code;
  }
  
  /**
   * Method returns Post Url configuration..
   * 
   * @return {@link String} containing 'Post' endpoint Url.
   */
  private String getPostUrl() {
  
    StringBuilder buffer = new StringBuilder();
  
    buffer.append(baseURL());
  
    buffer.append(entity());
  
    return buffer.toString();
  }
  
  /*
   * Method to check if object is valid.
   */
  public boolean isValid() {
  
    return _isValid;
  }

  /**
   * Method sends configured message to the server.
   * 
   * @param i message number in this run.
   * @return boolean value set to true for success, false otherwise.
   */
  public boolean send(long i) {
  
    if (!_isValid) {
      
      return false;
    }
    
    StringBuilder buffer = new StringBuilder();
  
    try {
    
      int result = post(getPostUrl(), buffer, payload(), i);
  
      if (result < 0) {
      
        _logger.error("server post fai;ed ",
            Integer.toString(result));
  
        return false;
      }
      
      if (result != HttpURLConnection.HTTP_OK) {
        
        _logger.error("server call not ok - code ",
            Integer.toString(result));
        
        return false;
      }
      
    } catch (Exception e) {
  
      _logger.exception(e);
 
      return false;
    }
  
    return true;
  }

  /**
   * Method to access action name.
   * 
   * @return String containing action name.
   */
  public String action() {
    
    return RestAPIClientValues._ACTION_POST;
  }
  
  /**
   * Valid flag to test if object is logically valid after creation.
   */
  private final boolean _isValid;
  
  /**
   * Local logger reference for logging operations.
   */
  final private static RestAPIClientLogger _logger = new RestAPIClientLogger(RestAPIClientActionPost.class.getName());
}
