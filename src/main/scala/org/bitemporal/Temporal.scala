package org.bitemporal

import java.util.Date

class Temporal[T, I](t: T, v: Period) {

  val element: T = t
  val vPeriod: Period = v
  val tPeriod = new Period(new Date(Long.MinValue), new Date(Long.MaxValue))
  var logicalId: Option[I] = None
  var technicalId: Option[I] = None

  def active: Boolean = {
    this.tPeriod.to == new Date(Long.MaxValue)
  }

  def update(t: Temporal[T, I]): List[Temporal[T, I]] = {
    this.update(t, new Date())
  }

  def update(t: Temporal[T, I], when: Date): List[Temporal[T, I]] = {
    if (!this.vPeriod.overlaps(t.vPeriod)) {
      throw new RuntimeException("You cannot update this object with a non-overlapping new object.")
    }
    if (this.vPeriod.within(t.vPeriod)) {
      return List()
    }
    if (this.vPeriod.contains(t.vPeriod)) {
      val first: Temporal[T, I] = new Temporal[T, I](this.element, new Period(this.vPeriod.from, t.vPeriod.from))
      first.tPeriod.from = when
      val third: Temporal[T, I] = new Temporal[T, I](this.element, new Period(t.vPeriod.to, this.vPeriod.to))
      third.tPeriod.from = when
      return List(first, third)
    }
    if (this.vPeriod.overlapsStart(t.vPeriod)) {
      val first: Temporal[T, I] = new Temporal[T, I](this.element, new Period(this.vPeriod.from, t.vPeriod.from))
      first.tPeriod.from = when
      return List(first)
    }
    if (this.vPeriod.overlapsEnd(t.vPeriod)) {
      val second: Temporal[T, I] = new Temporal[T, I](this.element, new Period(t.vPeriod.to, this.vPeriod.to))
      second.tPeriod.from = when
      return List(second)
    }
    throw new RuntimeException("Program error. This should never happen.")
  }

  override def equals(other: Any): Boolean = {
    // current and saved are ignored when comparing.
    if (!other.isInstanceOf[Temporal[T, I]]) return false
    val otherTemporal = other.asInstanceOf[Temporal[T, I]]
    if (otherTemporal.vPeriod != this.vPeriod) return false
    if (otherTemporal.element != this.element) return false
    true
  }

}



