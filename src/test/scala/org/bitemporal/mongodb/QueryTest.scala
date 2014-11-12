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
import org.bitemporal.BitemporalContext
import java.util.Date

class QueryTest extends FlatSpec with Matchers {

  val year2014 = new Period(new DateTime(2014,1,1,0,0,0).toDate(), new DateTime(2015,1,1,0,0,0).toDate())
  val year2015 = new Period(new DateTime(2015,1,1,0,0,0).toDate(), new DateTime(2016,1,1,0,0,0).toDate())
  val xmas2014 = new DateTime(2014,12,25,0,0,0).toDate()
  val newYear2015 = new DateTime(2015,1,1,0,0,0).toDate()
  val newYear2017 = new DateTime(2017,1,1,0,0,0).toDate()
  val myDb = new BitemporalMongoDb()
  
  behavior of "a BitemporalMongoDB "

  it should "be able to answer queries with a bitemporal context" in {
     myDb.clearCollection(new Building())
	 val buildingId = myDb.store(new Building("22nd Street"), year2014, buildingType())
	 myDb.update(new Building("23rd Street"), buildingId, year2015, buildingType())
	 val found : Option[Building] = myDb.find(classOf[Building], buildingId, new BitemporalContext(new Date(), xmas2014), buildingType())
	 found should not be (None)
	 found.get.address should be ("22nd Street")
	 val found2 = myDb.find(classOf[Building], buildingId, new BitemporalContext(new Date(), newYear2015), buildingType())
	 found2.get.address should be ("23rd Street")
     val found3 = myDb.find(classOf[Building], buildingId, new BitemporalContext(new Date(), newYear2017), buildingType())
	 found3 should be (None)
  }
  
  def buildingType() = {
    new TypeToken[SimpleTemporal[Building]]() {}.getType()
  }
  
}