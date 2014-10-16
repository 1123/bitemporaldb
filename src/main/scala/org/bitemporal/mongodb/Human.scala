package org.bitemporal.mongodb

import com.fasterxml.jackson.annotation.JsonProperty
import org.mongojack.ObjectId

class Human(a: Int) {

  @ObjectId
  val id: String = ""

  @JsonProperty
  val age: Int = a

}

