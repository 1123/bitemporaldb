package org.bitemporal.domain

class Building(a : String = "") {
  
  def this() {
    this("No Address given.")
  }
  
  val address = a
}
