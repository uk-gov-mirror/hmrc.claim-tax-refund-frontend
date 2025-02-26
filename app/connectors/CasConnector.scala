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

import javax.inject.Singleton
import com.google.inject.{ImplementedBy, Inject}
import config.FrontendAppConfig
import models.{SubmissionArchiveRequest, SubmissionArchiveResponse}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import uk.gov.hmrc.http.HttpReads.Implicits._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CasConnectorImpl @Inject()(appConfig: FrontendAppConfig, val http: HttpClient) extends CasConnector with Logging {

    def archiveSubmission(submissionRef: String, data: SubmissionArchiveRequest)(implicit hc: HeaderCarrier, ec:ExecutionContext): Future[SubmissionArchiveResponse] = {
      logger.debug(s"Sending submission $submissionRef to CAS via DMS API")

    val url: String = s"${appConfig.ctrUrl}/claim-tax-refund/archive-submission/"
    http.POST[SubmissionArchiveRequest, SubmissionArchiveResponse](url, data)
  }
}

@ImplementedBy(classOf[CasConnectorImpl])
trait CasConnector {
  def archiveSubmission(submissionRef: String, data: SubmissionArchiveRequest)(implicit hc: HeaderCarrier, ec:ExecutionContext): Future[SubmissionArchiveResponse]
}
