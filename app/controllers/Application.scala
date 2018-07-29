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

  def transactions = Action {
    Logger.info("Request received: transactions ")
    val transactions = DataAccessObj.getAllTransactions()
    if(!transactions.isEmpty)
      Ok(Json.toJson(transactions))
    else {
      Logger.error("No result found")
      NotFound
    }
  }

  def transactionByAccount(accountId : String) = Action {
    Logger.info("Request received: transactionByAccount " + accountId)
    if (null != accountId) {
      val transactions = DataAccessObj.getTransactionsForAccount(accountId)
      Ok(Json.toJson(transactions))
    }
    else {
      Logger.error("No result found")
      NotFound
    }
  }

  def allAccounts() = Action {
    Logger.info("Request received: allAccounts ")
    val accounts = DataAccessObj.getAllAccounts()
    if(!accounts.isEmpty) Ok(Json.toJson(accounts))
    else{
      Logger.error("No result found")
      NotFound
    }
  }


  def transactionById(transactionId : String) = Action {
    Logger.info("Request received: transactionById " + transactionId)
    if (null != transactionId) {
      val transaction = DataAccessObj.getTransactionsForId(transactionId)
      if (null != transaction) Ok(Json.toJson(transaction)) else NotFound
    }
    else {
      Logger.error("No result found")
      NotFound
    }
  }

  def createNew = Action { request =>
    Logger.info("Request to create object received + " + request.body.asText)
    val json = request.body.asJson.get
    if (DataAccessObj.insertTransactionJson(json)) Ok
    else {
      Logger.error("Bad request")
      BadRequest
    }
  }

}
