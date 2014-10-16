package org.bitemporal.mongodb

import org.scalatest.Matchers
import org.scalatest.FlatSpec
import java.util.Date
import org.bitemporal.Period

class MongoDbTest extends FlatSpec with Matchers {

  behavior of "a bitemporal mongodb"
  
  it should "be able to store objects" in {
    val db = new MongoBitemporalDatabase()
    val h = new Human(5);
    db.store(h, new Date, new Period)
  }
  
  it should " be able to store generic objects " in {
    val db = new MongoBitemporalDatabase()
    val s = new Student("Some", "Body")
    db.store(s, new Date(), new Period)
  }
}