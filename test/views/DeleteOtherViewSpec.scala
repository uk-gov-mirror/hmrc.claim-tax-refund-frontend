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

import play.api.data.Form
import controllers.routes
import forms.BooleanForm
import views.behaviours.YesNoViewBehaviours
import models.{Index, NormalMode}
import views.html.deleteOther

class DeleteOtherViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "deleteOther"
  val itemName = "qwerty"
  val index = Index(1)
  val benefitCollectionId = "otherBenefit"

  override val form = new BooleanForm()()

  def createView = () =>
    deleteOther(frontendAppConfig, form, NormalMode, index, itemName, benefitCollectionId)(fakeRequest, messages, formPartialRetriever, templateRenderer)

  def createViewUsingForm = (form: Form[_]) =>
    deleteOther(frontendAppConfig, form, NormalMode, index, itemName, benefitCollectionId)(fakeRequest, messages, formPartialRetriever, templateRenderer)

  "DeleteOther view" must {

    behave like normalPage(createView, messageKeyPrefix, None, "qwerty")

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.DeleteOtherController.onSubmit(index, itemName, benefitCollectionId).url, None)
  }
}
