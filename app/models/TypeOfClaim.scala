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

package models

import utils.{WithName, Enumerable}

sealed trait TypeOfClaim

object TypeOfClaim extends Enumerable[TypeOfClaim] {

  case object OptionSA extends WithName("optionSA") with TypeOfClaim
  case object OptionPAYE extends WithName("optionPAYE") with TypeOfClaim

  lazy val values: Set[TypeOfClaim] = Set(
    OptionSA, OptionPAYE
  )
}