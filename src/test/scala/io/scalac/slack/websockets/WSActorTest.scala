package io.scalac.slack.websockets

import akka.actor.{ActorSystem, Props}

import scala.concurrent.Await

/**
 * Created on 28.01.15 22:03
 */
object WSActorTest {

  def main(args: Array[String]) {
    implicit lazy val system = ActorSystem("TestEchoServer")
    var wsmsg = ""
    val wse = system.actorOf(Props[WSActor])
    //websocket echo service
    wse ! WebSocket.Connect("ms25.slack-msgs.com", 443, "/websocket/9mOAPneoOcdvGXm_C6tLAkSXRG5be8aR/fDRj3/C1a8tmUP8YAFWJM1zANmgv8pn3oX4DdRecbCgmbSvFe8gzZB1GQ2wyPDjEWDVg3s7OFc=", withSsl = true)

    Thread.sleep(2000L) // wait for all servers to be cleanly started
    val rock = "Rock it with WebSocket"
    wse ! WebSocket.Send(rock)
    Thread.sleep(2000L)
    wse ! WebSocket.Release
    system.shutdown()
    Thread.sleep(1000L)
  }

}
