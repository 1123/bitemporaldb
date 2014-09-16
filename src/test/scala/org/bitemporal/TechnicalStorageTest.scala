package org.bitemporal

import org.scalatest.{FlatSpec, Matchers}
import java.util.Date
import org.bitemporal.domain.Student

class TechnicalStorageTest extends FlatSpec with Matchers {

  MemoryDb.clearDatabase()

  val s = new Student("Some", "Body")
  val sValidity = new Period(TestData.d1, TestData.d2)
  val t = new Student("Some", "Where")
  val tValidity = new Period(TestData.d1, TestData.d2)
  new Temporal(s, sValidity).active should be (right = true)
  new Temporal(t, tValidity).active should be (true)

  val sId = MemoryDb.store(s, sValidity)
  sId should be (0)
  MemoryDb.countLogical(s) should be (1)
  MemoryDb.countTechnical(s) should be (1)
  MemoryDb.countCollections should be (1)

  MemoryDb.updateLogical(0, t, tValidity)

  MemoryDb.countLogical(t) should be (1)
  MemoryDb.countInstances(sId, new Student()) should be (2)
  MemoryDb.countTechnical(t) should be (2)
  MemoryDb.countTemporal(t) should be (1)

}
