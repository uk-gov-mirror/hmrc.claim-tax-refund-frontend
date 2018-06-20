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

package controllers

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import forms.TelephoneNumberForm
import identifiers.{AnyTelephoneId, TelephoneNumberId}
import javax.inject.Inject

import models.{Mode, TelephoneOption}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Navigator, UserAnswers}
import views.html.telephoneNumber

import scala.concurrent.Future

class TelephoneNumberController @Inject()(
                                           appConfig: FrontendAppConfig,
                                           override val messagesApi: MessagesApi,
                                           dataCacheConnector: DataCacheConnector,
                                           navigator: Navigator,
                                           authenticate: AuthAction,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           formBuilder: TelephoneNumberForm) extends FrontendController with I18nSupport {

  private val form: Form[TelephoneOption] = formBuilder()

  def onPageLoad(mode: Mode) = (authenticate andThen getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.anyTelephoneNumber match {
        case None => form
        case Some(value) => form.fill(value)
      }
      Ok(telephoneNumber(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(telephoneNumber(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[TelephoneOption](request.externalId, AnyTelephoneId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(TelephoneNumberId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
