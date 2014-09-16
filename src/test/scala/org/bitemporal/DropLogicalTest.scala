package org.bitemporal

import java.util.{Calendar, Date}

import org.bitemporal.domain.Student
import org.scalatest.{FlatSpec, Matchers}

/**
 * TODO: should dropping an object from the database be equivalent to setting the validity to an empty period?
 */

class DropLogicalTest extends FlatSpec with Matchers {

  MemoryDb.clearDatabase()

  val cal : Calendar = Calendar.getInstance()
  val today : Date = cal.getTime
  cal.add(Calendar.DATE, -1)
  val yesterday : Date = cal.getTime
  cal.add(Calendar.DATE, 2)
  val tomorrow : Date = cal.getTime

  val s1 = new Student("Some", "Body")
  val s2 = new Student("Some", "Head")

  val s1Id = MemoryDb.store(s1, new Period(yesterday, tomorrow))
  val s2Id = MemoryDb.store(s2, new Period(yesterday, tomorrow))

  val queryContext : BitemporalContext = new BitemporalContext(new Date(), new Date())

  MemoryDb.findLogical(new Student(), s1Id, queryContext).get.element should be (s1)
  MemoryDb.findLogical(new Student(), s2Id, queryContext).get.element should be (s2)

  MemoryDb.countLogical(s1) should be (2)
  MemoryDb.countInstances(s1Id, new Student()) should be (1)
  MemoryDb.dropLogical(s1Id, new Student())
  MemoryDb.countLogical(s1) should be (1)
  MemoryDb.countInstances(s1Id, new Student()) should be (0)

}
