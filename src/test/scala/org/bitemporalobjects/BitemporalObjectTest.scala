package org.bitemporalobjects

import org.scalatest._
import org.joda.time.DateTime
import org.bitemporal.Period
import org.bitemporal.BitemporalContext
import java.util.Date

/**
 * This is an attempt to use bitemporal objects in stead of a bitemporal database. 
 */

class BitemporalObjectTest extends FlatSpec with Matchers {

  behavior of "a BitemporalObject"

  val date2014 = new DateTime(2014, 1, 1, 0, 0, 0).toDate
  val date2015 = new DateTime(2015, 1, 1, 0, 0, 0).toDate
  val date2016 = new DateTime(2016, 1, 1, 0, 0, 0).toDate

  it should "allow storing bitemporal objects with validity and updating them" in {
    val address = new BitemporalObject[String]("hierstrasse 77", new Period(date2014, date2015))
    address.update("dortstrasse 66", new Period(date2015, date2016))
    address.history.size should be (2)
  }

  it should "allow querying with a valid Date " in {
    val address = new BitemporalObject[String]("hierstrasse 77", new Period(date2014, date2015))
    address.update("dortstrasse 66", new Period(date2015, date2016))
    address.get(validDate = date2014) should be ("hierstrasse 77")
    address.get(validDate = date2015) should be ("dortstrasse 66")
  }
  
  it should "allow querying with a given transaction date" in {
    val address = new BitemporalObject[String]("hierstrasse 77", new Period(date2014, date2015), date2014)
    address.update("dortstrasse 66", new Period(date2014, date2015), date2015)
    address.get(validDate = date2014, transactionDate = date2014) should be ("hierstrasse 77")
    address.get(validDate = date2014, transactionDate = date2015) should be ("dortstrasse 66")
  }
  
  /**
   * Composite objects are a challenge. We would need metaprogramming for generating the bitemporal version
   * of non-bitemporal class with annotated bitemporal properties. We might want to do this in Java using 
   * the java object model. Yet this will always require an additional preprocessing step similar to the 
   * generation of the meta-model for JPA entities.
   * 
   * Alternatively we might not introduce bitemporal versions of objects, but store the bitemporal data 
   * external to the objects. Upon serialization we may want to join the objects with their bitemporal 
   * and then store as a document within a document oriented DB.
   * 
   * Another alternative would be to store the bitemporal versions not with the objects themselves, but within
   * a separate collection.  
   */
  it should "allow the composition of bitemporal objects and querying of composite objects." in {
	  
  }
}

class Product(p : BitemporalObject[BigDecimal], i : Int, c: BitemporalObject[String]) {

  val category = c;
  val price = p
  val id = i;
  
}

