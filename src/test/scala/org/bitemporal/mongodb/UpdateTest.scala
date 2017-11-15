package org.bitemporal.mongodb

import org.bitemporal.Period
import org.bitemporal.domain.Building
import org.bitemporal.mongogson.BitemporalMongoDb
import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}

class UpdateTest extends FlatSpec with Matchers {

  private var buildingId : String = _
  private val year2014 = new Period(new DateTime(2014, 1, 1, 0, 0, 0).toDate, new DateTime(2015,1,1,0,0,0).toDate)
  private val year2015 = new Period(new DateTime(2015,1,1,0,0,0).toDate, new DateTime(2016,1,1,0,0,0).toDate)
  private val feb2014 = new Period(new DateTime(2014,2,1,0,0,0).toDate, new DateTime(2014,3,1,0,0,0).toDate)
  private val years2013To2017 = new Period(new DateTime(2013,1,1,0,0,0).toDate, new DateTime(2017,1,1,0,0,0).toDate)
  private val myDb = new BitemporalMongoDb()
  
  behavior of "a BitemporalMongoDB "

  it should "be able to update logical objects with new temporal non-overlapping versions" in {
    myDb.clearCollection(new Building())
    buildingId = myDb.store(new Building("Downing Street 10"), year2014)
    myDb.update(new Building("White House"), buildingId, year2015)
    myDb.findTemporals(new Building, buildingId).size should be (2)
    myDb.findActive(new Building, buildingId).size should be (2)
  }
  
  it should "be able to update logical objects with new temporal overlapping versions" in {
    myDb.update(new Building("Willy-Brandt-Straße 1"), buildingId, feb2014)
    // one inactive version for year2014
    // one active version for january2014
    // one active version for february2014
    // one active version for the rest of 2014
    // one active version for 2015
    myDb.findTemporals(new Building, buildingId).size should be (5)
    myDb.findActive(new Building, buildingId).size should be (4)
    myDb.update(new Building("Beijing"), buildingId, years2013To2017)
    myDb.findTemporals(new Building, buildingId).size should be (6)
    myDb.findAll(new Building, buildingId).size should be (6)
    myDb.findActive(new Building, buildingId).size should be (1)
  }
  
}