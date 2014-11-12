package org.bitemporal.mongogson

import org.bson.types.ObjectId
import com.mongodb.DBObject
import com.google.gson.Gson
import com.mongodb.util.JSON
import java.lang.reflect.Type
import com.mongodb.MongoClient
import org.bitemporal.Period
import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import org.joda.time.DateTime
import java.util.Date
import scala.language.higherKinds
import java.util.ArrayList
import java.util.UUID
import org.bitemporal.BitemporalContext

object MongoConf {

  val minimumDate = new DateTime(0,1,1,0,0,0).toDate()
  val maximumDate = new DateTime(100000,1,1,0,0,0).toDate()
  
  val host = "127.0.0.1"
  val port = 27017
  val db = "nested_test"
    
  val logicalIdField = "logicalId"  
  
}


class BitemporalMongoDb {

  val client = new MongoClient(MongoConf.host, MongoConf.port)
  val db = client.getDB(MongoConf.db)

  def getCollection[T](t : T) : DBCollection = {
    db.getCollection(t.getClass().toString().replaceAll("\\.", "_").replace("class ", ""))
  }
  
  def clearCollection[T](t : T) = {
    getCollection(t).remove(new BasicDBObject())
  }
  
  /**
   * Store the first temporal version of an object. 
   * For the first stored version, the logicalId is set to the _id given by MongoDB.
   * For all subsequent versions of this logical object, 
   * the logicalId will be set to the one of this first version.
   */
  
  def store[T](t: T, vPeriod : Period, myTemporalType : Type ) : String = {
	val uuid = UUID.randomUUID().toString()
    val myTemporal = new SimpleTemporal[T](t, vPeriod)
    val json = new Gson().toJson(myTemporal, myTemporalType)
    val dbObject : DBObject = JSON.parse(json).asInstanceOf[DBObject]
	dbObject.put(MongoConf.logicalIdField, uuid)
    getCollection(t).save(dbObject)
    uuid
  }
  
  /**
   * Finds one temporal instance without validity information (which may be active or not) for the given logical Id.
   */
  def findOne[T](template : T, id : String, myTemporalType : Type) : T = {
	val json = getCollection(template).findOne(new BasicDBObject(MongoConf.logicalIdField, id)).toString()
    val parsed : SimpleTemporal[T] = new Gson().fromJson[SimpleTemporal[T]](json, myTemporalType)
    parsed.value
  }
  
  /**
   * Finds all instances with validity information (which may be active or not) for the given logical Id.
   * This is useful for doing temporal updates.
   */
  def findTemporals[T](template : T, id : String, myTemporalType : Type) : List[SimpleTemporal[T]] = { 
    var result : List[SimpleTemporal[T]] = List[SimpleTemporal[T]]()
    val cursor = getCollection(template).find(new BasicDBObject("logicalId", id))
    while (cursor.hasNext()) {
      val parsed : SimpleTemporal[T] = new Gson().fromJson[SimpleTemporal[T]](cursor.next().toString(), myTemporalType)
      result = result ++ List(parsed)
    }
    result
  }
  
  def findAll[T](template : T, id : String, myTemporalType : Type) : List[T] = {
    return this.findTemporals(template, id, myTemporalType).map(t => t.value)
  }
  
  def findActive[T](template: T, id:String, myTemporalType : Type) : List[SimpleTemporal[T]] = {
    this.findTemporals(template, id, myTemporalType).filter(_.active)
  }
  
  /**
   * When a temporal update occurs, the easiest way is to delete all existing versions of the object, and 
   * insert the updated/new ones. This method can be used to delete all objects for a given logical id.
   */
  def deleteAll[T](template : T, id: String) {
    getCollection(template).remove(new BasicDBObject(MongoConf.logicalIdField, id));
  }
  
  /**
   * When a temporal update occurs, all inactive versions remain unaffected.
   * Also all non-overlapping versions remain unaffected.
   * Only versions that are active and overlapping are affected.
   * Affected versions must be updated.
   * Updating means setting the transaction-time of these versions to the current date, and creating fresh copies.
   * Finally, the inactive versions, the unaffected versions, 
   * the updated versions, the affected versions and the new version are stored.
   * 
   * TODO: deleting all documents with this logicalId, then updating only some of them and finally storing all back 
   * may be too inefficient. 
   * We could only query mongodb for those documents that are active and for those that overlap the given period.
   */
  
  def update[T](t: T, logicalId: String, vPeriod : Period, myTemporalType : Type) {
    val candidates = findTemporals(t, logicalId, myTemporalType)
    val active = candidates.filter(_.active)
    val inactive = candidates.filter(_.inactive)
    val affected : List[SimpleTemporal[T]] = active.filter(_.vPeriod.overlaps(vPeriod))
    val unaffected : List[SimpleTemporal[T]] = active.filter(_.vPeriod.disjunct(vPeriod))
    val updated : List[List[SimpleTemporal[T]]]= affected.map(e => e.update(vPeriod))
    val updated_flat = updated.flatten
    for (u <- updated_flat) {
      u.logicalId = logicalId
    }
    for (a <- affected) {
      a.tPeriod.to = new Date()
    }
    val newVersion = new SimpleTemporal(t, vPeriod)
    newVersion.logicalId = logicalId
    val toBeStored = inactive ++ unaffected ++ affected ++ updated_flat ++ List(newVersion)
    this.deleteAll(t, logicalId)
    this.storeAll(toBeStored, myTemporalType)
  }

  def storeAll[T](toBeStored : List[SimpleTemporal[T]], myTemporalType: Type) {
	for (temporal <- toBeStored) {
	    val json = new Gson().toJson(temporal, myTemporalType)
		val dbObject : DBObject = JSON.parse(json).asInstanceOf[DBObject]
		getCollection(temporal.value).save(dbObject)
	}
  }
  
  /**
   * Finding objects by logical Id and bitemporal context. 
   * If more than one is found this would be an invalid database state ==> an exception is thrown.
   * The database may not contain an instance for the bitemporal context. Therefore the return type is optional.
   * 
   * @param template: a (possibly empty) instance to find the correct database collection.
   * @param logicalId: the logical id
   * @param context: the bitemporal context to search for.
   * @param myTemporalType: technical value needed by Gson for correct serialization.
   */
  
  def find[T](clazz: Class[T], logicalId: String, context: BitemporalContext, myTemporalType : Type) : Option[T] = {
    val candidates = findTemporals(clazz.newInstance(), logicalId, myTemporalType)
    val selected = candidates.filter(_.tPeriod.containsDate(context.transactionDate)).filter(_.vPeriod.containsDate(context.validDate))
    if (selected.size > 1) throw new RuntimeException("Invalid database state")
    if (selected.size == 0) return None
    Some(selected(0).value)
  }
  
}
