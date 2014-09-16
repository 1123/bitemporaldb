package org.bitemporal

import domain.Student
import org.scalatest.{Matchers, FlatSpec}


class BitemporalRetrievalTest extends FlatSpec with Matchers {

  MemoryDb.clearDatabase()

  // at d1, store object with validity from d1 to d2
  // at d3, store a different object with validity from d3 to d4
  val s  = new Student("Some", "Body")
  val p1 = new Period(TestData.d1, TestData.d2)
  val t  = new Student("Some", "Where")
  val p2 = new Period(TestData.d3, TestData.d4)
  val id1 = MemoryDb.store(s, TestData.d1, p1)
  val id2 = MemoryDb.store(t, TestData.d3, p2)

  val template = new Student()

  // retrieve for the temporal context (technical: d2, actual: d3)
  MemoryDb.findLogical(template, id1, new BitemporalContext(TestData.d2, TestData.d3)) should be (None)
  MemoryDb.findLogical(template, id1, new BitemporalContext(TestData.d2, TestData.d2)) should be (None)
  MemoryDb.findLogical(template, id1, new BitemporalContext(TestData.d1, TestData.d1)).get.element should be (s)
  MemoryDb.findLogical(template, id1, new BitemporalContext(TestData.d2, TestData.d1)).get.element should be (s)
  MemoryDb.findLogical(template, id2, new BitemporalContext(TestData.d2, TestData.d3)) should be (None)
  MemoryDb.findLogical(template, id2, new BitemporalContext(TestData.d3, TestData.d3)).get.element should be (t)
  MemoryDb.findLogical(template, id2, new BitemporalContext(TestData.d3, TestData.d4)) should be (None)

  // at d2 store another version of s with validity from d2 to d3. This does not overlap with the previous version.
  val s1 = new Student("Some", "Body")
  MemoryDb.updateLogical(id1, s1, new Period(TestData.d2, TestData.d3), TestData.d2)
  MemoryDb.findLogical(template, id1, new BitemporalContext(TestData.d2, TestData.d2)).get.element should be (s1)

}
