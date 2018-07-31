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
import forms.OtherTaxableIncomeForm
import identifiers.OtherTaxableIncomeId
import models.{Index, Mode, OtherTaxableIncome}
import play.api.mvc.{Action, AnyContent}
import utils.{Navigator, SequenceUtil, UserAnswers}
import views.html.otherTaxableIncome

import scala.concurrent.Future

class OtherTaxableIncomeController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        authenticate: AuthAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        sequenceUtil: SequenceUtil[OtherTaxableIncome],
                                        formBuilder: OtherTaxableIncomeForm) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (authenticate andThen getData andThen requireData) {
    implicit request =>
      val form: Form[OtherTaxableIncome] = formBuilder(request.userAnswers.otherTaxableIncome.getOrElse(Seq.empty), index)

      val preparedForm = request.userAnswers.otherTaxableIncome match {
        case Some(value) =>
          if (index >= value.length) form else form.fill(value(index))
        case None => form
      }

      request.userAnswers.selectTaxYear.map {
        selectedTaxYear =>
          val taxYear = selectedTaxYear
          Ok(otherTaxableIncome(appConfig, preparedForm, mode, index, taxYear))
      }.getOrElse {
        Redirect(routes.SessionExpiredController.onPageLoad())
      }
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      val form: Form[OtherTaxableIncome] = formBuilder(request.userAnswers.otherTaxableIncome.getOrElse(Seq.empty), index)

      request.userAnswers.selectTaxYear.map {
        selectedTaxYear =>
          val taxYear = selectedTaxYear
          form.bindFromRequest().fold(
            (formWithErrors: Form[OtherTaxableIncome]) =>
              Future.successful(BadRequest(otherTaxableIncome(appConfig, formWithErrors, mode, index, taxYear))),
            value => {
              val otherTaxableIncome: Seq[OtherTaxableIncome] = request.userAnswers.otherTaxableIncome.getOrElse(Seq(value))
              dataCacheConnector.save[Seq[OtherTaxableIncome]](
                request.externalId,
                OtherTaxableIncomeId.toString,
                sequenceUtil.updateSeq(otherTaxableIncome, index, value)
              ).map(cacheMap =>
                Redirect(navigator.nextPageWithIndex(OtherTaxableIncomeId(index), mode)(new UserAnswers(cacheMap))))
            }
          )
      }.getOrElse {
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
  }
}