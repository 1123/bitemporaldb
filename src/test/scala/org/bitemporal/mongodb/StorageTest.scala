package org.bitemporal.mongodb

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import org.bitemporal.Period
import org.joda.time.DateTime
import com.mongodb.MongoClient
import com.mongodb.util.JSON
import com.mongodb.DBObject
import org.bson.types.ObjectId
import com.mongodb.BasicDBObject
import java.lang.reflect.ParameterizedType
import org.bitemporal.mongogson.SimpleTemporal
import java.util.ArrayList
import org.bitemporal.mongogson.BitemporalMongoDb
import org.scalatest.BeforeAndAfter
import org.bitemporal.domain.Airplane
import org.bitemporal.domain.Building

class StorageTest extends FlatSpec with Matchers with BeforeAndAfter {

  var buildingId : String = null
  var airplaneId : String = null
  val from = new DateTime(2014,1,1,0,0,0).toDate()
  val to = new DateTime(2015,1,1,0,0,0).toDate()
  val vPeriod1 = new Period(from, to)
  val vPeriod2 = new Period(new DateTime(2015,1,1,0,0,0).toDate(), new DateTime(2015,1,1,0,0,0).toDate())

  val myDb = new BitemporalMongoDb()
  
  behavior of "a BitemporalMongoDB "

  it should "be able to store temporal objects of different classes" in {
    myDb.clearCollection(new Building())
    myDb.clearCollection(new Airplane())
    buildingId = myDb.store(new Building("Downing Street 10"), vPeriod1, buildingType())
	airplaneId = myDb.store(new Airplane("A380"), vPeriod1, airplaneType())
  }
  
  def airplaneType() = {
    new TypeToken[SimpleTemporal[Airplane]]() {}.getType()
  }
  
  def buildingType() = {
    new TypeToken[SimpleTemporal[Building]]() {}.getType()
  }
  
  it should "be able to retrieve objects given a logical id using the default temporal context" in {
    val building : Building = myDb.findOne(new Building(), buildingId, buildingType())
    building.address should be ("Downing Street 10")
  }
  
  
  it should "be able to retrieve objects with collections" in {
    val airplane : Airplane = myDb.findOne(new Airplane(), airplaneId, airplaneType())
    airplane.typ should be ("A380")
    airplane.passengers.size() should be (1)
    airplane.passengers.get(0) should be ("me")
  }
  
}