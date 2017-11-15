package org.bitemporal.mongodb

import java.util.Date

import org.bitemporal.{BitemporalContext, Period}
import org.bitemporal.domain.Building
import org.bitemporal.mongogson.{BitemporalMongoDb, MongoConf}
import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}

class QueryTest extends FlatSpec with Matchers {

  private val year2014 = new Period(new DateTime(2014, 1, 1, 0, 0, 0).toDate, new DateTime(2015, 1, 1, 0, 0, 0).toDate)
  private val year2015 = new Period(new DateTime(2015, 1, 1, 0, 0, 0).toDate, new DateTime(2016, 1, 1, 0, 0, 0).toDate)
  private val xmas2014 = new DateTime(2014, 12, 25, 0, 0, 0).toDate
  private val newYear2015 = new DateTime(2015, 1, 1, 0, 0, 0).toDate
  private val newYear2017 = new DateTime(2017, 1, 1, 0, 0, 0).toDate
  
  MongoConf.db = "bla"

  val myDb = new BitemporalMongoDb()

  behavior of "a BitemporalMongoDB "

  it should "be able to answer queries with a bitemporal context" in {
     myDb.clearCollection(new Building())
	 val buildingId = myDb.store(new Building("22nd Street"), year2014)
	 myDb.update(new Building("23rd Street"), buildingId, year2015)
	 val found : Option[Building] = myDb.find(classOf[Building], buildingId, new BitemporalContext(new Date(), xmas2014))
	 found should not be None
	 found.get.address should be ("22nd Street")
	 val found2 = myDb.find(classOf[Building], buildingId, new BitemporalContext(new Date(), newYear2015))
	 found2.get.address should be ("23rd Street")
     val found3 = myDb.find(classOf[Building], buildingId, new BitemporalContext(new Date(), newYear2017))
	 found3 should be (None)
  }
  
}