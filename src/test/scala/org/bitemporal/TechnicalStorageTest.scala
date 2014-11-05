package org.bitemporal

import org.scalatest.{FlatSpec, Matchers}
import java.util.Date
import org.bitemporal.domain.Student

class TechnicalStorageTest extends FlatSpec with Matchers {

  InMemoryBitemporalDatabase.clearDatabase()

  val s = new Student("Some", "Body")
  val sValidity = new Period(TestData.d1, TestData.d2)
  val t = new Student("Some", "Where")
  val tValidity = new Period(TestData.d1, TestData.d2)
  new Temporal(s, sValidity).active should be (right = true)
  new Temporal(t, tValidity).active should be (true)

  val sId = InMemoryBitemporalDatabase.store(s, sValidity)
  sId should be (0)
  InMemoryBitemporalDatabase.countLogical(s) should be (1)
  InMemoryBitemporalDatabase.countTechnical(s) should be (1)
  InMemoryBitemporalDatabase.countCollections should be (1)

  InMemoryBitemporalDatabase.updateLogical(0, t, tValidity)

  InMemoryBitemporalDatabase.countLogical(t) should be (1)
  InMemoryBitemporalDatabase.countInstances(sId, new Student()) should be (2)
  InMemoryBitemporalDatabase.countTechnical(t) should be (2)
  InMemoryBitemporalDatabase.countTemporal(t) should be (1)

}
