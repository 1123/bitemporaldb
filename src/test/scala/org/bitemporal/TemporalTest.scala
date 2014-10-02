package org.bitemporal

import org.bitemporal.domain.Student
import org.scalatest.{FlatSpec, Matchers}

class TemporalTest extends FlatSpec with Matchers {

  InMemoryBitemporalDatabase.clearDatabase()

  // create two different temporal student objects
  val s1 : Temporal[Student] = new Temporal[Student](new Student("Some", "Body"), new Period(TestData.d1, TestData.d4))
  val s2 : Temporal[Student] = new Temporal[Student](new Student("Some", "Head"), new Period(TestData.d2, TestData.d3))

  // update one with the other
  val updated : List[Temporal[Student]] = s1.update(s2)
  // we expect two new versions to be generated from s
  updated.size should be (2)

  val expected : Temporal[Student] = new Temporal[Student](new Student("Some", "Body"), new Period(TestData.d1, TestData.d2))

  updated.head.vPeriod == expected.vPeriod should be (right = true)
  updated.head.element.lastName == "Body" should be (right = true)
  updated.tail.head.vPeriod == new Period(TestData.d3,TestData.d4) should be (right = true)
  updated.tail.head.element.lastName == "Body" should be (right = true)

}
