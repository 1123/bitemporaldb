package org.bitemporal

import org.bitemporal.domain.Student
import org.scalatest._

/**
 * Store two __different__ objects of the same class in the database.
 * Retrieve the objects by logical id and assert they are equal to the ones stored.
 */

class BasicStorageTest extends FlatSpec with Matchers {

    behavior of "a BitemporalDatabase"

    InMemoryBitemporalDatabase.clearDatabase()

    var id1 : Int = 0;
    var id2 : Int = 0;
    var s = new Student("Some", "Body")
    var t = new Student("Some", "Where")
    
    it should "be able to store objects with a validity period" in {
      val v1: Period = new Period(TestData.d1, TestData.d2)
      val v2: Period = new Period(TestData.d3, TestData.d4)
      id1 = InMemoryBitemporalDatabase.store(s, v1)
      id2 = InMemoryBitemporalDatabase.store(t, v2)
    }
    
    it should "be able to retrieve all objects belonging to a logical id" in {
      InMemoryBitemporalDatabase.tableCount() should be(1)
      InMemoryBitemporalDatabase.collectionFor(s).get.countLogical should be(2)
      InMemoryBitemporalDatabase.countInstances(id1, new Student()) should be(1)
      InMemoryBitemporalDatabase.collectionFor(s).get.get(id1).head.element should be(s)
      InMemoryBitemporalDatabase.collectionFor(s).get.get(id2).head.element should be(t)
    }
    
}


