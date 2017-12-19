/*
 * Copyright 2017 HM Revenue & Customs
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

import play.api.data.Form
import controllers.routes
import forms.BooleanForm
import views.behaviours.YesNoViewBehaviours
import models.NormalMode
import views.html.whereToSendPayment

class WhereToSendPaymentViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "whereToSendPayment"

  override val form = new BooleanForm()()

  def createView = () => whereToSendPayment(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[_]) => whereToSendPayment(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "WhereToSendPayment view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.WhereToSendPaymentController.onSubmit(NormalMode).url)
  }
}