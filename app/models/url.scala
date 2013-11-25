package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

case class URL(id: Pk[String], ref: String)

object URL {
  
  // -- Parsers
  
  /**
   * Parse a Computer from a ResultSet
   */
  val simple = {
    get[Pk[String]]("url.id") ~
    get[String]("url.ref") map {
      case id~ref => URL(id, ref)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a linf from the id.
   */
  def findById(id: String): Option[URL] = {
    DB.withConnection { implicit connection =>
      SQL("select * from url where id = {id}").on('id -> id).as(URL.simple.singleOpt)
    }
  }
  
  /**
   * Insert a new computer.
   *
   * @param computer The computer values.
   */
  def insert(url: URL) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into url values ( {id}, {ref} )
        """
      ).on(
        'id -> url.id,
        'ref -> url.ref
      ).executeUpdate()
    }
  }
}

