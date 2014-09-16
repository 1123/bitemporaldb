package org.bitemporal

import org.scalatest._

class PeriodTest extends FlatSpec with Matchers {

  /* Test functions for comparing periods */

  new Period(TestData.d1,TestData.d2).before(new Period(TestData.d3,TestData.d4)) should be (true)
  new Period(TestData.d1,TestData.d2).before(new Period(TestData.d2,TestData.d3)) should be (true)
  new Period(TestData.d1,TestData.d4).contains(new Period(TestData.d3,TestData.d4)) should be (true)
  new Period(TestData.d1,TestData.d3).contains(new Period(TestData.d3,TestData.d4)) should be (false)
  new Period(TestData.d1,TestData.d3).overlapsStart(new Period(TestData.d3,TestData.d4)) should be (false)
  new Period(TestData.d2,TestData.d4).overlapsEnd(new Period(TestData.d1,TestData.d3)) should be (true)
  new Period(TestData.d1,TestData.d3).within(new Period(TestData.d1,TestData.d3)) should be (true)

}


