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

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import connectors.DataCacheConnector
import controllers.actions._
import config.FrontendAppConfig
import forms.HowMuchOtherTaxableIncomeForm
import identifiers.HowMuchOtherTaxableIncomeId
import models.{Mode, SelectTaxYear}
import play.api.mvc.{Action, AnyContent, Result}
import utils.{Navigator, UserAnswers}
import views.html.howMuchOtherTaxableIncome

import scala.concurrent.Future

class HowMuchOtherTaxableIncomeController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        authenticate: AuthAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formBuilder: HowMuchOtherTaxableIncomeForm) extends FrontendController with I18nSupport {

  private val form: Form[String] = formBuilder()

  def onPageLoad(mode: Mode): Action[AnyContent] = (authenticate andThen getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.howMuchOtherTaxableIncome match {
        case None => form
        case Some(value) => form.fill(value)
      }

      val details: Option[Result] = for(
        selectedTaxYear: SelectTaxYear <- request.userAnswers.selectTaxYear;
        taxableIncomeName: String <- request.userAnswers.otherTaxableIncomeName
      ) yield {
        Ok(howMuchOtherTaxableIncome(appConfig, preparedForm, mode, selectedTaxYear, taxableIncomeName))
      }
      details.getOrElse{
        Redirect(routes.SessionExpiredController.onPageLoad())
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      val details: Option[Future[Result]] = for(
        selectedTaxYear: SelectTaxYear <- request.userAnswers.selectTaxYear;
        taxableIncomeName: String <- request.userAnswers.otherTaxableIncomeName
      ) yield {
        form.bindFromRequest().fold(
          (formWithErrors: Form[_]) =>
            Future.successful(BadRequest(howMuchOtherTaxableIncome(appConfig, formWithErrors, mode, selectedTaxYear, taxableIncomeName))),
          value =>
            dataCacheConnector.save[String](request.externalId, HowMuchOtherTaxableIncomeId.toString, value).map(cacheMap =>
              Redirect(navigator.nextPage(HowMuchOtherTaxableIncomeId, mode)(new UserAnswers(cacheMap))))
        )
      }
      details.getOrElse{
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
  }
}
