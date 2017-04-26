/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package conescala

import scala.collection.mutable.ListBuffer
import scala.io.Source

class Command
case class SysParam(f:Double, m: Double) extends Command {
  override def toString = "システムパラメータ " + f +" "+ m
}
case class WaitTemp(temp:Double, width:Double) extends Command {
  override def toString = "恒温待ち " + temp +" "+ width
}
case class SetShear(t:Int, shear:Double) extends Command {
  override def toString = "速度設定 " + t +" "+ shear
}

/**
 * 測定スクリプトクラス<p>
 * 以下の機能をサポートする<p>
 * ・コマンドの読み出し
 *
 * @author 涌井
 */
class Script (
  private val s: List[Command] ){
  def iterator = s.iterator
  override def toString = s.map(_.toString +"\n") toString
}

/**
 * スクリプトオブジェクト。ファイル名を受け取りScriptクラスを作成する
 */
object Script {
  /**
   * ファイル名からScriptクラスを作成する。スクリプトの正確な文法は実装されておらず、
   * 例えばシステムパラメータ行が複数回出現してもそのままスクリプトに格納する
   * @param filename ファイル名
   * @return Scriptクラス
   */
  def apply(filename: String) = {
    val s = Source.fromFile(filename)
    val lb = new ListBuffer[Command]
    try {
      for (line <- s.getLines if !(line startsWith "#")) { // #で始まる行はコメント
        line.split("\\s").toList match {
          case List("sp", f, m) => lb += SysParam(f.toDouble, m.toDouble)
          case List("wt", temp, width) => lb += WaitTemp(temp.toDouble, width.toDouble)
          case List(t, shear) => lb += SetShear(t.toInt, shear.toDouble)
          case _ =>
        }
      }
    } finally s.close
    new Script(lb.toList)
  }
}
