package org.bitemporal.domain

import com.fasterxml.jackson.annotation.JsonProperty
import org.mongojack.ObjectId

class Student(fn : String = "", ln: String = "") {

  @ObjectId
  val id : String = ""
  
  @JsonProperty
  val firstName : String = fn
  @JsonProperty
  val lastName : String = ln

}
