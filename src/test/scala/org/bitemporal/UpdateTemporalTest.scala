package org.bitemporal

import java.util.Date
import org.bitemporal.domain.Student
import org.scalatest.{FlatSpec, Matchers}
import org.joda.time.DateTime

/*
 * Store two temporal versions of the same object to the database.
 * Then find one version by date and update its logical properties.
 *
 * This should result in a new technical version of the updated temporal version.
 *
 * Use Case:
 * + A person has the name "Allen Doe" from Date d1 to Date d2
 * + The person marries on d3 and changes his name to "Allen Dot"
 * + We find out that the original name should have been "John Doe", and record this to the database.
 */

class UpdateTemporalTest extends FlatSpec with Matchers {

  private val d1 = new DateTime(2013,6,7,0,0,0).toDate // 2013-06-07
  private val d2 = new DateTime(2013,6,8,0,0,0).toDate // 2013-06-08
  private val d3 = new DateTime(2013,6,9,0,0,0).toDate // 2013-06-09
  private val d4 = new DateTime(2013,6,10,0,0,0).toDate // 2013-06-10

  behavior of "an InMemoryBitemporalDatabase"
  
  it should "allow updates for existing objects and retrieval with a bitemporal context" in {
    
    InMemoryBitemporalDatabase.clearDatabase()

    val s  = new Student("Allen", "Doe")
    val t  = new Student("Allen", "Dot")

    val sId = InMemoryBitemporalDatabase.store(s, new Period(d1, d2))

    // save the other temporal version
    InMemoryBitemporalDatabase.updateLogical(sId, t, new Period(d3, d4))
    val context1 =  new BitemporalContext(new Date(), d1)
    val retrieved1 : Temporal[Student, Int] = InMemoryBitemporalDatabase.findLogical(new Student(), sId, context1).get
    Thread.sleep(10)
    
    InMemoryBitemporalDatabase.updateLogical(sId, new Student("John", "Doe"), new Period(d1, d2))

    // since we are searching with the old context (the transaction time before we did the update),
    // we are expecting the old version of the Student.
    val retrieved2 = InMemoryBitemporalDatabase.findLogical(new Student(), sId, context1).get
    retrieved2.element should equal (retrieved1.element)

    // search with the new transaction time. Thus we expect to find the correct/updated name.
    val retrieved3 : Temporal[Student, Int] = InMemoryBitemporalDatabase.findLogical(new Student(), sId, new BitemporalContext(new Date(), d1)).get
    retrieved3.element.firstName should be ("John")
  }
}


