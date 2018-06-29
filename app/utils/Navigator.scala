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

package utils

import controllers.routes
import identifiers._
import javax.inject.{Inject, Singleton}
import models.WhereToSendPayment.{Myself, Nominee}
import models.{Benefits, _}
import play.api.mvc.Call

@Singleton
class Navigator @Inject()() {

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    SelectTaxYearId -> (_ => routes.EmploymentDetailsController.onPageLoad(NormalMode)),
    EmploymentDetailsId -> employmentDetails,
    EnterPayeReferenceId -> (_ => routes.DetailsOfEmploymentOrPensionController.onPageLoad(NormalMode)),
    AnyBenefitsId -> anyBenefits,
    SelectBenefitsId -> selectBenefits,
    HowMuchBereavementAllowanceId -> benefitRouter(HowMuchBereavementAllowanceId.cyaId),
    HowMuchCarersAllowanceId -> benefitRouter(HowMuchCarersAllowanceId.cyaId),
    HowMuchJobseekersAllowanceId -> benefitRouter(HowMuchJobseekersAllowanceId.cyaId),
    HowMuchIncapacityBenefitId -> benefitRouter(HowMuchIncapacityBenefitId.cyaId),
    HowMuchEmploymentAndSupportAllowanceId -> benefitRouter(HowMuchEmploymentAndSupportAllowanceId.cyaId),
    HowMuchStatePensionId -> benefitRouter(HowMuchStatePensionId.cyaId),
    AnyOtherBenefitsId -> anyOtherBenefits,
    AnyCompanyBenefitsId -> anyCompanyBenefits,
    AnyTaxableIncomeId -> otherTaxableIncome,
    HowMuchMedicalBenefitsId -> (_ => routes.AnyOtherTaxableIncomeController.onPageLoad(NormalMode)),
    AnyOtherTaxableIncomeId -> anyOtherTaxableIncome,
    WhereToSendPaymentId -> whereToSendPayment,
    NomineeFullNameId -> (_ => routes.AnyAgentRefController.onPageLoad(NormalMode)),
    IsPaymentAddressInTheUKId -> isPaymentAddressInUkRoute,
    PaymentUKAddressId -> (_ => routes.TelephoneNumberController.onPageLoad(NormalMode)),
    PaymentInternationalAddressId -> (_ => routes.TelephoneNumberController.onPageLoad(NormalMode)),
    TelephoneNumberId -> (_ => routes.WhereToSendPaymentController.onPageLoad(NormalMode)),
    OtherBenefitsNameId -> (_ => routes.HowMuchOtherBenefitController.onPageLoad(NormalMode)),
    HowMuchOtherBenefitId -> (_ => routes.AnyOtherBenefitsController.onPageLoad(NormalMode))
  )

  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map(

  )

  private def employmentDetails(userAnswers: UserAnswers): Call = userAnswers.employmentDetails match {
    case Some(true) => routes.AnyBenefitsController.onPageLoad(NormalMode)
    case Some(false) => routes.EnterPayeReferenceController.onPageLoad(NormalMode)
    case None => routes.SessionExpiredController.onPageLoad()
  }

  private def anyBenefits(userAnswers: UserAnswers): Call = userAnswers.anyBenefits match {
    case Some(true) => routes.SelectBenefitsController.onPageLoad(NormalMode)
    case Some(false) => routes.AnyCompanyBenefitsController.onPageLoad(NormalMode)
    case None => routes.SessionExpiredController.onPageLoad()
  }

  private def selectBenefits(userAnswers: UserAnswers): Call = userAnswers.selectBenefits match {
    case Some(benefits) =>
      benefits.head match {
        case Benefits.BEREAVEMENT_ALLOWANCE => routes.HowMuchBereavementAllowanceController.onPageLoad(NormalMode)
        case Benefits.CARERS_ALLOWANCE => routes.HowMuchCarersAllowanceController.onPageLoad(NormalMode)
        case Benefits.JOBSEEKERS_ALLOWANCE => routes.HowMuchJobseekersAllowanceController.onPageLoad(NormalMode)
        case Benefits.INCAPACITY_BENEFIT => routes.HowMuchIncapacityBenefitController.onPageLoad(NormalMode)
        case Benefits.EMPLOYMENT_AND_SUPPORT_ALLOWANCE => routes.HowMuchEmploymentAndSupportAllowanceController.onPageLoad(NormalMode)
        case Benefits.STATE_PENSION => routes.HowMuchStatePensionController.onPageLoad(NormalMode)
        case Benefits.OTHER_TAXABLE_BENEFIT => routes.OtherBenefitsNameController.onPageLoad(NormalMode)
      }
    case None =>
      routes.SessionExpiredController.onPageLoad()
  }

  private def benefitRouter(currentPageId: String)(userAnswers: UserAnswers): Call = userAnswers.selectBenefits match {
    case Some(benefits) =>
      val nextPageIndex: Int = (benefits.map(_.toString) indexOf currentPageId) + 1

      if (nextPageIndex < benefits.length) {
        benefits(nextPageIndex) match {
          case Benefits.CARERS_ALLOWANCE => routes.HowMuchCarersAllowanceController.onPageLoad(NormalMode)
          case Benefits.JOBSEEKERS_ALLOWANCE => routes.HowMuchJobseekersAllowanceController.onPageLoad(NormalMode)
          case Benefits.INCAPACITY_BENEFIT => routes.HowMuchIncapacityBenefitController.onPageLoad(NormalMode)
          case Benefits.EMPLOYMENT_AND_SUPPORT_ALLOWANCE => routes.HowMuchEmploymentAndSupportAllowanceController.onPageLoad(NormalMode)
          case Benefits.STATE_PENSION => routes.HowMuchStatePensionController.onPageLoad(NormalMode)
          case Benefits.OTHER_TAXABLE_BENEFIT => routes.OtherBenefitsNameController.onPageLoad(NormalMode)
        }
      } else {
        routes.AnyCompanyBenefitsController.onPageLoad(NormalMode)
      }
    case None =>
      routes.SessionExpiredController.onPageLoad()
  }

  private def anyCompanyBenefits(userAnswers: UserAnswers): Call = userAnswers.anyCompanyBenefits match {
    case Some(true) => routes.SelectCompanyBenefitsController.onPageLoad(NormalMode)
    case Some(false) => routes.AnyTaxableIncomeController.onPageLoad(NormalMode)
    case None => routes.SessionExpiredController.onPageLoad()
  }

  private def anyOtherBenefits(userAnswers: UserAnswers): Call = userAnswers.anyOtherBenefits match {
    case Some(true) => routes.OtherBenefitsNameController.onPageLoad(NormalMode)
    case Some(false) => routes.AnyCompanyBenefitsController.onPageLoad(NormalMode)
    case None => routes.SessionExpiredController.onPageLoad()
  }

  private def otherTaxableIncome(userAnswers: UserAnswers): Call = userAnswers.anyTaxableIncome match {
    case Some(true) => routes.SelectTaxableIncomeController.onPageLoad(NormalMode)
    case Some(false) => routes.TelephoneNumberController.onPageLoad(NormalMode)
    case None => routes.SessionExpiredController.onPageLoad()
  }

  private def anyOtherTaxableIncome(userAnswers: UserAnswers): Call = userAnswers.anyOtherTaxableIncome match {
    case Some(true) => ???
    case Some(false) => routes.WhereToSendPaymentController.onPageLoad(NormalMode)
    case None => routes.SessionExpiredController.onPageLoad()
  }

  private def whereToSendPayment(userAnswers: UserAnswers): Call = userAnswers.whereToSendPayment match {
    case Some(Nominee) => routes.NomineeFullNameController.onPageLoad(NormalMode)
    case Some(Myself) => routes.TelephoneNumberController.onPageLoad(NormalMode)
    case None => routes.SessionExpiredController.onPageLoad()
  }

  private def anyAgentRef(userAnswers: UserAnswers): Option[Call] = userAnswers.anyAgentRef map {
    case AnyAgentRef.Yes(agentRef) => ???
    case AnyAgentRef.No => routes.IsPaymentAddressInTheUKController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def isPaymentAddressInUkRoute(userAnswers: UserAnswers): Call = userAnswers.isPaymentAddressInTheUK match {
    case Some(true) => routes.PaymentUKAddressController.onPageLoad(NormalMode)
    case Some(false) => routes.PaymentInternationalAddressController.onPageLoad(NormalMode)
    case None => routes.SessionExpiredController.onPageLoad()
  }

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode =>
      routeMap.getOrElse(id, _ => routes.IndexController.onPageLoad())
    case CheckMode =>
      editRouteMap.getOrElse(id, _ => routes.CheckYourAnswersController.onPageLoad())
  }
}
