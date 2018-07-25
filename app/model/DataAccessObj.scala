package model

import java.sql.ResultSet

import play.api.db._
import play.api.libs.json._
import play.api.Play.current


import scala.annotation.tailrec

/**
  * Created by thusitha on 7/25/18.
  */
object DataAccessObj {
  def getTransactionsForAccount(account: String) : List[Transaction] = {
    Nil
  }


  def getTransactionsForId(transactionId: String) : Transaction = {
    null
  }

  def getTransactionsForAccountDateRange(account: String, startDate:Int, endDate:Int) : Transaction = {
    null
  }

  def insertTransactions(transactionId : String,
                         accountId : String,
                         transactionDay : Int,
                         category: String,
                         transactionAmount:BigDecimal) {
    //insert
  }

  def insertTransactionJson(json:JsValue) = {
    json.validate[Transaction] match {
      case c: JsSuccess[Transaction] => {
        val charge: Transaction = c.get
        //log success
      }
      case e: JsError => {
      // log error
      }
    }
  }


  def getAllTransactions(): List[Transaction] = {

    def createTransaction(rs:ResultSet) : Transaction = {
      Transaction(rs.getString("transactionId"), rs.getString("accountId"), rs.getInt("transactionDay"), rs.getString("category"), rs.getBigDecimal("transactionAmount"))
    }

    @tailrec
    def getItemsFromResult(tList:List[Transaction], rs: ResultSet) : List[Transaction] = {
      val hasNext = rs.next()
      if (!hasNext) tList else getItemsFromResult(createTransaction(rs)::tList, rs)
    }

    val conn = DB.getConnection("transactions")

    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT transactionId, accountId, transactionDay, category, transactionAmount FROM trans_details")
      getItemsFromResult(Nil, rs)
    } finally {
      conn.close()
    }
  }







}
