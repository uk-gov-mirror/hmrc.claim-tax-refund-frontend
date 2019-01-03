/*
 * Copyright 2019 HM Revenue & Customs
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

import org.scalatest.{MustMatchers, WordSpec}
import org.scalatest.mockito.MockitoSugar

class ReferenceGeneratorSpec extends WordSpec with MustMatchers with MockitoSugar {

  val referenceGenerator = new ReferenceGenerator

  "generateSubmissionNumber" must {
    "create reference in the correct format" in {
      val submissonNumber = referenceGenerator.generateSubmissionNumber
      val regex = """([A-Z0-9]{3})(-)([A-Z0-9]{4})(-)([A-Z0-9]{3})"""
      val regexMatchResult = submissonNumber matches regex
      regexMatchResult mustBe true
    }
  }
}
