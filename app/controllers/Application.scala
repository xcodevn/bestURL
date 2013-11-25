package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

import views._

import models._
import anorm._

case class FormData (id: Option[String], ref: String)

object Application extends Controller {
  val submitForm: Form[FormData] = Form(
    // Defines a mapping that will handle Contact values
    mapping(
      "id" -> optional(text),
      "ref" -> nonEmptyText
    )(FormData.apply)(FormData.unapply)
  )

  def index = Action {
    Ok(views.html.index(submitForm))
  }

  def getID(id: Option[String]): String = id match {
    case None => {
      val random = new scala.util.Random
      def randomString(alphabet: String)(n: Int): String = 
        Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(n).mkString
      def randomAlphanumericString(n: Int) = 
        randomString("abcdefghijklmnopqrstuvwxyz0123456789")(n)

      var rl = randomAlphanumericString(5)
      while (URL.findById(rl) != None) rl = randomAlphanumericString(5)
      rl
    }
    case Some(idt) => idt
  }

  def normalizeURL(url:String): String = {
    if (url.contains("://") == false) { "http://"+url } else url
  }

  def addURL(data: FormData) = {
    val id = getID(data.id)
    val rl = URL.findById(id)
    if (rl != None) {
      var count = 0
      var s = ""
      var i = id.length
      while (i >= 1 && id(i-1) >= '0' && id(i-1) <='9') {s = id(i-1) + s; i = i - 1;}
      if (s.length > 0) count = s.toInt
      val newid = id.substring(0, i)

      while (URL.findById(newid + count.toString) != None) count = count + 1
      Ok(views.html.exist(id, data.ref, newid + count.toString ))
    } else {
      URL.insert(URL(Id(id), data.ref))
      Ok(views.html.newurl(id, data.ref))
    }
  }

  /**
   * Handle form submission.
   */
  def submit = Action { implicit request =>
    submitForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(errors)),
      url => addURL(url)
    )
  }

  def error = Action {
    Ok(views.html.error())
  }

  def about = Action {
    Ok(views.html.about())
  }

  def contact = Action {
    Ok(views.html.contact())
  }
  def query(id: String) = Action {
    URL.findById(id) match {
      case None => Ok(views.html.error())
      case Some(url) => Redirect(normalizeURL(url.ref))
    }
  }

  
}
