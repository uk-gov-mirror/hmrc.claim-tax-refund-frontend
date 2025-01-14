/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import config.FrontendAppConfig
import models.requests.DataRequest
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Format
import play.api.i18n.Lang
import play.api.mvc.Request
import uk.gov.hmrc.http.cache.client.CacheMap
import utils.{UserAnswers, WireMockHelper}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class AddressLookupConnectorSpec extends SpecBase with MockitoSugar with WireMockHelper with GuiceOneAppPerSuite with ScalaFutures {
  lazy val dataCacheConnector: DataCacheConnector = new DataCacheConnector {
    override def save[A](cacheId: String, key: String, value: A)(implicit fmt: Format[A]): Future[CacheMap] =
      Future.successful(CacheMap(cacheId, Map(key -> fmt.writes(value))))

    override def remove(cacheId: String, key: String): Future[Boolean] = ???

    override def removeAll(cacheId: String): Future[Boolean] = ???

    override def fetch(cacheId: String): Future[Option[CacheMap]] = ???

    override def getEntry[A](cacheId: String, key: String)(implicit fmt: Format[A]): Future[Option[A]] = ???

    override def addToCollection[A](cacheId: String, collectionKey: String, value: A)(implicit fmt: Format[A]): Future[CacheMap] = ???

    override def removeFromCollection[A](cacheId: String, collectionKey: String, item: A)(implicit fmt: Format[A]): Future[CacheMap] = ???

    override def replaceInCollection[A](cacheId: String, collectionKey: String, index: Int, item: A)(implicit fmt: Format[A]): Future[CacheMap] = ???
  }

  override implicit lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        conf = "microservice.services.address-lookup-frontend.port" -> server.port
      )
      .overrides(bind(classOf[DataCacheConnector]).toInstance(dataCacheConnector))
      .build()

  implicit val dataRequest: DataRequest[_] = mock[DataRequest[_]]
  implicit val ec: ExecutionContext = mock[ExecutionContext]
  implicit val request: Request[_] = mock[Request[_]]
  implicit val appConfig: FrontendAppConfig = mock[FrontendAppConfig]

  private lazy val connector: AddressLookupConnector = app.injector.instanceOf[AddressLookupConnector]

  private implicit val language = Lang("en")


  "AddressLookupConnector" must {

    "return a location when addressLookup.initialise" in {
      server.stubFor(
        post(urlEqualTo("/api/v2/init"))
          .willReturn(
            aResponse()
              .withStatus(202)
              .withHeader("Location", "/api/v2/location")
          )
      )

      val result: Option[String] = Await.result(connector.initialise(continueUrl = "", accessibilityFooterUrl = ""), 500.millisecond)
      result mustBe Some("/api/v2/location")

    }

    "return error when there is no Location" in {
      server.stubFor(
        post(urlEqualTo("/api/v2/init"))
          .willReturn(
            aResponse()
              .withStatus(202)
              .withBody("")
              .withHeader("notLocation", "")
          )
      )

      val result: Option[String] = Await.result(connector.initialise("", ""), 500.millisecond)
      result mustBe Some(s"[AddressLookupConnector][initialise] - Failed to obtain location from http://localhost:${server.port}/api/v2/init")
    }

    "return error when status is other than 202" in {
      server.stubFor(
        post(urlEqualTo("/api/v2/init"))
          .willReturn(
            aResponse()
              .withStatus(204)
          )
      )

      val result: Option[String] = Await.result(connector.initialise("", ""), 500.millisecond)
      result mustBe None
    }

    "get None when there is an error" in {
      server.stubFor(
        post(urlEqualTo("/api/v2/init"))
          .willReturn(
            aResponse()
          )
      )

      val result: Future[Option[String]] = connector.initialise("", "")
      whenReady(result) {
        res =>
          res mustBe None
      }

    }

    "return None when HTTP call fails" in {
      server.stubFor(
        post(urlEqualTo("/api/v2/init"))
          .willReturn(
            aResponse()
              .withStatus(400)
          )
      )

      val result: Future[Option[String]] = connector.initialise("", "")
      whenReady(result) {
        res =>
          res mustBe None
      }
    }

    "return cacheMap when called with ID" in {
      server.stubFor(
        get(urlEqualTo("/api/confirmed?id=123456789"))
          .willReturn(
            aResponse()
              .withStatus(200)
              .withBody(testResponseAddress.toString)
          )
      )

      val result: UserAnswers = Await.result(connector.getAddress(cacheId = "12345", saveKey = "saveKey", id = "123456789"), 5.second)
      result.cacheMap mustBe CacheMap("12345", Map("saveKey" -> testResponseAddress))
    }
  }
}

