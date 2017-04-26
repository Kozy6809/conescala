package conescala

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * 粘度計を制御するクラス<p>
 * このクラスの各メソッドは粘度計がエラーメッセージを返した時に例外を投げる(
 * IOExceptionではない)。従ってこの例外の処理としてはエラーメッセージに応じたコマンドの
 * 再送やモーター停止などが適切である。
 * @author 涌井
 */
class Controller(private val t:Transceiver) {

  def quit() {
    t.logclose()
    t.terminate()
  }
    
  /**
   * 送信コマンドに対し受信データが不正だった場合に呼び出される
   * @param e 受信文字列
   */
  private def invokeErr(e:String) {
    t.logflush()
    println("invokeErr(): receive data error " + e)
    // throw new SerialException(e)
  }

  /**
   * コマンド送信。スレッドセーフ
   * @param cmd
   */
  private def doCmd(cmd:String) {
    synchronized {
      t.send("W " + cmd)
      println("W "+cmd)
      val r = t.rcv()
      if (r != "$") invokeErr(r)
    }
  }

  /**
   * 粘度計をPC制御モードにする
   */
  def takeControl() = doCmd("ME")

  /**
   * 粘度計タイマーのリセット
   */
  def resetTimer() = doCmd("ZI")

  /**
   * モーター停止
   */
  def stopMotor() = doCmd("MS")


  /**
   * 角速度の設定。このコマンドに対し、粘度計は時刻を返してくるが、無視する。
   * スレッドセーフ
   * @param vel 角速度(rad/s)を表す文字列。書式は{+|-}xxx.xxx
   */
  def setAnglVel(vel:String) {
    synchronized {
      t.send("W CR" + vel)
      val r = t.rcv()
      if (!r.startsWith("MZ")) invokeErr(r)
    }
  }

  /**
   * 角速度の設定。このコマンドに対し、粘度計は時刻を返してくるが、無視する
   * @param vel 角速度(rad/s)
   */
  def setAnglVel(vel:Double) {
    val df = new DecimalFormat("000.000")
    setAnglVel("+" + df.format(vel))
  }
    
  /**
   * 粘度計データの読み出し<p>
   * 粘度計が保持しているデータの読み出しを行う。スレッドセーフになっており、
   * 同時に複数スレッドから呼び出されると一つのスレッドを除きブロックする
   * @param cmd 読み出すデータの種類を指定するパラメータ
   * @return 粘度計が保持しているデータ
   */
  private def readData(cmd:String):Double = {
    synchronized {
      t.send("R " + cmd)
      val r = t.rcv()
      if (!r.startsWith(cmd)) {
      	invokeErr(cmd +":"+ r +":"+ r.length)
      	0	
      } else r.substring(3).toDouble
    }
  }

  /**
   * 粘度計タイマーの読み出し(s)
   */
  def readTime:Double = readData("MZ")

  /**
   * トルクの読み出し(Nm)
   */
  def readTorque:Double = readData("DM")

  /**
   * 角速度の読み出し(rad/s)
   */
  def readAnglVel:Double = readData("WG")

  /**
   * 温度の読み出し
   */
  def readTemp:Double = readData("ST")
}
