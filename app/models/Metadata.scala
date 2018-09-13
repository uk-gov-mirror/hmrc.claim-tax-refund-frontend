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

import org.joda.time.LocalDateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.util.Random
import scala.xml._
import scala.xml.Utility._

case class Metadata(customerId: String = "", hmrcReceivedAt: LocalDateTime = LocalDateTime.now, xmlCreatedAt: LocalDateTime = LocalDateTime.now) {

	def generateSubmissionNumber: String = {
		val charList = ('A' to 'Z') ++ ('0' to '9')
		val randomGen = new Random
		val dmsSubmission = new StringBuilder
		for (count <- 1 to 10) {
			if (count == 4 || count == 8) {
				dmsSubmission.append("-")
			}
			dmsSubmission.append(charList(randomGen.nextInt(charList.length)))
		}
		dmsSubmission
	}

	val timeStamp: String = xmlCreatedAt.toString("ssMMyyddmmHH")
	val fileFormat: String = "pdf"
	val mimeType: String = "application/pdf"

	val casKey: String = ""
	val submissionMark: String = ""
	val submissionReference: String = generateSubmissionNumber
	val reconciliationId: String = timeStamp
	val attachmentCount: Int = 0
	val numberOfPages: Int = 2

	val formId: String = "R39_EN"
	val businessArea: String = "PSA"
	val classificationType: String = "PSA-DFS Repayments"
	val source: String = "R39_EN"
	val target: String = "DMS"
	val store: Boolean = true
	val robotXml: Boolean = true
}

object Metadata {

	def toXml(metadata: Metadata): NodeSeq = {
		val metadataXml: Elem = {
			<documents>
				<document>
					<header>
						<title>{metadata.submissionReference}</title>
						<format>{metadata.fileFormat}</format>
						<mime_type>{metadata.mimeType}</mime_type>
						<store>{metadata.store}</store>
						<source>{metadata.source}</source>
						<target>{metadata.target}</target>
						<reconciliation_id>{metadata.reconciliationId}</reconciliation_id>
					</header>
					<metadata>
						{attributeXml("hmrc_time_of_receipt", "time", metadata.hmrcReceivedAt.toString("dd/MM/yyyy HH:mm:ss"))}
						{attributeXml("time_xml_created", "time", metadata.hmrcReceivedAt.toString("dd/MM/yyyy HH:mm:ss"))}
						{attributeXml("submission_reference", "string", metadata.submissionReference)}
						{attributeXml("form_id", "string", metadata.formId)}
						{attributeXml("number_pages", "integer", metadata.numberOfPages.toString)}
						{attributeXml("source", "string", metadata.source)}
						{attributeXml("customer_id", "string", metadata.customerId)}
						{attributeXml("submission_mark", "string", metadata.submissionMark)}
						{attributeXml("cas_key", "string", metadata.casKey)}
						{attributeXml("classification_type", "string", metadata.classificationType)}
						{attributeXml("business_area", "string", metadata.businessArea)}
						{attributeXml("attachment_count", "integer", metadata.attachmentCount.toString)}
					</metadata>
				</document>
			</documents>
		}

		trim(metadataXml)
	}

	def attributeXml(attributeName: String, attributeType: String, attributeValue: String): NodeSeq = {
		<attribute>
			<attribute_name>{attributeName}</attribute_name>
			<attribute_type>{attributeType}</attribute_type>
			<attribute_values>
				<attribute_value>{attributeValue}</attribute_value>
			</attribute_values>
		</attribute>
	}

	implicit val writes: Writes[Metadata] =
		Writes {
			metadata =>
				Json.obj(
					"customerId" -> metadata.customerId,
					"hmrcReceivedAt" -> metadata.hmrcReceivedAt.toString,
					"xmlCreatedAt" -> metadata.xmlCreatedAt.toString,
					"submissionReference" -> metadata.submissionReference,
					"reconciliationId" -> metadata.reconciliationId,
					"fileFormat" -> metadata.fileFormat,
					"mimeType" -> metadata.mimeType,
					"casKey" -> metadata.casKey,
					"submissionMark" -> metadata.submissionMark,
					"attachmentCount" -> metadata.attachmentCount,
					"numberOfPages" -> metadata.numberOfPages,
					"formId" -> metadata.formId,
					"businessArea" -> metadata.businessArea,
					"classificationType" -> metadata.classificationType,
					"source" -> metadata.source,
					"target" -> metadata.target,
					"store" -> metadata.store,
					"robotXml" -> metadata.robotXml
				)
		}
	implicit def reads: Reads[Metadata] = (
		(__ \ "customerId").read[String] and
			(__ \ "hmrcReceivedAt").read[LocalDateTime](jodaDateReads) and
			(__ \ "xmlCreatedAt").read[LocalDateTime](jodaDateReads)
		) (apply _)

	val jodaDateReads: Reads[LocalDateTime] = Reads[LocalDateTime](js =>
		js.validate[String].map[LocalDateTime](dtString =>
			LocalDateTime.parse(dtString)
		)
	)
}
