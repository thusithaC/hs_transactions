package model

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by thusitha on 7/25/18.
  */
case class Transaction(transactionId : String,
                       accountId : String,
                       transactionDay : Int,
                       category: String,
                       transactionAmount:BigDecimal)

object Transaction {
  implicit val transactionReads: Reads[Transaction] = (
    (JsPath \ "transactionId").read[String] and
      (JsPath \ "accountId").read[String] and
      (JsPath \ "transactionDay").read[Int] and
      (JsPath \ "category").read[String] and
      (JsPath \ "transactionAmount").read[BigDecimal]
    )(Transaction.apply _)

  implicit val transactionWrites: Writes[Transaction] = (

    (JsPath \ "transactionId").write[String] and
      (JsPath \ "accountId").write[String]and
      (JsPath \ "transactionDay").write[Int] and
      (JsPath \ "category").write[String] and
      (JsPath \ "transactionAmount").write[BigDecimal]
    )(unlift(Transaction.unapply))

}


