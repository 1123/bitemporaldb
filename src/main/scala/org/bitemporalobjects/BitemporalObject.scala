package org.bitemporalobjects

import java.util.Date
import org.bitemporal.Period

class BitemporalObject[T](t: T, p: Period, creationDate : Date = new Date()) {
  var history = List(new Temporal[T](t, p, creationDate))

  def update(t: T, p: Period, transactionTime : Date = new Date()) : Unit = {
    val activeVersions: List[Temporal[T]] = history.filter(_.active(transactionTime))
    val affectedVersions: List[Temporal[T]] = activeVersions.filter(_.validity.overlaps(p))
    val updatedVersions: List[Temporal[T]] = affectedVersions.map(_.setInvalidFor(p)).flatten
    for (affected <- affectedVersions) { affected.tvalidity.to = transactionTime; }
    history = history.++(updatedVersions).::(new Temporal(t, p, transactionTime))
  }
  
  def get(validDate : Date = new Date(), transactionDate : Date = new Date) : T = {
    val filtered : List[Temporal[T]] = this.history.filter(_.active(transactionDate)).filter(_.validity.containsDate(validDate))
    if (filtered.size > 1) throw new RuntimeException("InvalidState")
    return filtered(0).value
  }
}

class Temporal[T](t: T, p: Period, d: Date) {
  val value = t;
  val validity = p;
  val tvalidity = new Period(d, new Date(Long.MaxValue))

  def active(when: Date = new Date()): Boolean = {
    return tvalidity.containsDate(when);
  }
  
  /**
   * Set this temporal version invalid for the given period. This may result
   * + in no active temporal version of this object at all if the new period contains the period of this object,
   * + one active version in case that the new period overlaps the beginning or end
   * + two active version in case that the new period is contained within the period of this object
   * + one active version (the current object) in case the given period does not overlap with the validity of this object.
   */
  
  def setInvalidFor(p: Period): List[Temporal[T]] = {
    val transactionTime = new Date()
    if (!this.validity.overlaps(p)) return List(this)
    if (this.validity.within(p)) return List()
    if (this.validity.contains(p)) {
      val firstPeriod = new Period(this.validity.from, p.from);
      val firstTemporal = new Temporal(this.value, firstPeriod, transactionTime);
      val thirdPeriod = new Period(p.to, this.validity.to)
      val thirdTemporal = new Temporal(this.value, thirdPeriod, transactionTime);
      return List(firstTemporal, thirdTemporal)
    }
    if (this.validity.overlapsStart(p)) {
      return List(new Temporal(this.value, new Period(this.validity.from, p.from), transactionTime))
    }
    if (this.validity.overlapsEnd(p)) {
      return List(new Temporal(this.value, new Period(p.to, this.validity.to), transactionTime))
    }
    throw new Exception("This should never happen.");
  }

} 

