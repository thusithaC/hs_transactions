package model

import java.sql.{ResultSet, SQLException}

import play.api.Logger
import play.api.db._
import play.api.libs.json._
import play.api.Play.current

import scala.annotation.tailrec

/**
  * Created by thusitha on 7/25/18.
  */
object DataAccessObj {

  def getTransactionsForAccount(account: String) : List[Transaction] = {
    val query = s"SELECT transactionId, accountId, transactionDay, category, transactionAmount FROM trans_details WHERE accountId=\'${account}\'"
    getItemsFromDatsource(query)
  }


  def getTransactionsForId(transactionId: String) : Transaction = {
    val query = s"SELECT transactionId, accountId, transactionDay, category, transactionAmount FROM trans_details WHERE transactionId=\'$transactionId\'"
    getItemFromDatsource(query)
  }

  def getTransactionsForAccountDateRange(account: String, startDate:Int, endDate:Int) : List[Transaction] = {
    val query = s"SELECT transactionId, accountId, transactionDay, category, transactionAmount FROM trans_details WHERE accountId=\'${account}\' AND  transactionDay BETWEEN ${startDate} AND ${endDate}"
    getItemsFromDatsource(query)
  }

  def getAllTransactions(): List[Transaction] = {
    val query = "SELECT transactionId, accountId, transactionDay, category, transactionAmount FROM trans_details"
    getItemsFromDatsource(query)
  }

  def getAllAccounts() : List[String] = {

    def getaccountsFromResult(accList:List[String], rs: ResultSet) : List[String] = {
      val hasNext = rs.next()
      if (!hasNext) accList else getaccountsFromResult(rs.getString("accountId")::accList, rs)
    }

    val query = "SELECT distinct accountId FROM trans_details ORDER BY accountId"

    val conn = DB.getConnection("transactions")
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(query)
      getaccountsFromResult(Nil, rs)
    } finally {
      conn.close()
    }
  }

  def insertTransactionJson(json:JsValue) : Boolean= {
    json.validate[Transaction] match {
      case c: JsSuccess[Transaction] => {
        val transaction: Transaction = c.get
        val rowsAffected = insertTranactionIntoDb(transaction)
        if (rowsAffected > 0){
          Logger.info("successfully entered transaction " + transaction )
          true
        }
        else {
          Logger.error("Database Error couldnt enter transaction " + transaction)
          false
        }
      }
      case e: JsError => {
        Logger.error("Error parsing transaction " +  json)
        false
      }
    }
  }

  def insertTranactionIntoDb(transaction: Transaction) : Int = {
    val conn = DB.getConnection("transactions")
    try {
      val stmt = conn.createStatement
      val insertString = s"INSERT INTO trans_details (transactionId, accountId, transactionDay, category, transactionAmount) VALUES (\'${transaction.transactionId}\', \'${transaction.accountId}\', ${transaction.transactionDay}, \'${transaction.category}\', ${transaction.transactionAmount})"
      println(insertString)
      stmt.executeUpdate(insertString)
    } finally {
      conn.close()
    }
  }


  private def createTransaction(rs:ResultSet) : Transaction = {
    Transaction(rs.getString("transactionId"), rs.getString("accountId"), rs.getInt("transactionDay"), rs.getString("category"), rs.getBigDecimal("transactionAmount"))
  }

  @tailrec
  private def getItemsFromResult(tList:List[Transaction], rs: ResultSet) : List[Transaction] = {
    val hasNext = rs.next()
    if (!hasNext) tList else getItemsFromResult(createTransaction(rs)::tList, rs)
  }

  private def getItemFromResult(rs: ResultSet) : Transaction = {
    if (rs.first()) createTransaction(rs) else null
  }


  private def getItemsFromDatsource(query: String) : List[Transaction] = {
    val conn = DB.getConnection("transactions")
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(query)
      getItemsFromResult(Nil, rs)
    } finally {
      conn.close()
    }
  }

  private def getItemFromDatsource(query: String) : Transaction = {
    val conn = DB.getConnection("transactions")
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(query)
      getItemFromResult(rs)
    } finally {
      conn.close()
    }
  }








}
