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

package models

import identifiers._
import play.api.libs.json.Json
import utils.UserAnswers

case class Submission(selectTaxYear: SelectTaxYear)

object Submission {

  implicit val format = Json.format[Submission]

  def apply(answers: UserAnswers): Submission = {

    require(answers.selectTaxYear.isDefined, "Tax year was not answered")

    Submission(answers.selectTaxYear.get)
  }

  def asMap(e: Submission): Map[String, String] = {

    Map(
      SelectTaxYearId.toString -> e.selectTaxYear.toString
    )
  }
}