package org.bitemporal.domain

import java.util.ArrayList

class Airplane(t :String = "") {
  val typ = t
  val passengers : java.util.List[String] = new ArrayList[String]()
  
  passengers.add("me")
} 

