package org.bitemporal

/**
 * Created with IntelliJ IDEA.
 * User: linse
 * Date: 3/5/14
 * Time: 12:47
 * To change this template use File | Settings | File Templates.
 */
class BitemporalStorageException(message: String = "Error storing, updating or deleting the object.", cause: Throwable = null)
  extends Exception(message, cause)
