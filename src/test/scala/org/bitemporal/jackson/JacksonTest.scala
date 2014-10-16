package org.bitemporal.jackson

import org.scalatest._
import org.mongojack.JacksonDBCollection
import com.mongodb.MongoClient
import org.bitemporal.mongodb.Student


class JacksonTest extends FlatSpec with Matchers {

  behavior of "a jackson "

  it should "be able to store objects in mongodb " in {
    val client = new MongoClient()
    val db = client.getDB("expedia")
    val collection = db.getCollection("students")
    val coll = JacksonDBCollection.wrap(collection, classOf[Student],
        classOf[String]);
    val result = coll.insert(new Student("some", "body"))
  }
  
}


