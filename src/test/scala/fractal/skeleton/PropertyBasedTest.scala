package fractal.skeleton

import org.scalatest.PropSpec
import org.scalatest.prop.{PropertyChecks, Checkers}
import org.scalatest.Matchers
import org.scalacheck.Prop._

class PropertyBasedTest extends PropSpec with PropertyChecks with Matchers {

  property("When n is bigger than 1, half n should be more than zero"){
 	forAll { (n: Int) =>
  		whenever (n > 1) { n / 2 should be > 0 }
	}
  }
}
