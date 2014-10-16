package org.bitemporal

import org.bitemporal.mongodb.Student
import org.scalatest.{ Matchers, FlatSpec }

class BitemporalRetrievalTest extends FlatSpec with Matchers {

  behavior of "a MemoryDB"

  InMemoryBitemporalDatabase.clearDatabase()
  // at d1, store object with validity from d1 to d2
  // at d3, store a different object with validity from d3 to d4
  val s = new Student("Some", "Body")
  val t = new Student("Some", "Where")
  val id1 = InMemoryBitemporalDatabase.store(s, TestData.d1, new Period(TestData.d1, TestData.d2))
  val id2 = InMemoryBitemporalDatabase.store(t, TestData.d3, new Period(TestData.d3, TestData.d4))
  val template = new Student()

  it should " return nothing when searching out of the validity of all stored objects " in {
    // s has validity between d1 and d2. It is not valid at d3.
    InMemoryBitemporalDatabase.findLogical(template, id1, new BitemporalContext(TestData.d2, TestData.d3)) should be(None)
    // the upper bound of validity is exclusive
    InMemoryBitemporalDatabase.findLogical(template, id1, new BitemporalContext(TestData.d2, TestData.d2)) should be(None)
  }
  
  it should " find the object when the bitemporal context is the lower bound of the valid time and/or the lower bound of the transaction time " in {
    // the lower bound of valid time is inclusive
    // also the lower bound of transaction time is inclusive
    InMemoryBitemporalDatabase.findLogical(template, id1, new BitemporalContext(TestData.d1, TestData.d1)).get.element should be(s)
  }
   
  it should " find objects that are technically active " in {
    // technical validity is a semi-bounded interval
    InMemoryBitemporalDatabase.findLogical(template, id1, new BitemporalContext(TestData.d2, TestData.d1)).get.element should be(s)
    // at d2, t is not technically valid
    InMemoryBitemporalDatabase.findLogical(template, id2, new BitemporalContext(TestData.d2, TestData.d3)) should be(None)
    // at d3, t is technically valid
    InMemoryBitemporalDatabase.findLogical(template, id2, new BitemporalContext(TestData.d3, TestData.d3)).get.element should be(t)
    // at d4, t is not temporally valid 
    InMemoryBitemporalDatabase.findLogical(template, id2, new BitemporalContext(TestData.d3, TestData.d4)) should be(None)

    // at d2 store another version of s with validity from d2 to d3. This does not overlap with the previous version.
    val s1 = new Student("Some", "Body")
    InMemoryBitemporalDatabase.updateLogical(id1, s1, new Period(TestData.d2, TestData.d3), TestData.d2)
    InMemoryBitemporalDatabase.findLogical(template, id1, new BitemporalContext(TestData.d2, TestData.d2)).get.element should be(s1)
  }
}
