package org.bitemporal.mongodb

import com.mongodb.MongoClient
import java.util.Date
import com.fasterxml.jackson.annotation.JsonProperty
import org.scalatest.Matchers
import org.scalatest.FlatSpec
import org.joda.time.DateTime
import com.mongodb.DBObject
import com.mongodb.util.JSON
import com.mongodb.util.JSON
import com.mongodb.BasicDBObject
import org.bson.types.ObjectId
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import java.lang.reflect.Type

class Outer {

  var _id : ObjectId = null
  
  val name = "Some Body"
  
  val inner = new Inner()
    
}

class Inner {
  
  val address = "Some Where"
  
}

class PlainJsonTest extends FlatSpec with Matchers {

  behavior of "MongoDB"

  val client = new MongoClient("127.0.0.1", 27017)
  val db = client.getDB("nested_test")
  
  val collection = db.getCollection("outer")
  var id : ObjectId = null
  var id2 : ObjectId = null
  
  it should "be able to store Json" in {
    val dbObject : DBObject = JSON.parse("{'name':'mkyong', 'age':30}").asInstanceOf[DBObject]
    val writeResult = collection.save(dbObject);
    id = dbObject.get("_id").asInstanceOf[ObjectId]
  }
  
  it should "be able to retrieve by Id" in {
    print(collection.findOne(new BasicDBObject("_id", id)).toString())
  }
  
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

