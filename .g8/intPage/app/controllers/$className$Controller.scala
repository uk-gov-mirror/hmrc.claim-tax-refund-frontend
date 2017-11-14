package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import connectors.DataCacheConnector
import controllers.actions._
import config.FrontendAppConfig
import forms.$className$Form
import identifiers.$className$Id
import models.Mode
import utils.{Navigator, UserAnswers}
import views.html.$className;format="decap"$

import scala.concurrent.Future

class $className;format="cap"$Controller @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.$className;format="decap"$ match {
        case None => $className$Form()
        case Some(value) => $className$Form().fill(value)
      }
      Ok($className;format="decap"$(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      $className$Form().bindFromRequest().fold(
        (formWithErrors: Form[Int]) =>
          Future.successful(BadRequest($className;format="decap"$(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[Int](request.sessionId, $className$Id.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage($className$Id, mode)(new UserAnswers(cacheMap))))
      )
  }
}
