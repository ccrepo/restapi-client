package cc.tools.restapi.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class implements logging single access.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public class RestAPIClientLogger {

  /**
   * Constructor {@link RestAPIClientLogger}.
   * 
   * @param name String containing logger name.
   */
  public RestAPIClientLogger(String name) {

    _name = name;
  }
  
  /**
   * Method output error level text.
   * 
   * @param os objects to be output.
   */
  public void error(Object... os) {

    logger().error(_name, " ", logger().concat(os));
  }

  /**
   * Method output info level text.
   * 
   * @param os objects to be output.
   */
  public void info(Object... os) {

    logger().info(_name, " ", logger().concat(os));
  }

  /**
   * Method output debug level text.
   * 
   * @param os objects to be output.
   */
  public void debug(Object... os) {

    logger().debug(_name, " ", logger().concat(os));
  }

  /**
   * Method output a raw character.
   * 
   * @param c chracter to be output.
   */
  public void info(char c) {

    logger().standard_output(c, false);
  }
  
  /**
   * Method output exception text.
   * 
   * @param e exception to be output.
   */
  public void exception(Exception e) {

    logger().exception(e);
  }

  /**
   * Print all buffered error rows.
   */
  public void unbuffer_errors() {

    logger().unbuffer("error");
  }

  /**
   * Print all buffered  rows.
   * 
   * @param level log level to use for output filtering.
   */
  public void unbuffer(String level) {

    logger().unbuffer(level);
  }
  
  /**
   * Method returns a boolean to indicate whether the log level in parameter is
   * active.
   * 
   * @param level value to be tested.
   * @return boolean true indicating yes, false no.
   */
  public boolean isOn(RestAPIClientLoggerImpl._LOG_LEVEL level) {

    return logger().isOn(level);
  }
  
  /**
   * Logger simgleton access function.
   * 
   * @return RestAPIClientLoggerImpl logging implementation object.
   */
  private RestAPIClientLoggerImpl logger() {
    return _logger;
  }
  
  /**
   * Logger name. Normally owning class name.
   */
  private String _name = null;
  
  /**
   * Logger singleton.
   */
  private static RestAPIClientLoggerImpl _logger = new RestAPIClientLoggerImpl();
}

