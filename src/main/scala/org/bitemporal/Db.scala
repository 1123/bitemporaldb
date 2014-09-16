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
trait Db {

  def tableCount(): Int
  def countInstances[T](logicalId: Int, t: T) : Int
  def countTemporal[T](t: T) : Int
  def countTechnical[T](t: T) : Int
  def countLogical[T](t: T) : Int
  def updateLogical[T](logicalId: Int, instance : T, validity: Period)
  def updateLogical[T](logicalId: Int, instance : T, validity: Period, date: Date)
  // drop a given version of an object in valid time
  def dropTemporal[T](t: Temporal[T])
  // drop an object together with all its temporal and technical versions
  def dropLogical[T](logicalId: Int, t : T)
  def countCollections() : Int
  def findLogical[T](t: T, id: Int) : Option[Temporal[T]] =
    findLogical(t, id, new BitemporalContext(new Date(), new Date()))
  def findLogical[T](t: T, id: Int, context: BitemporalContext) : Option[Temporal[T]]
  def collectionFor[T](t : T) : Option[Collection[T]]
  def collection(name: String) : Option[Collection[Object]]
  def store[T](t : T) : Int
  def store[T](t : T, when: Date) : Int
  def store[T](t : T, validity: Period) : Int
  def store[T](t : T, when : Date, validity : Period) : Int
  def clearDatabase()
}