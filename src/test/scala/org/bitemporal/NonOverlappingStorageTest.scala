package org.bitemporal

import org.bitemporal.domain.Student
import org.scalatest.{FlatSpec, Matchers}

/**
 * Store two temporal versions of the same object with non-overlapping validity.
 */

class NonOverlappingStorageTest extends FlatSpec with Matchers {

  MemoryDb.clearDatabase()

  val s = new Student("Some", "Body")
  val t = new Student("Some", "Head")

  val sLogicalId = MemoryDb.store(s,  new Period(null, TestData.d1))
  MemoryDb.countLogical(s) should be (1)
  MemoryDb.countTechnical(s) should be (1)

  MemoryDb.updateLogical(sLogicalId, t, new Period(TestData.d2, TestData.d3))
  MemoryDb.countTechnical(s) should be (2)
  MemoryDb.countTemporal(s) should be (2)
  MemoryDb.countLogical(s) should be (1)

}
