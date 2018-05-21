/*
 * Copyright 2018 HM Revenue & Customs
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
import models.{Submission, SubmissionResponse}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import utils.MockUserAnswers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CtrConnectorSpec extends SpecBase with MockitoSugar with ScalaFutures {

  "submission" must {

    "return an Submission Response when the HTTP call succeeds" in {

      implicit val hc: HeaderCarrier = HeaderCarrier()

      val answers = MockUserAnswers.minimalValidUserAnswers

      val submission = Submission(answers)

      val httpMock = mock[HttpClient]
      when(httpMock.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(200, Some(Json.parse("""{"id":"id","filename":"filename"}""")))))

      val connector = new CtrConnector(frontendAppConfig, httpMock)
      val futureResult = connector.ctrSubmission(Json.toJson(submission))

      whenReady(futureResult) { result =>
        result mustBe Some(SubmissionResponse("id", "filename"))
      }
    }

    "return nothing when the HTTP call fails" in {
      implicit val hc: HeaderCarrier = HeaderCarrier()

      val answers = MockUserAnswers.minimalValidUserAnswers

      val submission = Submission(answers)

      val httpMock = mock[HttpClient]
      when(httpMock.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
        .thenReturn(Future.successful(HttpResponse(500, None)))

      val connector = new CtrConnector(frontendAppConfig, httpMock)
      val futureResult = connector.ctrSubmission(Json.toJson(submission))

      whenReady(futureResult) { result =>
        result mustBe None
      }
    }

    "return nothing when the HTTP call exceptions" in {
      implicit val hc: HeaderCarrier = HeaderCarrier()

      val answers = MockUserAnswers.minimalValidUserAnswers

      val submission = Submission(answers)

      val httpMock = mock[HttpClient]
      when(httpMock.POST[JsValue, HttpResponse](any(), any(), any())(any(), any(), any(), any()))
        .thenReturn(Future.failed(new RuntimeException("call failed")))

      val connector = new CtrConnector(frontendAppConfig, httpMock)
      val futureResult = connector.ctrSubmission(Json.toJson(submission))

      whenReady(futureResult) { result =>
        result mustBe None
      }
    }

  }
}