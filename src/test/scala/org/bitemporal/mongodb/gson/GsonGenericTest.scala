package org.bitemporal.mongodb.gson

import java.lang.reflect.Type
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyGeneric[T](t : T) {
  val value : T = t;
}

class Bla(v1: String) {
  val value1 = v1
}

/**
 * This test just shows that Gson can serialize generic objects. Serialization and deserialization of 
 * generic objects is important for our bitemporal MongoDatabase, since our temporals are all generics.
 */

class GsonGenericTest extends FlatSpec with Matchers {

  behavior of "Gson"

  it should "be able to serialize plain objects" in {
    val myObject = new Bla("value1")
    new Gson().toJson(myObject) should be ("{\"value1\":\"value1\"}")
  }
  
  it should "be able to serialize generic objects" in {
    val myGeneric = new MyGeneric[Bla](new Bla("value1"))
    val myGenericType : Type = new TypeToken[MyGeneric[Bla]]() { }.getType();
    val json = new Gson().toJson(myGeneric, myGenericType)
    print(json)
    json should be ("{\"value\":{\"value1\":\"value1\"}}")
  }
  
}

