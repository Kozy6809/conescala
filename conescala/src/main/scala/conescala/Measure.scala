/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package conescala

import java.text.DecimalFormat
import java.util.Timer
import java.util.TimerTask
import javax.swing.JTextArea
import javax.swing.SwingWorker
import scala.collection.mutable.Buffer
import java.io.IOException
import java.lang.System.{currentTimeMillis => stime}

class SerialException(s:String) extends java.lang.Exception(s:String)

/**
 * 測定を実施するクラス。途中経過をUIに表示できる
 *
 * @author 涌井
 */
class Measure(s: Script, c: Controller, flying: Boolean) extends
SwingWorker[Unit, List[Double]] {
  private val mv = new MeasureView()
  private var t0 = 0L // 時刻の起点(ミリ秒)
  private var f = 0.0 // システムファクター
  private var m = 0.0 // システムファクター
  private val it = s.iterator
  private var res = List.empty[List[Double]] // 測定結果
  private var error = false
  private val ta = mv.getProcessTA
  private var tempReady = true // 温度が範囲内であることを示す
  private var settingShear = false // 剪断速度の設定中を示す

  //mv.setLocation(420,0)
  mv.setVisible(true)

  def doInBackground() = {
    try {
      start()
    }catch {
      case e:SerialException => {
          ta.append("\ncommunication error in background: " + e)
          processError()
        }
      case e:Exception => {
          e.printStackTrace
          processError()
        }
    }
  }

  private def processError() {
    error = true
    Ex.cancel()
    c.stopMotor()
    Main.errored
  }

  /**
   * 測定を開始する
   */
  private def start() {
    c.takeControl()
    t0 = stime
    c.resetTimer()
    val tim = new Timer
    tim.scheduleAtFixedRate(Ex, 0, 1000) // 1秒周期版の変更点
    for (cmd <- it) {
      if (!error) {
        cmd match {
          case sp: SysParam => f = sp.f; m = sp.m
          case wt: WaitTemp => if (!flying) waitTemp(wt.temp, wt.width)
          case ss: SetShear => setShear(ss.t, ss.shear)
        }
      }
    }
    Ex.cancel()
  //  done()
  }

  /**
   * 測定開始時に、温度が一定範囲に入るまで待つ
   */
  private def waitTemp(temp:Double, width:Double) {
  	synchronized {
  		tempReady = false
  				var tmp = c.readTemp
  				while ((tmp - temp).abs > width) {
  					mv.setStatus("温度が範囲外です:" + tmp + "度")
  					Thread.sleep(1000)
  					tmp = c.readTemp
  				}
  		mv.setStatus("測定を開始します")
  		t0 = stime
  		println("well tempered at " + tmp); System.out.flush
  		c.resetTimer()
  		res = res take 0 // 結果リストをクリア
  		tempReady = true
  	}
  }

  /**
   * 剪断速度を設定する。まだ設定時刻になっていない場合、
   * スレッドはブロックする
   * @param t 時刻(秒)
   * @param shear 剪断速度(1/s)
   */
  private def setShear(t:Int, shear:Double) {
    val delta = t * 1000 - (stime - t0)
    if (delta > 0) Thread.sleep(delta)
    val rads = shear / m / 60 * 2 * math.Pi // shear(1/s) / m(min/s) = rpm(1/min)
    synchronized {
      settingShear = true
    	c.setAnglVel(rads)
    	settingShear = false
    }
  }


  /**
   * 温度が範囲内であれば、測定値を読みとり結果リストに入れる。温度をチェックするのは通信の排他制御のため
   */
  private object Ex extends TimerTask {
    override def run() {
      try {
        if (tempReady && !settingShear) {
          val gp = c.readAnglVel * 60 / (2 * math.Pi) * m
          val tau = c.readTorque * f * 100 // (Nm) * (Pa/Ncm) *100
          val eta = tau / gp
          val t = c.readTime
          val tmp = c.readTemp
          val l = List(gp, tau, eta, t, tmp)
          publish(l)
          res = l :: res
        }
      }catch{
        case e:SerialException => {
            ta.append("\ncommunication error in timertask: " + e )
            processError()
          }
        case e:Exception => {
            e.printStackTrace
            processError()
          }
      }
    }
  }

  override def process(l: java.util.List[List[Double]]) {
    // 測定値を整形してTextAreaに追加
    val df = new DecimalFormat("#.000")
    import scala.collection.JavaConversions._
    l.foreach(line => ta.append(line.map(df.format(_)).mkString("","\t","\n")))
//    val b: Buffer[List[Double]] = l
//    for (t <- b) {
//      ta.append(t.foldLeft("")((s, d) => s + df.format(d) +"\t") + "\n")
//    }
  }

  override def done() {
    mv.setStatus("測定が終了しました")
    res = res.reverse
    Main.doneMeasure(this, res)
    mv.canDispose = true
  }

  def setResult(r: List[String]) {
    mv setResult1 r.head
    mv setResult2 r.last
  }
}
