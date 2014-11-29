package org.bitemporal

import org.bitemporal.domain.Product
import org.scalatest.{Matchers, FlatSpec}

class OverlappingStorageTest extends FlatSpec with Matchers {

  InMemoryBitemporalDatabase.clearDatabase()

  val p1 = new Product(1, 1.2f, 2.3f)
  val p2 = new Product(1, 1.2f, 2.4f)

  val p1Id = InMemoryBitemporalDatabase.store(p1, new Period(TestData.d1, TestData.d3))
  InMemoryBitemporalDatabase.updateLogical(p1Id, p2, new Period(TestData.d2, TestData.d4))

  InMemoryBitemporalDatabase.countLogical(p1) should be (1)
  InMemoryBitemporalDatabase.countInstances(p1Id, new Product()) should be (3)
  InMemoryBitemporalDatabase.countTechnical(p1) should be (3)
  InMemoryBitemporalDatabase.activeObjects(p1) should be (2)
}
