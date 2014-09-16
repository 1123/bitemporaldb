package org.bitemporal

import java.util.Date

class Period(f : Date, t : Date) {


  var from: Date = if (f == null) {
    new Date(Long.MinValue)
  } else {
    f
  }
  var to: Date = if (t == null) { new Date(Long.MaxValue) } else { t }

  def before(other : Period) : Boolean = {
    this.lte(this.to, other.from)
  }

  def after(other: Period) : Boolean = {
    this.lte(other.to, this.from)
  }

  def within(other : Period) : Boolean = {
    this.lte(other.from, this.from) && this.lte(this.to, other.to)
  }

  def contains(other : Period) : Boolean = {
    other.within(this)
  }

  def overlapsStart(other : Period) : Boolean = {
    this.containsDateStrict(other.from) && (!this.contains(other))
  }

  def disjunct(other: Period): Boolean = {
    this.before(other) || this.after(other)
  }

  def overlaps(other : Period) : Boolean = {
    this.overlapsStart(other) || this.overlapsEnd(other) || this.contains(other) || this.within(other)
  }

  def overlapsEnd(other : Period) : Boolean = {
    this.containsDate(other.to) && (!this.contains(other))
  }

  // lower bound inclusive, upper bound exclusive
  def containsDate(d : Date) : Boolean = {
    this.lte(this.from, d) && d.before(this.to)
  }

  def containsDateStrict(d : Date) : Boolean = {
    this.from.before(d) && d.before(this.to)
  }

  def lte(a : Date, b : Date): Boolean = {
    a.before(b) || a.equals(b)
  }

  def split(d : Date) : List[Period] = {
    if (this.containsDateStrict(d)) {
      return List(new Period(this.from, d), new Period(d, this.to))
    }
    List(this)
  }

  override def equals(that: Any) : Boolean = {
    if (! that.isInstanceOf[Period]) return false
    val thatPeriod : Period = that.asInstanceOf[Period]
    this.from.equals(thatPeriod.from) && this.to.equals(thatPeriod.to)
  }

}
