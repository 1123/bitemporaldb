package org.bitemporal.mongodb

import org.bitemporal.Period
import org.bitemporal.domain.{Airplane, Building}
import org.bitemporal.mongogson.BitemporalMongoDb
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class StorageTest extends FlatSpec with Matchers with BeforeAndAfter {

  private var buildingId : String = _
  private var airplaneId : String = _
  private val from = new DateTime(2014, 1, 1, 0, 0, 0).toDate
  private val to = new DateTime(2015, 1, 1, 0, 0, 0).toDate
  private val vPeriod1 = new Period(from, to)

  val myDb = new BitemporalMongoDb()
  
  behavior of "a BitemporalMongoDB "

  it should "be able to store temporal objects of different classes" in {
    myDb.clearCollection(new Building())
    myDb.clearCollection(new Airplane())
    buildingId = myDb.store(new Building("Downing Street 10"), vPeriod1)
	airplaneId = myDb.store(new Airplane("A380"), vPeriod1)
  }
  
  it should "be able to retrieve objects given a logical id using the default temporal context" in {
    val building : Building = myDb.findOne(new Building(), buildingId)
    building.address should be ("Downing Street 10")
  }
  
  
  it should "be able to retrieve objects with collections" in {
    val airplane : Airplane = myDb.findOne(new Airplane(), airplaneId)
    airplane.typ should be ("A380")
    airplane.passengers.size() should be (1)
    airplane.passengers.get(0) should be ("me")
  }
  
}