package org.bitemporal

import java.util.Date


class BitemporalContext(tDate : Date, vDate : Date) {

  val transactionDate : Date = tDate
  val validDate : Date = vDate

}
