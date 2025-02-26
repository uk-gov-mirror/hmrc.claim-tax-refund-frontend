/*
 * Copyright 2021 HM Revenue & Customs
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

package repositories

import javax.inject.{Inject, Singleton}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.libs.json.{JsValue, Json}
import play.api.{Configuration, Logging}
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.DefaultDB
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class DatedCacheMap(id: String,
                         data: Map[String, JsValue],
                         lastUpdated: DateTime = DateTime.now(DateTimeZone.UTC))

object DatedCacheMap {
  implicit val dateFormat = ReactiveMongoFormats.dateTimeFormats
  implicit val formats = Json.format[DatedCacheMap]

  def apply(cacheMap: CacheMap): DatedCacheMap = DatedCacheMap(cacheMap.id, cacheMap.data)
}

class ReactiveMongoRepository(config: Configuration, mongo: () => DefaultDB)
  extends ReactiveRepository[DatedCacheMap, BSONObjectID](config.get[String]("appName"), mongo, DatedCacheMap.formats) {

  val timeToLiveInSeconds: Int = config.underlying.getInt("mongodb.timeToLiveInSeconds")

  val ttlIndex = Index(
    key  = Seq(
      "lastUpdated" -> IndexType.Ascending
    ),
    name = Some("userAnswersExpiry"),
    options = BSONDocument("expireAfterSeconds" -> timeToLiveInSeconds)
  )

  val idIndex = Index(
    key = Seq(
      "id" -> IndexType.Ascending
    ),
    name = Some("userAnswersId")
  )

  collection.indexesManager.ensure(ttlIndex).map {
    result => {
      logger.debug(s"set [userAnswersExpiry] with value $timeToLiveInSeconds -> result : $result")
      result
    }
  }.recover {
    case e =>
      logger.error("Failed to set TTL index", e)
      false
  }.flatMap {
    _ =>
      collection.indexesManager.ensure(idIndex)
  }

  collection.indexesManager.ensure(Index(Seq()))

  def upsert(cm: CacheMap): Future[Boolean] = {
    val selector = Json.obj("id" -> cm.id)
    val cmDocument = Json.toJson(DatedCacheMap(cm))
    val modifier = Json.obj("$set" -> cmDocument)

    collection.update(ordered = false).one(selector, modifier, upsert = true).map {
      lastError =>
        lastError.ok
    }
  }

  def get(id: String): Future[Option[CacheMap]] = {
    collection.find(Json.obj("id" -> id), None).one[CacheMap]
  }
}

@Singleton
class SessionRepository @Inject()(config: Configuration, mongo: ReactiveMongoComponent) {

  private lazy val sessionRepository = new ReactiveMongoRepository(config, mongo.mongoConnector.db)

  def apply(): ReactiveMongoRepository = sessionRepository
}
