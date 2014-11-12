package org.bitemporal.mongodb.gson

import org.bson.types.ObjectId
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.MongoClient
import com.mongodb.util.JSON

class Outer {

  var _id : ObjectId = null
  val name = "Some Body"
  val inner = new Inner()
}

class Inner {
  val address = "Some Where"
}

/**
 * This test shows that MongoDb can store objects which have been serialized to Json using Gson.
 * It also shows that these objects can be retrieved from mongodb using their stored id and 
 * deserialized to their original objects. 
 */

class PlainJsonTest extends FlatSpec with Matchers {

  behavior of "MongoDB"

  val client = new MongoClient("127.0.0.1", 27017)
  val db = client.getDB("nested_test")
  
  val collection = db.getCollection("outer")
  var id2 : ObjectId = null
  
  it should "be able to store objects serialized by Gson" in {
    val outer = new Outer()
    val json = new Gson().toJson(outer)
    val dbObject : DBObject = JSON.parse(json).asInstanceOf[DBObject]
    val writeResult = collection.save(dbObject);
    id2 = dbObject.get("_id").asInstanceOf[ObjectId]
  }
  
  it should "be able to retrieve objects and deserialize them with Gson" in {
    val json : String = collection.findOne(new BasicDBObject("_id", id2)).toString()
    val gson = new GsonBuilder().create()
    val outer = gson.fromJson(json, classOf[Outer])
    outer.name should be ("Some Body")
    outer.inner.address  should be ("Some Where")
  }
  
}

