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
import forms.OtherCompanyBenefitsDetailsForm
import identifiers.OtherCompanyBenefitsDetailsId
import models.Mode
import utils.{Navigator, UserAnswers}
import views.html.otherCompanyBenefitsDetails

import scala.concurrent.Future

class OtherCompanyBenefitsDetailsController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        authenticate: AuthAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formBuilder: OtherCompanyBenefitsDetailsForm) extends FrontendController with I18nSupport {

  private val form: Form[String] = formBuilder()

  def onPageLoad(mode: Mode) = (authenticate andThen getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.otherCompanyBenefitsDetails match {
        case None => form
        case Some(value) => form.fill(value)
      }

      request.userAnswers.selectTaxYear.map{
        selectedTaxYear =>
          val taxYear = selectedTaxYear.asString
          Ok(otherCompanyBenefitsDetails(appConfig, preparedForm, mode, taxYear))
      }.getOrElse {
        Redirect(routes.SessionExpiredController.onPageLoad())
      }
  }

  def onSubmit(mode: Mode) = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      request.userAnswers.selectTaxYear.map {
        selectedTaxYear =>
          val taxYear = selectedTaxYear.asString
          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(otherCompanyBenefitsDetails(appConfig, formWithErrors, mode, taxYear))),
            (value) =>
              dataCacheConnector.save[String](request.externalId, OtherCompanyBenefitsDetailsId.toString, value).map(cacheMap =>
                Redirect(navigator.nextPage(OtherCompanyBenefitsDetailsId, mode)(new UserAnswers(cacheMap))))
          )
      }.getOrElse{
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
  }
}