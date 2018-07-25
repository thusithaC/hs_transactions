package controllers

import model.{DataAccessObj, Transaction}
import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current
import play.api.db._
import play.api.libs.json._
import model.Transaction._



object Application extends Controller {

  def index = Action {
    Ok(views.html.index(null))
  }

  def tick = Action {
    var out = ""
    val conn = DB.getConnection("transactions")
    try {
      val stmt = conn.createStatement

      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())")

      val rs = stmt.executeQuery("SELECT tick FROM ticks")

      while (rs.next) {
        out += "Read from DB: " + rs.getTimestamp("tick") + "\n"
      }
    } finally {
      conn.close()
    }
    println(out)
    Ok(out)
  }

  def transactions = Action {
    var out = ""
    val conn = DB.getConnection("transactions")
    try {
      val stmt = conn.createStatement

      val rs = stmt.executeQuery("SELECT transactionId, transactionDay, transactionAmount FROM trans_details")

      while (rs.next) {
        out += "Read from DB: " + rs.getString("transactionId") + " " + rs.getString("transactionDay") + " " + rs.getInt("transactionAmount")  + "\n"
      }
    } finally {
      conn.close()
    }
    Ok(out)
  }

  def transactions_new = Action {
    var transactions = DataAccessObj.getAllTransactions()
    Ok(Json.toJson(transactions))
  }





}