/**
 * This class implements logging.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
class RestAPIClientLoggerImpl {

  /**
   * Constructor {@link RestAPIClientLoggerImpl}.
   */
  public RestAPIClientLoggerImpl() {
  }
  
  /**
   * Enum _LOG_LEVEL is used to indicate log level
   */
  public enum _LOG_LEVEL {

    /**
     * Status value {@link _LOG_NONE} indicates no level.
     */
    _LOG_NONE(0),
    /**
     * Status value {@link _LOG_OFF} indicates off level.
     */
    _LOG_OFF(1),
    /**
     * Status value {@link _LOG_ERROR} indicates error level.
     */
    _LOG_ERROR(2),
    /**
     * Status value {@link _LOG_INFO} indicates info level.
     */
    _LOG_INFO(3),
    /**
     * Status value {@link _LOG_DEBUG} indicates debug level.
     */
    _LOG_DEBUG(4);

    /**
     * Enum number.
     */
    private int numVal;

    /**
     * Constructor
     * 
     * @param numVal enum number.
     */
    _LOG_LEVEL(int numVal) {
      this.numVal = numVal;
    }

    /**
     * Method to return enumm number.
     * @return int enum numver value.
     */
    public int getNumVal() {
      return numVal;
    }
  }

  /**
   * Print buffered rows with config check filtered on level parameter.
   * 
   * @param level the log level to used to filter output.
   */
  public void unbuffer(String level) {

    if (_level == _LOG_LEVEL._LOG_NONE) {

      if (level.compareTo("debug") == 0) {

        _level = _LOG_LEVEL._LOG_DEBUG;

      } else if (level.compareTo("info") == 0) {

        _level = _LOG_LEVEL._LOG_INFO;

      } else if (level.compareTo("error") == 0) {

        _level = _LOG_LEVEL._LOG_ERROR;

      } else if (level.compareTo("off") == 0) {

        _level = _LOG_LEVEL._LOG_OFF;
      }
    }

    for (Map.Entry<Integer, String> entry : _buffer) {

      if (isSet()) {

        if (isOn(entry.getKey())) {

          error_output(entry.getValue(), true);
        }
      }
    }
  }

  /**
   * Method returns a boolean to indicate whether the configured log level has
   * been set.
   * 
   * @return boolean true indicating yes, false no.
   */
  private boolean isSet() {

    return _level.getNumVal() != _LOG_LEVEL._LOG_NONE.getNumVal();
  }

  /**
   * Method returns a boolean to indicate whether the log level in parameter is
   * active.
   * 
   * @param level value to be tested.
   * @return boolean true indicating yes, false no.
   */
  public boolean isOn(_LOG_LEVEL level) {

    return isSet() && isOn(level.getNumVal());
  }

  /**
   * Method returns a boolean to indicate whether the log level in parameter is
   * active.
   * 
   * @param level value to be tested.
   * @return boolean true indicating yes, false no.
   */
  private boolean isOn(Integer level) {

    return isSet() && _level.getNumVal() >= level;
  }

  /**
   * Method output info level text.
   * 
   * @param os objects to be output.
   */
  public void info(Object... os) {

    if (!isSet()) {

      _buffer.add(
          new AbstractMap.SimpleEntry<>(_LOG_LEVEL._LOG_INFO.getNumVal(), 
              concat(this.prefix(), _info, concat(os))));

    } else if (isOn(_LOG_LEVEL._LOG_INFO)) {

      standard_output(concat(this.prefix(), _info, concat(os)), true);
    }
  }

  /**
   * Method output info level text.
   * 
   * @param os objects to be output.
   */
  public void debug(Object... os) {

    if (!isSet()) {

      _buffer.add(new AbstractMap.SimpleEntry<>(_LOG_LEVEL._LOG_DEBUG.getNumVal(),
          concat(this.prefix(), _debug, concat(os))));
    
    } else if (isOn(_LOG_LEVEL._LOG_DEBUG)) {

      standard_output(concat(this.prefix(), _debug, concat(os)), true);
    }
  }

  /**
   * Method output error level text.
   * 
   * @param os objects to be output.
   */
  public void error(Object... os) {

    if (!isSet()) {

      _buffer.add(new AbstractMap.SimpleEntry<>(_LOG_LEVEL._LOG_ERROR.getNumVal(),
          concat(this.prefix(), _error, concat(os))));

    } else if (isOn(_LOG_LEVEL._LOG_ERROR)) {

      error_output(concat(this.prefix(), _error, concat(os)), true);
    }
  }

  /**
   * Method that outputs data to stderr.
   * 
   * @param data to be output.
   * @param newline flag indicating whether newline should be output.
   */
  private void error_output(String data, boolean newline) {

    if (newline) {

      System.err.println(data);
    } else {
      
      System.err.print(data);
    }
  }

  /**
   * Method that outputs data to stdout.
   * 
   * @param data data to be output.
   * @param newline flag indicating whether newline should be output.
   */
  private void standard_output(String data, boolean newline) {

    if (newline) {
      
      System.out.println(data);
    } else {
      
      System.out.print(data);      
    }
  }

  /**
   * Method that outputs data to stdout.
   * 
   * @param c char to be output.
   * @param newline flag indicating whether newline should be output.
   */
  public void standard_output(char c, boolean newline) {

    if (newline) {
      
      System.out.println(c);
    } else {
      
      System.out.print(c);      
    }
  }

  /**
   * Method to collate all strings.
   * 
   * @param os objects to be output.
   * @return collated string.
   */
  public String concat(Object... os) {

    StringBuilder buffer = new StringBuilder();

    for (Object o : os) {

      buffer.append(o.toString());
    }

    return buffer.toString();
  }

  /**
   * Method to collate all strings.
   * 
   * @param os strings to be output.
   * @return collated string.
   */
  public String concat(String... os) {

    StringBuilder buffer = new StringBuilder();

    for (Object o : os) {

      buffer.append(o.toString());
    }

    return buffer.toString();
  }

  /**
   * Method output exception level text.
   * 
   * @param e Exception to be output.
   */
  public void exception(Exception e) {

    if (!isOn(_LOG_LEVEL._LOG_ERROR)) {

      return;
    }

    StringBuilder buffer = new StringBuilder();

    buffer.append(this.prefix());
    buffer.append(_exception);
    buffer.append(e.toString());
    buffer.append(" ");
    buffer.append(getStackTraceAsString(e));
    buffer.append(isSet());

    if (!isSet()) {

      _buffer.add(new AbstractMap.SimpleEntry<>(_LOG_LEVEL._LOG_ERROR.getNumVal(), buffer.toString()));

      return;
    }

    error_output(buffer.toString(), true);
  }

  /**
   * Method output log line prefix.
   * 
   * @return String containing output prefix.
   */
  private String prefix() {

    return new Date().toString();
  }

  /**
   * Method returns a String containing stack trace from Throwable parameter t.
   * 
   * @param t {@link java.lang.Throwable} object containing stack trace
   * @return String containing stack trace of throwable parameter
   */
  private String getStackTraceAsString(Throwable t) {

    StringWriter stringWriter = new StringWriter();

    PrintWriter printWriter = new PrintWriter(stringWriter, true);

    t.printStackTrace(printWriter);

    return stringWriter.getBuffer().toString();
  }

  /**
   * Output level text for error.
   */
  private String _error = " error: ";

  /**
   * Output level text for info.
   */
  private String _info = " info:  ";

  /**
   * Output level text for info.
   */
  private String _debug = " debug: ";

  /**
   * Output level text for exception.
   */
  private String _exception = " exception: ";

  /**
   * Buffer for storage until config is ready.
   */
  private List<Map.Entry<Integer, String>> _buffer = new ArrayList<Map.Entry<Integer, String>>();

  /**
   * Log level mapped to an enum.
   */
  private _LOG_LEVEL _level = _LOG_LEVEL._LOG_NONE;

}
