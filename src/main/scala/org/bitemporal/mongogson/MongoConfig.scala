package org.bitemporal.mongogson

import org.joda.time.DateTime

object MongoConf {

  val minimumDate = new DateTime(0,1,1,0,0,0).toDate()
  val maximumDate = new DateTime(100000,1,1,0,0,0).toDate()

  var host = "127.0.0.1"
  var port = 27017
  var db = "nested_test"
    
  var logicalIdField = "logicalId"
  
}

