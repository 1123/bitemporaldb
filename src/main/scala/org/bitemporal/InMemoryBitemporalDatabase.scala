package org.bitemporal

import java.util.Date

import scala.collection.mutable.HashMap
import scala.collection.mutable

/**
 * This is an in-memory implementation of the bitemporal database interface.
 * Other implementations may be ones backed by mongodb, hadoop or even by ordinary relational databases.
 */

object InMemoryBitemporalDatabase extends BitemporalDatabase[Int] {

  private var tables : mutable.HashMap[String, Collection[Object]] = new mutable.HashMap[String, Collection[Object]]()

  /**
   * {@inheritDoc}
   */
  override def tableCount() : Int = {
    this.tables.size
  }

  /**
   * {@inheritDoc}
   */
  override def countInstances[T](logicalId: Int, t: T) : Int = collectionFor(t).get.countInstances(logicalId)
  override def activeObjects[T](t: T) : Int = collectionFor(t).get.activeObjects()
  override def countTechnical[T](t: T) : Int = collectionFor(t).get.countTechnical()
  override def countLogical[T](t: T) : Int = collectionFor(t).get.countLogical()
  override def updateLogical[T](logicalId : Int, temporal: T, validity: Period) {
    this.updateLogical(logicalId, temporal, validity, new Date())
  }

  override def updateLogical[T](logicalId : Int, temporal: T, validity : Period , date: Date) {
    collectionFor(temporal).get.updateLogical(logicalId, temporal, validity, date)
  }

  /**
   * Drop all stored instances for a given logical ID.
   *
   * TODO: it would be cleaner to only pass in the logical ID plus the type.
   *
   * @param t the instance holding the logical ID
   * @tparam T the type of the instance
   */

  override def dropLogical[T](logicalId: Int, t: T) {
    collectionFor(t).get.dropLogical(logicalId)
  }

  override def countCollections() : Int = this.tables.keys.size

  /**
   * Find the logical object for a given bitemporal context.
   *
   * TODO: it would be sufficient and cleaner to only pass in the logical id plus the data type, instead of an entire object.
   *
   * @param context the bitemporal context composed of transaction time and valid time.
   * @tparam T the data type
   * @return
   */
  override def findLogical[T](t: T, logicalId: Int, context: BitemporalContext) : Option[Temporal[T, Int]] =
    collectionFor(t).get.findLogical(logicalId, context)

  /**
   * Given an instance of a temporal data type, retrieve the collection for this instance.
   * @return the collection holding all currently stored instances of T.
   */

  def collectionFor[T](t: T): Option[Collection[T]] =
    collection(t.getClass.toString).asInstanceOf[Option[Collection[T]]]

  /**
   * Get the collection for a given name of a class.
   * @param name the name of the class for which the collection is retrieved
   * @return the collection
   */

  def collection(name: String) : Option[Collection[Object]] = this.tables.get(name)

  /**
   * Insert a new logical object into the database. The transaction time is the current time.
   * @param t the object to store
   * @tparam T the type of the object
   * @return the logical id of the newly stored object
   */

  override def store[T](t : T) : Int = { this.store(t, new Date()) }

  /**
   * Insert a new logical object into the database. This should only be used for testing purposes.
   * In real life, the method without the parameter for the transaction time should be used.
   * @param t the temporal object to store
   * @param when the transaction time.
   * @tparam T the type of the object to store.
   * @return the logical id of the newly created object.
   */

  override def store[T](t : T, when: Date) : Int = {
    if (collectionFor(t).isEmpty)
      initCollectionFor(t)
    collectionFor(t).get.store(t, when)
  }

  /**
   * Insert a new logical object into the database with a given validity.
   * @param t the object to be stored
   * @param validity the validity (valid time)
   * @tparam T the type of the object.
   */

  override def store[T](t : T, validity : Period) : Int = {
    this.store(t, new Date(), validity)
  }

  /**
   * Insert a new logical object with a given validity and an artificial transaction time. Use this method for testing purposes.
   * @param t the object to be stored
   * @param when the transaction time
   * @param validity the valid period
   * @tparam T the type of the object to be stored
   */

  override def store[T](t: T, when: Date, validity : Period) : Int = {
    if (collectionFor(t).isEmpty)
      initCollectionFor(t)
    collectionFor(t).get.store(t, when, validity)
  }

  private def initCollectionFor[T](t: T) {
    this.tables.put(t.getClass.toString, new Collection[Object](t.getClass.toString))
  }

  override def clearDatabase() { tables = new mutable.HashMap[String, Collection[Object]]() }

}
