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

package utils

import base.SpecBase
import models.FullOrPartialClaim.{OptionAll, OptionSome}
import models.SelectTaxYear._
import models.TypeOfClaim.{OptionPAYE, OptionSA}
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import models.{InternationalAddress, UkAddress}

class CheckYourAnswersHelperSpec extends SpecBase with MockitoSugar {

  private var answers = mock[UserAnswers]

  "Full Name" must {
    s"have the correct label" in {
      when(answers.fullName) thenReturn Some("value")
      val helper = new CheckYourAnswersHelper(answers)
      helper.fullName.get.label mustBe s"fullName.checkYourAnswersLabel"
    }
  }

  "National Insurance Number" must {
    s"have the correct label" in {
      when(answers.nationalInsuranceNumber) thenReturn Some("value")
      val helper = new CheckYourAnswersHelper(answers)
      helper.nationalInsuranceNumber.get.label mustBe s"nationalInsuranceNumber.checkYourAnswersLabel"
    }
  }


  "Is the address in the UK (true)" must {
    s"have the correct label" in {
      when(answers.isTheAddressInTheUK) thenReturn Some(true)
      val helper = new CheckYourAnswersHelper(answers)
      helper.isTheAddressInTheUK.get.label mustBe s"isTheAddressInTheUK.checkYourAnswersLabel"
    }
  }

  "Is the address in the UK (false)" must {
    s"have the correct label" in {
      when(answers.isTheAddressInTheUK) thenReturn Some(false)
      val helper = new CheckYourAnswersHelper(answers)
      helper.isTheAddressInTheUK.get.label mustBe s"isTheAddressInTheUK.checkYourAnswersLabel"
    }
  }


  "UK Address" must {
    s"have correct label" in {
      when(answers.ukAddress) thenReturn Some (UkAddress("line 1", "line 2", None, None, None, "AA11 1AA"))
      val helper = new CheckYourAnswersHelper(answers)
      helper.ukAddress.get.label mustBe s"ukAddress.checkYourAnswersLabel"
    }
  }

  "International Address" must {
    s"have correct label" in {
      when(answers.internationalAddress) thenReturn Some(InternationalAddress("line 1", "line 2", None, None, None, "country"))
      val helper = new CheckYourAnswersHelper(answers)
      helper.internationalAddress.get.label mustBe s"internationalAddress.checkYourAnswersLabel"
    }
  }

  "Telephone number" must {
    s"have the correct label" in {
      when(answers.telephoneNumber) thenReturn Some("01912134587")
      val helper = new CheckYourAnswersHelper(answers)
      helper.telephoneNumber.get.label mustBe s"telephoneNumber.checkYourAnswersLabel"
    }
  }

  "UTR Number" must {
    s"have the correct label" in {
      when(answers.uniqueTaxpayerReference) thenReturn Some("1234567890")
      val helper = new CheckYourAnswersHelper (answers)
      helper.uniqueTaxpayerReference.get.label mustBe s"uniqueTaxpayerReference.checkYourAnswersLabel"
    }
  }

  "PAYE Reference" must {
    s"have the correct label" in {
      when(answers.payAsYouEarn) thenReturn Some("ABC/123456")
      val helper = new CheckYourAnswersHelper (answers)
      helper.payAsYouEarn.get.label mustBe s"payAsYouEarn.checkYourAnswersLabel"
    }
  }

  "Select tax year (Option 1)" must {
    s"have correct label" in {
      when(answers.selectTaxYear) thenReturn Some(Option1)
      val helper = new CheckYourAnswersHelper (answers)
      helper.selectTaxYear.get.label mustBe s"selectTaxYear.checkYourAnswersLabel"
     }
  }

  "Select tax year (Option 2)" must {
    s"have correct label" in {
      when(answers.selectTaxYear) thenReturn Some(Option2)
      val helper = new CheckYourAnswersHelper (answers)
      helper.selectTaxYear.get.label mustBe s"selectTaxYear.checkYourAnswersLabel"
    }
  }

  "Select tax year (Option 3)" must {
    s"have correct label" in {
      when(answers.selectTaxYear) thenReturn Some(Option3)
      val helper = new CheckYourAnswersHelper (answers)
      helper.selectTaxYear.get.label mustBe s"selectTaxYear.checkYourAnswersLabel"
    }
  }

  "Select tax year (Option 4)" must {
    s"have correct label" in {
      when(answers.selectTaxYear) thenReturn Some(Option4)
      val helper = new CheckYourAnswersHelper (answers)
      helper.selectTaxYear.get.label mustBe s"selectTaxYear.checkYourAnswersLabel"
    }
  }

  "Is any benefits (true)" must {
    s"have the correct label" in {
      when(answers.anyBenefits) thenReturn Some(true)
      val helper = new CheckYourAnswersHelper(answers)
      helper.anyBenefits.get.label mustBe s"anyBenefits.checkYourAnswersLabel"
    }
  }

  "Is any benefits (false)" must {
    s"have the correct label" in {
      when(answers.anyBenefits) thenReturn Some(false)
      val helper = new CheckYourAnswersHelper(answers)
      helper.anyBenefits.get.label mustBe s"anyBenefits.checkYourAnswersLabel"
    }
  }

  "Is type of claim (SA)" must {
    s"have the correct label" in {
      when(answers.typeOfClaim) thenReturn Some(OptionSA)
      val helper = new CheckYourAnswersHelper(answers)
      helper.typeOfClaim.get.label mustBe s"typeOfClaim.checkYourAnswersLabel"
    }
  }

  "Is type of claim (PAYE)" must {
    s"have the correct label" in {
      when(answers.typeOfClaim) thenReturn Some(OptionPAYE)
      val helper = new CheckYourAnswersHelper(answers)
      helper.typeOfClaim.get.label mustBe s"typeOfClaim.checkYourAnswersLabel"
    }
  }

    "Is fullOrPartial (Some)" must {
      s"have the correct label" in {
        when(answers.fullOrPartialClaim) thenReturn Some(OptionSome)
        val helper = new CheckYourAnswersHelper(answers)
        helper.fullOrPartialClaim.get.label mustBe s"fullOrPartialClaim.checkYourAnswersLabel"
      }
    }

  "Is fullOrPartial (All)" must {
    s"have the correct label" in {
      when(answers.fullOrPartialClaim) thenReturn Some(OptionAll)
      val helper = new CheckYourAnswersHelper(answers)
      helper.fullOrPartialClaim.get.label mustBe s"fullOrPartialClaim.checkYourAnswersLabel"
    }
  }

  "Is other income (true)" must {
    s"have the correct label" in {
      when(answers.otherIncome) thenReturn Some(true)
      val helper = new CheckYourAnswersHelper(answers)
      helper.otherIncome.get.label mustBe s"otherIncome.checkYourAnswersLabel"
    }
  }

  "Is other income (false)" must {
    s"have the correct label" in {
      when(answers.otherIncome) thenReturn Some(false)
      val helper = new CheckYourAnswersHelper(answers)
      helper.otherIncome.get.label mustBe s"otherIncome.checkYourAnswersLabel"
    }
  }
}