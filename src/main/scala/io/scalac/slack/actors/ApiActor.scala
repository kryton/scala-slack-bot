package io.scalac.slack.actors

import io.scalac.slack.actors.messages._
import io.scalac.slack.api.ResponseObject._
import io.scalac.slack.api.{RtmStartResponse, ApiTestResponse, AuthTestResponse}
import io.scalac.slack.errors.{ApiTestError, SlackError}
import spray.http.Uri
import spray.httpx.RequestBuilding._
import spray.json._

/**
 * Created on 21.01.15 20:32
 */
class ApiActor extends ClientActor {

  import context.dispatcher
  import io.scalac.slack.api.Unmarshallers._

  override def receive = {
    case ApiTest(param, error) =>
      log.debug("api.test requested")
      val params = Map("param" -> param, "error" -> error).collect { case (key, Some(value)) => key -> value}
      val uri = Uri(url("api.test")).withQuery(params)

      val futureResponse = request(Get(uri))

      val send = sender()
      futureResponse onSuccess {
        case result =>
          val res = result.parseJson.convertTo[ApiTestResponse]
          if (res.ok)
            send ! Ok(res.args)
          else
            send ! ApiTestError

      }

    case AuthTest(token) =>
      log.debug("auth.test requested")
      val uri = Uri(url("auth.test")).withQuery("token" -> token.key)

      val futureResponse = request(Get(uri))
      val send = sender()
      futureResponse onSuccess {
        case response =>
          val res = response.parseJson.convertTo[AuthTestResponse]
          if (res.ok)
            send ! AuthData(res)
          else
            send ! SlackError(res.error.get)
      }
    case RtmStart(token) =>
      log.debug("rtm.start requested")
      val uri = Uri(url("rtm.start")).withQuery("token" -> token.key)
      val futureResponse = request(Get(uri))

      val send = sender()

      futureResponse onSuccess {
        case response =>
          val res = response.parseJson.convertTo[RtmStartResponse]
          if(res.ok)
            send ! RtmData(res.url)

      }
  }
}
