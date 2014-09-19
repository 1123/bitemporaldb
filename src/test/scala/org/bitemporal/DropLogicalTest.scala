package org.bitemporal

import java.util.{Calendar, Date}
import org.bitemporal.domain.Student
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.BeforeAndAfter

/**
 * TODO: should dropping an object from the database be equivalent to setting the validity to an empty period?
 */

class DropLogicalTest extends FlatSpec with Matchers with BeforeAndAfter {

  MemoryDb.clearDatabase()
  
  var s1Id = 0
  var s2Id = 0

  val s1 = new Student("Some", "Body")
  val s2 = new Student("Some", "Head")
  
  val cal : Calendar = Calendar.getInstance()
  val today : Date = cal.getTime

  cal.add(Calendar.DATE, -1)
  val yesterday : Date = cal.getTime	
	  
  cal.add(Calendar.DATE, 2)
  val tomorrow : Date = cal.getTime
  
  behavior of "a MemoryDb"

  it should "be able to store objects with a validity" in {
	  s1Id = MemoryDb.store(s1, new Period(yesterday, tomorrow))
	  s2Id = MemoryDb.store(s2, new Period(yesterday, tomorrow))
  }

  it should "correctly count logical instances" in {
	  MemoryDb.countLogical(s1) should be (2)
  }

  it should "correctly count temporal and technical instances of a logical object" in {
	  MemoryDb.countInstances(s1Id, new Student()) should be (1)
  }
  
  it should "allow to find objects using a bitemporal context" in {
      val queryContext : BitemporalContext = new BitemporalContext(new Date(), new Date())
	  MemoryDb.findLogical(new Student(), s1Id, queryContext).get.element should be (s1)
	  MemoryDb.findLogical(new Student(), s2Id, queryContext).get.element should be (s2)
  }
  
  it should "be able to drop logical objects with all its instances" in {
	  MemoryDb.dropLogical(s1Id, new Student())
	  MemoryDb.countLogical(s1) should be (1)
	  MemoryDb.countInstances(s1Id, new Student()) should be (0)
  }
  
}
