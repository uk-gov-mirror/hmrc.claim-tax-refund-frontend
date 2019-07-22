/*
 * Copyright 2019 HM Revenue & Customs
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

import controllers.routes
import forms.HowMuchJobseekersAllowanceForm
import models.NormalMode
import models.SelectTaxYear.CustomTaxYear
import org.scalatest.mockito.MockitoSugar
import play.api.data.Form
import views.behaviours.StringViewBehaviours
import views.html.howMuchJobseekersAllowance

class HowMuchJobseekersAllowanceViewSpec extends StringViewBehaviours with MockitoSugar {

  private val messageKeyPrefix = "howMuchJobseekersAllowance"
  private val taxYear = CustomTaxYear(2017)

  override val form: Form[String] = new HowMuchJobseekersAllowanceForm(frontendAppConfig)()

  def createView = () => howMuchJobseekersAllowance(frontendAppConfig, form, NormalMode, taxYear)(fakeRequest, messages, formPartialRetriever, scalate)

  def createViewUsingForm = (form: Form[String]) => howMuchJobseekersAllowance(frontendAppConfig, form, NormalMode, taxYear)(fakeRequest, messages, formPartialRetriever, scalate)

  "HowMuchJobseekersAllowance view" must {
    behave like normalPage(createView, messageKeyPrefix, None)

    behave like pageWithBackLink(createView)

    behave like pageWithSecondaryHeader(createView, messages("site.service_name.with_tax_year", taxYear.asString(messages)))

    behave like stringPage(
      createView = createViewUsingForm,
      messageKeyPrefix = messageKeyPrefix,
      expectedFormAction = routes.HowMuchJobseekersAllowanceController.onSubmit(NormalMode).url,
      expectedHintKeyLine1 = None,
      expectedHintKeyLine2 = None
    )

  }
}
