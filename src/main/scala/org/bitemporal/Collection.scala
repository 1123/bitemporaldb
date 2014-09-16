package org.bitemporal

import java.util.Date

import scala.collection.{mutable => m}

class Collection[T](n : String) {

  def containsTechnical(logicalId: Int, technicalId: Int) : Boolean = {
    this.getTechnical(logicalId, technicalId) != None
  }

  def getTechnical(technicalId: Int, logicalId: Int) : Option[Temporal[T]] = {
    val myList : List[Temporal[T]] = this.get(logicalId).filter(elem => technicalId == elem.technicalId)
    if (myList.size > 0) Some(myList.head) else None
  }

  var maxTechnicalId : Int = 0
  var maxLogicalId : Int = 0
  val name : String = n
  var table = new m.HashMap[Int, List[Temporal[T]]]()

  /**
   * Find the version of the temporal object for the given bitemporal context.
   */

  def findLogical(id : Int, context : BitemporalContext) : Option[Temporal[T]] = {
    if (id < 0) {
      throw new BitemporalStorageException("cannot find without a given logical Id.")
    }
    var candidates = this.instances(id)
    candidates = candidates.filter(_.vPeriod.containsDate(context.validDate))
    candidates = candidates.filter(_.tPeriod.containsDate(context.transactionDate))
    if (candidates.size > 1)
      throw new BitemporalStorageException("Invalid database state. Multiple versions for the object for a given " +
        "transaction and valid time.")
    if (candidates.size == 0)
      return None
    Some(candidates.head)
  }

  /**
   * find all the instances for a given logical id. This may include instances that are no longer valid. Also this
   * may include contradictory data (since all versions are returned).
   *
   * @param logicalId the logical id for which to find the instances.
   * @return all instances that have been found.
   */

  def get(logicalId: Int) : List[Temporal[T]] = {
    table.get(logicalId).get
  }

  /**
   * returns the number of logical objects within this collection.
   */
  def countLogical() : Int = {
    this.table.size
  }

  /**
   * returns the number of technical objects within this collection.
   */
  def countTechnical() : Int = {
    val sizes : List[Int] = this.table.values.toList.map(_.size)
    sizes.foldLeft(0)((a,b) => a + b)
  }

  /**
   * @return the number of active objects within this collection
   */
  def countTemporal() : Int = {
    this.table.values.toList.flatten.count(_.active)
  }

  /**
   * returns the number of instances of an object this collection.
   */
  def countInstances(id : Int) : Int = {
    instances(id).size
  }

  /**
   * Returns the number of __active__ technical instances for a given object within this collection
   * This coincides with the number temporal versions of the object.
   */
  def countTemporalInstances(id: Int) : Int = {
    instances(id).count(_.active)
  }

  /**
   * This method finds _all_ the instances of a logical object. The instances do not need to be active, neither
   * do the need to be valid at some certain point in time.
   *
   * @param id the logical id for which to find the instances.
   * @return a list of instances that belong to the same class as t
   */

  def instances(id: Int) : List[Temporal[T]] = {
    if (this.table.get(id) == None) return List[Temporal[T]]()
    this.table.get(id).get
  }

  /**
   * For a given object, this method finds all active temporal version of this object, and prunes them
   * such that they no longer overlap with the new temporal instance to be stored.
   *
   * An example could be an insurance contract that is considered to be valid from 2014 until 2020
   *
   * Yet a new version of this contract has been authored in 2015 and is supposed to be valid from 2016 to 2030.
   *
   * Then this method will prune the validity of the old contract to only last until the end of 2015,
   * and store the new contract with its given validity.
   */

  def updateLogical(logicalId: Int, t : T, validity: Period, when: Date) {
    if (! this.table.contains(logicalId))
      throw new BitemporalStorageException("No previous version for this object can be found.")
    val candidates : List[Temporal[T]] = this.instances(logicalId).filter(_.active)
    val unaffected : List[Temporal[T]] = candidates.filter(_.vPeriod.disjunct(validity))
    val affected : List[Temporal[T]] = candidates.filter(_.vPeriod.overlaps(validity))
    val temporal = new Temporal(t, validity)
    val updated : List[List[Temporal[T]]] = affected.map(_.update(temporal, when))
    val updated_flat : List[Temporal[T]] = updated.flatten
    for (aff <- affected) {
      aff.tPeriod.to = when
    }
    temporal.technicalId = maxTechnicalId
    temporal.tPeriod.from = when
    maxTechnicalId += 1
    this.table.update(
      logicalId,
      List(temporal) ++ updated_flat ++ unaffected ++ affected
    )
  }



  /**
   * Insert a new temporal version of the object into the database, possibly pruning other versions. The current date is used as the transaction date.
   * @param t the new version to be inserted.
   */

  def updateLogical(logicalId : Int, t : T, validity: Period) {
    this.updateLogical(logicalId, t, validity, new Date())
  }

  /**
   * Insert the first temporal version of an object. No validity is given as parameter, hence maximum validitiy is assumed.
   *
   * @param t the item to store
   * @param when the transaction Date.
   */
  def store(t : T, when: Date) : Int = {
    this.store(t, when, new Period(new Date(Long.MinValue), new Date(Long.MaxValue)))
  }

  /**
   * Insert the first temporal version of an object.
   *
   * @param t the object to store
   * @param when the transaction time
   * @param validity the validity
   */

  def store(t: T, when: Date, validity: Period) : Int = {
    val toStore = new Temporal(t, validity)
    toStore.logicalId = maxLogicalId
    maxLogicalId += 1
    toStore.technicalId = maxTechnicalId
    maxTechnicalId += 1
    toStore.tPeriod.from = when
    this.table.update(toStore.logicalId, toStore :: List[Temporal[T]]())
    toStore.logicalId
  }

  /**
   * Drop all temporal and technical versions of an object.
   *
   * @param id the logical id of the object to be dropped.
   */

  def dropLogical(id: Int) {
    this.table.remove(id)
  }

}
