package org.bitemporal

import java.util.Date

/**
 *  * Use cases:
 * 1) store a single object without validity ==> use store()
 * 2) store two distinct objects without validity ==> use store()
 * 3) store two temporal versions of the same object with non-overlapping validity
 *    ==> use update()
 * 4) reduce/extend the validity of one temporal version of an object --> We need to introduce temporal ids
 *    ==> use update_temporal
 * 5) drop one temporal version of an object
 *    ==> use drop_temporal
 * 6) change the domain-properties of an object
 *    --> all temporal versions of this object should be affected
 * 7) change the domain-properties of a given temporal version of an object --> need for temporal ids
 *    --> only this specific version will be affected
 * 8) update-overwrite: save an object with given validity. This will adjust temporal boundaries of overlapping
 *    instances of the same object.
 */

/**
 * @tparam I : the type of the identifiers used in the bitemporal database. 
 * This may be Strings for the mongodb implementation or Integers for the in memory implementation.
 */

trait BitemporalDatabase[I] {
  /**
   * Return the number of distinct classes that are managed by this database.
   */
  def tableCount(): Int
  
  /**
   * Returns the number of instances for a given logical identifier. 
   * 
   * @param logicalId the logical identifier
   * @param t a dummy instance of T such that the DB knows in which collection to look.
   */
  def countInstances[T](logicalId: I, t: T) : Int
  
  /**
   * The number of active objects, this may be multiple objects for one logical id.
   * 
   * @param t a dummy instance of the class for which to determine the number of active objects.
   * 
   */
  def activeObjects[T](t: T) : Int
  def countTechnical[T](t: T) : Int
  def countLogical[T](t: T) : Int
  def updateLogical[T](logicalId: I, instance : T, validity: Period)
  def updateLogical[T](logicalId: I, instance : T, validity: Period, date: Date)
  // drop an object together with all its temporal and technical versions
  def dropLogical[T](logicalId: I, t : T)
  def countCollections() : Int
  def findLogical[T](t: T, id: I) : Option[Temporal[T, I]] =
    findLogical(t, id, new BitemporalContext(new Date(), new Date()))
  def findLogical[T](t: T, id: I, context: BitemporalContext) : Option[Temporal[T, I]]
  def store[T](t : T) : I
  def store[T](t : T, when: Date) : I
  def store[T](t : T, validity: Period) : I
  def store[T](t : T, when : Date, validity : Period) : I
  def clearDatabase()
}
