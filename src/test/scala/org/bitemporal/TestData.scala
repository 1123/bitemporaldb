package org.bitemporal

import org.joda.time.format.DateTimeFormat
import java.util.Date

/*
 * this singleton class only provides test data for the real tests.
 */

object TestData {

  val dateFormat = DateTimeFormat.forPattern("dd-MM-yyyy")
  val d1 : Date = dateFormat.parseDateTime("07-06-2013").toDate
  val d2 : Date = dateFormat.parseDateTime("08-06-2013").toDate
  val d3 : Date = dateFormat.parseDateTime("09-06-2013").toDate
  val d4 : Date = dateFormat.parseDateTime("10-06-2013").toDate

}
