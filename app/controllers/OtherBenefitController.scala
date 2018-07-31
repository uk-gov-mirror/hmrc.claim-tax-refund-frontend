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
import forms.OtherBenefitForm
import identifiers.OtherBenefitId
import javax.inject.Inject
import models.{Index, Mode, OtherBenefit}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Navigator, SequenceUtil, UserAnswers}
import views.html.otherBenefit

import scala.concurrent.Future

class OtherBenefitController @Inject()(
                                             appConfig: FrontendAppConfig,
                                             override val messagesApi: MessagesApi,
                                             dataCacheConnector: DataCacheConnector,
                                             navigator: Navigator,
                                             authenticate: AuthAction,
                                             getData: DataRetrievalAction,
                                             requireData: DataRequiredAction,
                                             sequenceUtil: SequenceUtil[OtherBenefit],
                                             formBuilder: OtherBenefitForm) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode, index: Index): Action[AnyContent] = (authenticate andThen getData andThen requireData) {
    implicit request =>
      val form = formBuilder(request.userAnswers.otherBenefit.getOrElse(Seq.empty), index)

      val preparedForm = request.userAnswers.otherBenefit match {
        case Some(value) =>
          if (index >= value.length) form else form.fill(value(index))
        case None => form
      }

      request.userAnswers.selectTaxYear.map {
        selectedTaxYear =>
          Ok(otherBenefit(appConfig, preparedForm, mode, index, selectedTaxYear))
      }.getOrElse {
        Redirect(routes.SessionExpiredController.onPageLoad())
      }
  }

  def onSubmit(mode: Mode, index: Index): Action[AnyContent] = (authenticate andThen getData andThen requireData).async {
    implicit request =>
      val form = formBuilder(request.userAnswers.otherBenefit.getOrElse(Seq.empty), index)

      request.userAnswers.selectTaxYear.map {
        selectedTaxYear =>
          val taxYear = selectedTaxYear
          form.bindFromRequest().fold(
            (formWithErrors: Form[OtherBenefit]) =>
              Future.successful(BadRequest(otherBenefit(appConfig, formWithErrors, mode, index, taxYear))),
            value => {
              val otherBenefits: Seq[OtherBenefit] = request.userAnswers.otherBenefit.getOrElse(Seq(value))
              dataCacheConnector.save[Seq[OtherBenefit]](
                request.externalId,
                OtherBenefitId.toString,
                sequenceUtil.updateSeq(otherBenefits, index, value)
              ).map(cacheMap =>
                Redirect(navigator.nextPage(OtherBenefitId, mode)(new UserAnswers(cacheMap))))
            }
          )
      }.getOrElse {
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
  }

}