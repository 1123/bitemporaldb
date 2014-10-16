package org.bitemporal.mongodb

import org.bitemporal.BitemporalDatabase
import com.mongodb.MongoClient
import org.bitemporal.Temporal
import org.bitemporal.Period
import org.mongojack.JacksonDBCollection
import java.util.Date
import org.bson.types.ObjectId
import org.mongojack.WriteResult
import com.fasterxml.jackson.annotation.JsonProperty
import org.bitemporal.BitemporalDatabase
import org.bitemporal.BitemporalContext

class MongoBitemporalDatabase(
  host: String = "localhost",
  port: Int = 27017,
  dbname: String = "test") extends BitemporalDatabase[ObjectId] {

  val client = new MongoClient(host, port)
  val db = client.getDB(dbname)

  private def getCollection[T](t: T): JacksonDBCollection[Temporal[T, ObjectId], ObjectId] = {
    val collectionName = t.getClass.toString.replaceAll(".", "_");
    val collection = db.getCollection(collectionName)
    JacksonDBCollection.wrap(collection, classOf[Temporal[T, ObjectId]], classOf[ObjectId]);
  }

  override def store[T](t: T, when: Date, validity: Period): ObjectId = {
    val coll = this.getCollection(t);
    val result: WriteResult[Temporal[T, ObjectId], ObjectId] = coll.insert(new Temporal[T, ObjectId](t, validity))
    result.getSavedId()
  }

  override def store[T](t: T) = { this.store(t, new Date(), new Period) }
  override def store[T](t: T, when: Date) = { this.store(t, when, new Period) }
  override def store[T](t: T, validity: Period) = { this.store(t, new Date(), validity) }

  override def tableCount(): Int = { 1 }
  override def countInstances[T](logicalId: ObjectId, t: T): Int = { 1 }
  override def countTemporal[T](t: T): Int = { 1 }
  override def countTechnical[T](t: T): Int = { 1 }
  override def countLogical[T](t: T): Int = { 1 }
  override def updateLogical[T](logicalId: ObjectId, instance: T, validity: Period) = {}
  override def updateLogical[T](logicalId: ObjectId, instance: T, validity: Period, date: Date) = {}
  // drop an object together with all its temporal and technical versions
  def dropLogical[T](logicalId: ObjectId, t: T) = {}
  override def countCollections(): Int = { 1 }
  override def findLogical[T](t: T, id: ObjectId): Option[Temporal[T, ObjectId]] =
    findLogical(t, id, new BitemporalContext(new Date(), new Date()))

  override def findLogical[T](t: T, id: ObjectId, context: BitemporalContext): Option[Temporal[T, ObjectId]] = {
    val coll = this.getCollection(t);
    coll.find().lessThan("validity.from", context.validDate).greaterThan("validity.to", context.validDate)
    None
  }

  override def clearDatabase() = {}

}