package org.bitemporal.mongodb.gson

import java.lang.reflect.Type
import org.bitemporal.Period
import org.joda.time.DateTime
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bitemporal.mongogson.SimpleTemporal
import org.bitemporal.domain.Building

/**
 * This test just shows that a generic SimpleTemporal can be serialized and deserialized with Gson, 
 * finally yielding the original object.  
 */

class GsonTemporalTest extends FlatSpec with Matchers {

  behavior of "Gson"

  it should "be able to serialize instances of SimpleTemporal" in {
    val vPeriod = new Period(new DateTime(2014,1,1,0,0,0).toDate(), new DateTime(2015,1,1,0,0,0).toDate())
    val myTemporal = new SimpleTemporal[Building](new Building("Downing Street 10"), vPeriod)
    val myTemporalType : Type = new TypeToken[SimpleTemporal[Building]]() { }.getType();
    val json = new Gson().toJson(myTemporal, myTemporalType)
    json should include ("\"value\":{\"address\":\"Downing Street 10\"}")
    val parsed = new Gson().fromJson[SimpleTemporal[Building]](json, myTemporalType)
    parsed.value.address should be (myTemporal.value.address)
    parsed.vPeriod.from should be (new DateTime(2014,1,1,0,0,0).toDate())
  }
  
}