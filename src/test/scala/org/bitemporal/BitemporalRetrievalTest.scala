package org.bitemporal

import domain.Student
import org.scalatest.{ Matchers, FlatSpec }

class BitemporalRetrievalTest extends FlatSpec with Matchers {

  behavior of "a MemoryDB"

  it should " be able to retrieve objects based on a bitemporal context " in {
    MemoryDb.clearDatabase()

    // at d1, store object with validity from d1 to d2
    // at d3, store a different object with validity from d3 to d4
    val s = new Student("Some", "Body")
    val t = new Student("Some", "Where")
    val id1 = MemoryDb.store(s, TestData.d1, new Period(TestData.d1, TestData.d2))
    val id2 = MemoryDb.store(t, TestData.d3, new Period(TestData.d3, TestData.d4))

    val template = new Student()

    // s has validity between d1 and d2. It is not valid at d3.
    MemoryDb.findLogical(template, id1, new BitemporalContext(TestData.d2, TestData.d3)) should be(None)
    // the upper bound of validity is exclusive
    MemoryDb.findLogical(template, id1, new BitemporalContext(TestData.d2, TestData.d2)) should be(None)
    // the lower bound of valid time is inclusive
    // also the lower bound of transaction time is inclusive
    MemoryDb.findLogical(template, id1, new BitemporalContext(TestData.d1, TestData.d1)).get.element should be(s)
    // technical validity is a semi-bounded interval
    MemoryDb.findLogical(template, id1, new BitemporalContext(TestData.d2, TestData.d1)).get.element should be(s)
    // at d2, t is not technically valid
    MemoryDb.findLogical(template, id2, new BitemporalContext(TestData.d2, TestData.d3)) should be(None)
    // at d3, t is technically valid
    MemoryDb.findLogical(template, id2, new BitemporalContext(TestData.d3, TestData.d3)).get.element should be(t)
    // at d4, t is not temporally valid 
    MemoryDb.findLogical(template, id2, new BitemporalContext(TestData.d3, TestData.d4)) should be(None)

    // at d2 store another version of s with validity from d2 to d3. This does not overlap with the previous version.
    val s1 = new Student("Some", "Body")
    MemoryDb.updateLogical(id1, s1, new Period(TestData.d2, TestData.d3), TestData.d2)
    MemoryDb.findLogical(template, id1, new BitemporalContext(TestData.d2, TestData.d2)).get.element should be(s1)
  }
}
