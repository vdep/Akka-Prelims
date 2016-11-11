

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.util.Random

case class NewFeature(name : String)
case class BusyDeveloper(currentWork : String)
object Bug
object Fix


class TesterActor extends Actor {

  override def receive = {
    case feature : NewFeature => {
      println("testing feature : " + feature.name)
      val bugExist : Boolean = if(Random.nextInt(10) < 7) true else false
      if(bugExist) {
        println("bug exists in the feature : " + feature.name + ", reverting to dev : " + sender().path)
        sender() ! Bug
      }
      else println("no bugs")
    }
    case Fix => println("verifying fix sent by : " + sender().path)
    case notAbleToDo : BusyDeveloper => {
      println("Due to " + notAbleToDo.currentWork + " assigning fix to other dev")

    }
    case _ => println("playing TT")
  }
}

class DeveloperActor(tester : ActorRef) extends Actor {

  override def receive = {
    case feature : NewFeature => {
      println("working on feature : " + feature.name)
      tester ! feature
    }
    case Bug => {
      val isFree : Boolean = if(Random.nextInt(10) > 5) true else false
      if(isFree) {
        println("fixing bug reported by : " + sender().path)
        sender() ! Fix
      }
      else sender() ! BusyDeveloper("FeatureX")
    }
    case _ => println("watching youtube")
  }
}

object SandBox {

  def main(args : Array[String]) : Unit = {
    val system = ActorSystem.create("system")

    val tester = system.actorOf(Props[TesterActor], "alice")
    val developerBob = system.actorOf(Props(new DeveloperActor(tester)), "bob")
    val developerTom = system.actorOf(Props(new DeveloperActor(tester)), "tom")

    developerBob ! NewFeature("optimization")
    system.terminate()
  }


}
