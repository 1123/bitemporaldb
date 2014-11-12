[![Build Status](https://travis-ci.org/1123/StackExchangeImporter.png)](https://travis-ci.org/1123/bitemporaldb)

Bitemporal
----------

This is a prototype of a bitemporal, object oriented in-memory database written
in Scala. Next to the in-memory prototype, it also offers a persistent
bitemporal database based on MongoDB. MongoDB has been chosen as the first
persistent implementation, since it allows for document oriented storage and
therefore helps to simplify the data model of your application, reducing the
number of joins, which must be thought out carefully in a bitemporal context.

For an explanation of the concept of bitemporality see http://en.wikipedia.org/wiki/Temporal_database

The concept of bitemporal data management is important for many industries
ranging from banking over insurances to the public sector. Ordinary databases
per se only store our current knowledge about the current state of reality.
Bitemporal databases instead store the entire history of our knowledge about
the different states of reality at different points in time. Therefore bitemporal data
management allows to answer WHY-questions about the past, allowing to simulate
queries for any points in the past.

Bitemporal databases distinguish two time dimensions: the valid time and the
transaction time.  Valid time concerns the state of objects in reality, which
changes over time.  Transaction time concerns our assumptions or knowledge
about the objects in reality as they are saved in our bitemporal database.

Requirements
------------

* scala 
* sbt
* java 1.6 or higher

Usage
-----

Using this library is most easily understood by looking at the test cases in the example directory.



