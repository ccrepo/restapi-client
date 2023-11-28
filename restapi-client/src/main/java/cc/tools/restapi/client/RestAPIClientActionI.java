package cc.tools.restapi.client;


/**
 * This interface is for common action functions.
 * 
 * @author cc
 * @version %I%, %G%
 * @since 0.1
 */
public interface RestAPIClientActionI {
  
  /**
   * Method indicates whether object is valid.
   * 
   * @return boolean true if valid, false otherwise.
   */
  public boolean isValid();

  /**
   * Method implements the clint semd.
   * @param i message number.
   * @return boolean true is success, false otherwise.
   */
  public boolean send(long i);
  

  /**
   * Method to obtain action type.
   * @return String containing action type name.
   */
  public String action();
}
