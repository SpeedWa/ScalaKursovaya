package com.kr

import akka.actor.typed.{ActorRef, ActorSystem, Scheduler}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path
import UserRegistry.{ActionPerformed, CreateUser, DeleteUser, GetUser, GetUserResponse, GetUsers}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class UserRoutes(userRegistryActor: ActorRef[UserRegistry.Command])(implicit val system: ActorSystem[_]) {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  implicit val timeout: Timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration
  implicit val scheduler: Scheduler = system.scheduler

  val userRoutes: Route =
    pathPrefix("users") {
      concat(
        pathEnd {
          concat(
            get {
              val users: Future[Users] = userRegistryActor.ask(replyTo => GetUsers(replyTo))
              complete(users)
            },
            post {
              entity(as[User]) { user =>
                val userCreated: Future[ActionPerformed] = userRegistryActor.ask(replyTo => CreateUser(user, replyTo))
                onSuccess(userCreated) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        path(Segment) { name =>
          concat(
            get {
              val getUser: Future[GetUserResponse] = userRegistryActor.ask(replyTo => GetUser(name, replyTo))
              rejectEmptyResponse {
                onSuccess(getUser) { response =>
                  complete(response.maybeUser)
                }
              }
            },
            delete {
              val userDeleted: Future[ActionPerformed] = userRegistryActor.ask(replyTo => DeleteUser(name, replyTo))
              onSuccess(userDeleted) { performed =>
                complete((StatusCodes.OK, performed))
              }
            }
          )
        }
      )
    }
}
