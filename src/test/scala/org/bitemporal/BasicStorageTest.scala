package org.bitemporal

import org.bitemporal.domain.Student
import org.scalatest._

/**
 * Store two __different__ objects of the same class in the database.
 * Retrieve the objects by logical id and assert they are equal to the ones stored.
 */

class BasicStorageTest extends FlatSpec with Matchers {

    behavior of "a MemoryDb"

    it should "be able to store and retrieve objects with logical ids" in {
      MemoryDb.clearDatabase()
      val s: Student = new Student("Some", "Body")
      val v1: Period = new Period(TestData.d1, TestData.d2)
      val t: Student = new Student("Some", "Where")
      val v2: Period = new Period(TestData.d3, TestData.d4)
      val id1 = MemoryDb.store(s, v1)
      val id2 = MemoryDb.store(t, v2)
      MemoryDb.tableCount() should be(1)
      MemoryDb.collectionFor(s).get.countLogical should be(2)
      MemoryDb.countInstances(id1, new Student()) should be(1)
      MemoryDb.collectionFor(s).get.get(id1).head.element should be(s)
      MemoryDb.collectionFor(s).get.get(id2).head.element should be(t)
    }
    
}


