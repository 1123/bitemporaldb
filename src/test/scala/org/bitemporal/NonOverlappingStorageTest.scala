package org.bitemporal

import org.bitemporal.domain.Student
import org.scalatest.{FlatSpec, Matchers}

/**
 * Store two temporal versions of the same object with non-overlapping validity.
 */

class NonOverlappingStorageTest extends FlatSpec with Matchers {

  InMemoryBitemporalDatabase.clearDatabase()

  val s = new Student("Some", "Body")
  val t = new Student("Some", "Head")

  val sLogicalId = InMemoryBitemporalDatabase.store(s,  new Period(null, TestData.d1))
  InMemoryBitemporalDatabase.countLogical(s) should be (1)
  InMemoryBitemporalDatabase.countTechnical(s) should be (1)

  InMemoryBitemporalDatabase.updateLogical(sLogicalId, t, new Period(TestData.d2, TestData.d3))
  InMemoryBitemporalDatabase.countTechnical(s) should be (2)
  InMemoryBitemporalDatabase.countTemporal(s) should be (2)
  InMemoryBitemporalDatabase.countLogical(s) should be (1)

}
