package com.mcallydevelops

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.all._
import io.chrisdavenport.circuit.CircuitBreaker
import io.chrisdavenport.circuit.CircuitBreaker.RejectedExecution

import scala.concurrent.duration.DurationInt

trait Response {
  def number: Int
}

case class SuccessResponse(number: Int) extends Response
case class FailureResponse(number: Int, ex: Throwable) extends Response
case class RejectedResponse(number: Int) extends Response
case class Request(number: Int)

class Service(circuitBreaker: CircuitBreaker[IO]) {
  def process(request: Request): IO[Response] = {
    circuitBreaker.protect(ExternalService.process(request.number))
      .handleErrorWith {
        case RejectedExecution(_) => IO.delay(RejectedResponse(1))
        case e: Exception => IO.delay(FailureResponse(1, e))
      }
  }
}

object ExternalService {
  def process(number: Int): IO[Response] =
    if(number < 10) {
      IO.pure(SuccessResponse(1))
    } else {
      IO.raiseError(new RuntimeException)
    }

}
object Application extends IOApp{
  val circuitBreaker = CircuitBreaker.of[IO](
     maxFailures = 5,
     resetTimeout = 10.seconds,
     exponentialBackoffFactor = 2,
     maxResetTimeout = 10.minutes
  )

  override def run(args: List[String]): IO[ExitCode] = {
    val result = circuitBreaker.flatMap { cb =>
      (1 to 100).toList.map { num =>
        new Service(cb).process(Request(num))
      }.sequence
    }

    result.flatTap(f => IO.delay(f.map { y =>
      println(y)
    })).as(ExitCode.Success)
  }
}
