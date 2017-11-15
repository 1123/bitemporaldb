package org.bitemporal.domain

import java.util

class Airplane(t :String = "") {
  val typ: String = t
  val passengers : java.util.List[String] = new util.ArrayList[String]()
  
  passengers.add("me")
} 

