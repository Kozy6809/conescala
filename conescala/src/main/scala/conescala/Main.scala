
package conescala

import java.text.DecimalFormat
import scala.swing._
/**
 * コーンプレート粘度計測定プログラム。測定周期1秒版
 * 任意の測定スクリプトを走らせる場合は、以下の書式でコマンドを入力する
 * cd c:\home
 * java -jar haake.jar <スクリプト>
 */
object Main {
  private lazy val s = Script("c:/home/haake.scr")
  private lazy val pk2scr = Script("c:/home/pk2.scr")
  private lazy val pq5scr = Script("c:/home/pq5.scr")
  private lazy val scripts = Array(pk2scr, pq5scr, s)
  private lazy val sv = new StartView()
  private var spec = 0 // 測定方法種別
  private var flying = false // 温度チェックをスキップするか

  private val c = new Controller(new Transceiver("COM3")) // TODO magic number
  //private val c = new ControllerMock

  def main(args: Array[String]) {
    println("new version 2014/12/8")
    println("new version params: " + args.toList)
    if (args.isEmpty) {
      sv.pack()
      sv.setVisible(true)
      scripts // evaluate lazy value after GUI initialization
    } else {
      startMeasure(Script(args(0)))
    }
  }

  /**
   * 測定開始
   */
  def startMeasure(spec: Int) {
    this.spec = spec
    flying = false
    startMeasure(scripts(spec))
  }
  
  def flyingMeasure(spec: Int) {
    this.spec = spec
    flying = true
    startMeasure(scripts(spec))
  }

  private def startMeasure(s: Script) {
    val m = new Measure(s, c, flying)
    m.execute()
  }

  /**
   * 測定終了時に呼ばれるメソッド
   */
  def doneMeasure(m: Measure, res: List[List[Double]]){
    spec match {
      case 0 => m setResult calcPK2vis(res)
      case 1 => m setResult calcPQ5vis(res)
      case _ =>
    }
    println("done!")
  }

  import scala.math._
  private def eta(row: List[Double]) = row(2) // etaはListの3番目の値
  private def maxRow(r: List[List[Double]]) = // eta最大の行を取得
    r.foldLeft(0.0)((b,a) => max(b, eta(a)))
  private val df = new DecimalFormat("#.000")

  private def calcPK2vis(res: List[List[Double]]) = {
    val eta10 = eta(res(2)) // 3番目(3秒後)の値  1秒周期版の変更点
    val eta1000 = maxRow(res.filter(_.head > 900)) // 周速 > 900
    println("eta10= " + eta10)
    println("eta1000= " + eta1000)
    val p = 5.64019 * log(eta10) + 9.95384 // 計算式確認済み
    val u = 6.72913 + 0.948829 * eta1000 * eta1000 // 計算式確認済み
    List("Eta(d=10)   "+ df.format(eta10) +"   P値相当値 " + df.format(p),
         "Eta(d=1000) "+ df.format(eta1000) +"   U値相当値 " + df.format(u))
  }
  private def calcPQ5vis(res: List[List[Double]]) = {
    val etaMax = maxRow(res)
    List("Eta(d=2000) " + df.format(etaMax), "")
  }
  /**
   * 測定でエラー発生時に呼ばれるメソッド
   */
  def errored() {

  }

  def quit() {
    println("quit")
    System.exit(0)
  }

}
