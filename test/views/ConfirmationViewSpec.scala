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

package views

import config.FrontendAppConfig
import org.scalatest.mockito.MockitoSugar
import views.behaviours.ViewBehaviours
import views.html.confirmation

class ConfirmationViewSpec extends ViewBehaviours with MockitoSugar {

  val messageKeyPrefix = "confirmation"

  val appConfig: FrontendAppConfig = mock[FrontendAppConfig]

  def createView = () => confirmation(frontendAppConfig)(fakeRequest, messages, formPartialRetriever, templateRenderer)

  "Confirmation view" must {
    behave like normalPage(createView, messageKeyPrefix, None)

    "have its title in a highlight box" in {
      val doc = asDocument(createView())
      val h1 = doc.getElementsByTag("h1").first
      h1.parent.hasClass("govuk-box-highlight") mustBe true
    }

    "have a reference number in a highlight box" in {
      val doc = asDocument(createView())
      val referenceText = doc.getElementById("reference")
      referenceText.text mustBe messages("confirmation.reference") + " ABC-5678-DEF"
      referenceText.parent.hasClass("govuk-box-highlight") mustBe true
    }

    "have what happens next header and text" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messagesApi("confirmation.whatHappensNext.title"))
      assertContainsText(doc, messagesApi("confirmation.whatHappensNext.line1"))
    }

    "have a link to an exit survey" in {
      val doc = asDocument(createView())
      val link = doc.getElementById("survey-link")
      link.text mustBe messages("confirmation.survey.linkText")
      link.attr("href") must include("/feedback-survey/?origin=CTR")
    }
  }
}