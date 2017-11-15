package org.bitemporal

import org.bitemporal.domain.Student
import org.scalatest.{ Matchers, FlatSpec }
import org.joda.time.DateTime

class BitemporalRetrievalTest extends FlatSpec with Matchers {

  private val d1 = new DateTime(2013,6,7,0,0,0).toDate
  private val d2 = new DateTime(2013,6,8,0,0,0).toDate
  private val d3 = new DateTime(2013,6,9,0,0,0).toDate
  private val d4 = new DateTime(2013,6,10,0,0,0).toDate

  behavior of "an InMemoryBitemporalDatabase"

  InMemoryBitemporalDatabase.clearDatabase()
  // at d1, store object with validity from d1 to d2
  // at d3, store a different object with validity from d3 to d4
  private val s = new Student("Some", "Body")
  private val t = new Student("Some", "One")
  private val id1 = InMemoryBitemporalDatabase.store(s, d1, new Period(d1, d2))
  private val id2 = InMemoryBitemporalDatabase.store(t, d3, new Period(d3, d4))
  private val template = new Student()

  it should " return nothing when searching out of the validity of all stored objects " in {
    // s has validity between d1 and d2. It is not valid at d3.
    InMemoryBitemporalDatabase.findLogical(template, id1, new BitemporalContext(d2, d3)) should be (None)
    // the upper bound of validity is exclusive
    InMemoryBitemporalDatabase.findLogical(template, id1, new BitemporalContext(d2, d2)) should be (None)
  }
  
  it should " find the object when the bitemporal context is the lower bound of the valid time and/or the lower bound of the transaction time " in {
    // the lower bound of valid time is inclusive
    // also the lower bound of transaction time is inclusive
    InMemoryBitemporalDatabase.findLogical(template, id1, new BitemporalContext(d1, d1)).get.element should be(s)
  }
   
  it should " find objects that are technically active " in {
    // technical validity is a semi-bounded interval
    InMemoryBitemporalDatabase.findLogical(template, id1, new BitemporalContext(d2, d1)).get.element should be(s)
    // at d2, t is not technically valid
    InMemoryBitemporalDatabase.findLogical(template, id2, new BitemporalContext(d2, d3)) should be(None)
    // at d3, t is technically valid
    InMemoryBitemporalDatabase.findLogical(template, id2, new BitemporalContext(d3, d3)).get.element should be(t)
    // at d4, t is not temporally valid 
    InMemoryBitemporalDatabase.findLogical(template, id2, new BitemporalContext(d3, d4)) should be(None)

    // at d2 store another version of s with validity from d2 to d3. This does not overlap with the previous version.
    val s1 = new Student("Some", "Body")
    InMemoryBitemporalDatabase.updateLogical(id1, s1, new Period(d2, d3), d2)
    InMemoryBitemporalDatabase.findLogical(template, id1, new BitemporalContext(d2, d2)).get.element should be(s1)
  }
}
