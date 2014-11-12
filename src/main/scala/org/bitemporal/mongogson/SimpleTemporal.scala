package org.bitemporal.mongogson

import org.bitemporal.Period
import org.joda.time.DateTime
import org.bson.types.ObjectId
import java.util.Date

class SimpleTemporal[T](t : T, vP : Period) {
  val value = t
  val vPeriod = vP
  // minimum and maximum Date for lower technical and upper technical validity
  val tPeriod = new Period(MongoConf.minimumDate, MongoConf.maximumDate)
  
  var logicalId : String = null
  
  var _id : ObjectId = null
  
  def active : Boolean = {
    tPeriod.to == MongoConf.maximumDate
  }
  
  def inactive : Boolean = {
    tPeriod.to != MongoConf.maximumDate
  }
  
  def update(p: Period): List[SimpleTemporal[T]] = {
    this.update(p, new Date())
  }
  
  def update(p : Period, when: Date): List[SimpleTemporal[T]] = {
    if (!this.vPeriod.overlaps(p)) {
      throw new RuntimeException("You cannot update this object with a non-overlapping new object.")
    }
    if (this.vPeriod.within(p)) {
      return List()
    }
    if (this.vPeriod.contains(p)) {
      val first: SimpleTemporal[T] = new SimpleTemporal[T](this.value, new Period(this.vPeriod.from, p.from))
      first.tPeriod.from = when
      val third: SimpleTemporal[T] = new SimpleTemporal[T](this.value, new Period(p.to, this.vPeriod.to))
      third.tPeriod.from = when
      return List(first, third)
    }
    if (this.vPeriod.overlapsStart(p)) {
      val first: SimpleTemporal[T] = new SimpleTemporal[T](this.value, new Period(this.vPeriod.from, p.from))
      first.tPeriod.from = when
      return List(first)
    }
    if (this.vPeriod.overlapsEnd(p)) {
      val second: SimpleTemporal[T] = new SimpleTemporal[T](this.value, new Period(p.to, this.vPeriod.to))
      second.tPeriod.from = when
      return List(second)
    }
    throw new RuntimeException("Program error. This should never happen.")
  }
}