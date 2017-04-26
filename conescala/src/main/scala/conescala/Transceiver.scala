package conescala

import gnu.io.CommPortIdentifier
import gnu.io.SerialPort
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.util.logging.Level
import java.util.logging.Logger
import System.{currentTimeMillis => sysTime}
import java.io.FileWriter


/**
 * シリアルポートの通信をするクラス。行指向の読み書きをサポートする。受信は無期限に
 * データ到着を待つ
 * @author 涌井
 */
class Transceiver(portName:String) {
  private var firstTime = true // for debug
  private val times = new Array[Long](4)
  private val portId = CommPortIdentifier.getPortIdentifier(portName)
  private val port = portId.open("cone", 2000).asInstanceOf[SerialPort]
  port.setSerialPortParams(9600, SerialPort.DATABITS_8,
                           SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)
  port.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN +
                          SerialPort.FLOWCONTROL_RTSCTS_OUT)
  port.setDTR(true)
  port.setRTS(true)
  private val sps = new SerialPollingStream(port)
  private val br = new BufferedReader(new InputStreamReader(sps))
  private val pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(port.getOutputStream())))
  private val logw = new PrintWriter(new BufferedWriter(new FileWriter("c:/home/log.txt")))


  def terminate() = port.close()

  def logflush() = logw.flush()
  def logclose() = logw.close()
  
  /**
   * データを1行送信する
   * @param s 送信データ
   */
  def send(s:String) {
    times(0) = sysTime
    println(s)
    pw.println(s)
    pw.flush()
    if (firstTime) times(1) = sysTime
  }

  /**
   * データを1行受信する。1行分のデータが到着するまでブロックする。
   * @return 受信データ。エラーが発生した場合は空文字列
   */
  def rcv():String = {
    val r = br.readLine()
    println(r)
    r match {
      case null => rcv // brがストリーム終端を返した場合は再帰する
      case r if r.length == 0 => println("blank"); rcv
      case _ => {
      	Thread.sleep(160) // 送受信の直後に次の送信データを粘度計に送らないようにする 1秒周期版の変更点
      	r		 
      }
    }
  }
  
  /**
   * RxTxの受信ストリームは、読み出しリクエストに対しデータが無くてもブロック
   * せず、0を返す。そのためデータが来るまでブロックするストリームをかぶせる。
   */
  private class SerialPollingStream(port:SerialPort) extends InputStream {

    private var in = port.getInputStream()
    private var waitInterval = 10 // バッファが空の時の待ち時間


    def setWait(t:Int) {
      waitInterval = t
    }


    override def read(b:Array[Byte]):Int = {
      b(0) = read().toByte
      1
    }

    override def read():Int = {
      val n = in.read()
      n match {
        case -1 => n // ストリーム終端に到達
        case 0 => Thread.sleep(waitInterval); read
        case _ => n
      }
    }
  }
}

