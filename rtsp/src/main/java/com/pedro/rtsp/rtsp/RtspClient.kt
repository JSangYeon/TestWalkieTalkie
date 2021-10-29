/*
 * Copyright (C) 2021 pedroSG94.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pedro.rtsp.rtsp

import android.media.MediaCodec
import android.util.Log
import com.pedro.rtsp.rtsp.commands.CommandsManager
import com.pedro.rtsp.rtsp.commands.Method
import com.pedro.rtsp.utils.ConnectCheckerRtsp
import com.pedro.rtsp.utils.CreateSSLSocket.createSSlSocket
import com.pedro.rtsp.utils.RtpConstants
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.util.concurrent.*
import java.util.regex.Pattern

/**
 * Created by pedro on 10/02/17.
 */
open class RtspClient(private val connectCheckerRtsp: ConnectCheckerRtsp) {

  private val TAG = "RtspClient"

  //sockets objects
  private var connectionSocket: Socket? = null
  private var reader: BufferedReader? = null
  private var writer: BufferedWriter? = null
  private var thread: ExecutorService? = null
  private val semaphore = Semaphore(0)


  private var pauseExecutor: ExecutorService? = null

  @Volatile
  var isStreaming = false
    private set

  //for secure transport
  private var tlsEnabled = false
  private val rtspSender: RtspSender = RtspSender(connectCheckerRtsp)
  private var url: String? = null
  private val commandsManager: CommandsManager = CommandsManager()
  private var doingRetry = false
  private var numRetry = 0
  private var reTries = 0
  private var handler: ScheduledExecutorService? = null
  private var runnable: Runnable? = null
  private var checkServerAlive = false

  val droppedAudioFrames: Long
    get() = rtspSender.droppedAudioFrames
  val droppedVideoFrames: Long
    get() = rtspSender.droppedVideoFrames

  val cacheSize: Int
    get() = rtspSender.getCacheSize()
  val sentAudioFrames: Long
    get() = rtspSender.getSentAudioFrames()
  val sentVideoFrames: Long
    get() = rtspSender.getSentVideoFrames()

  companion object {
    private val rtspUrlPattern = Pattern.compile("^rtsps?://([^/:]+)(?::(\\d+))*/([^/]+)/?([^*]*)$")
    var isPause = false;
  }

  /**
   * Check periodically if server is alive using Echo protocol.
   */
  fun setCheckServerAlive(enabled: Boolean) {
    checkServerAlive = enabled
  }

  /**
   * Must be called before connect
   */
  fun setOnlyAudio(onlyAudio: Boolean) {
    if (onlyAudio) {
      RtpConstants.trackAudio = 0
      RtpConstants.trackVideo = 1
    } else {
      RtpConstants.trackVideo = 0
      RtpConstants.trackAudio = 1
    }
    commandsManager.audioDisabled = false
    commandsManager.videoDisabled = onlyAudio
  }

  /**
   * Must be called before connect
   */
  fun setOnlyVideo(onlyVideo: Boolean) {
    RtpConstants.trackVideo = 0
    RtpConstants.trackAudio = 1
    commandsManager.videoDisabled = false
    commandsManager.audioDisabled = onlyVideo
  }

  fun setProtocol(protocol: Protocol) {
    commandsManager.protocol = protocol
  }

  fun setAuthorization(user: String?, password: String?) {
    commandsManager.setAuth(user, password)
  }

  fun setReTries(reTries: Int) {
    numRetry = reTries
    this.reTries = reTries
  }

  fun shouldRetry(reason: String): Boolean {
    val validReason = doingRetry && !reason.contains("Endpoint malformed")
    return validReason && reTries > 0
  }

  fun setVideoInfo(sps: ByteBuffer?, pps: ByteBuffer?, vps: ByteBuffer?) {
    Log.i(TAG, "send sps and pps")
    commandsManager.setVideoInfo(sps, pps, vps)
    semaphore.release()
  }

  fun setAudioInfo(sampleRate: Int, isStereo: Boolean) {
    commandsManager.setAudioInfo(sampleRate, isStereo)
  }
  fun replay(){

    Log.i(TAG, "pauseExecutor : ${pauseExecutor}")
    pauseExecutor?.execute post@{
      try {


        if (!commandsManager.videoDisabled) {
          writer?.write(commandsManager.createSetup(RtpConstants.trackVideo))
          writer?.flush()
          val setupVideoStatus = commandsManager.getResponse(reader, Method.SETUP).status
          if (setupVideoStatus != 200) {
//            connectCheckerRtsp.onConnectionFailedRtsp("Error configure stream, setup video $setupVideoStatus")
            Log.d("CommandsManager","connect7 ${setupVideoStatus}")
            return@post
          }
        }
        if (!commandsManager.audioDisabled) {
          writer?.write(commandsManager.createSetup(RtpConstants.trackAudio))
          writer?.flush()
          val setupAudioStatus = commandsManager.getResponse(reader, Method.SETUP).status
          if (setupAudioStatus != 200) {
//            connectCheckerRtsp.onConnectionFailedRtsp("Error configure stream, setup audio $setupAudioStatus")
            Log.d("CommandsManager","connect7 ${setupAudioStatus}")
            return@post
          }
        }
        writer?.write(commandsManager.createRecord())
        writer?.flush()
        val recordStatus = commandsManager.getResponse(reader, Method.RECORD).status
        if (recordStatus != 200) {
          connectCheckerRtsp.onConnectionFailedRtsp("Error configure stream, record $recordStatus")
          Log.d("rtsp테스트","connect9")
          return@post
        }
        Log.d("TAG", "recordStatus : $recordStatus")
        writer?.write(commandsManager.createPlay())
        writer?.flush()

        isPause = false
        Log.i(TAG, "write play")
      } catch (e: IOException) {

        Log.e(TAG, "disconnect error", e)
      }
    }
    try {
      pauseExecutor?.shutdownNow()
      pauseExecutor?.awaitTermination(200, TimeUnit.MILLISECONDS)
    } catch (e: Exception) { }

  }

  @JvmOverloads
  fun connect(url: String?, isRetry: Boolean = false) {
    if (!isRetry) doingRetry = true
    if (url == null) {
      isStreaming = false
      connectCheckerRtsp.onConnectionFailedRtsp("Endpoint malformed, should be: rtsp://ip:port/appname/streamname")
      Log.d("rtsp테스트","connect1")
      return
    }
    if (!isStreaming || isRetry) {
      this.url = url
      connectCheckerRtsp.onConnectionStartedRtsp(url)
      val rtspMatcher = rtspUrlPattern.matcher(url)
      if (rtspMatcher.matches()) {
        tlsEnabled = (rtspMatcher.group(0) ?: "").startsWith("rtsps")
      } else {
        isStreaming = false
        connectCheckerRtsp.onConnectionFailedRtsp("Endpoint malformed, should be: rtsp://ip:port/appname/streamname")
        Log.d("rtsp테스트","connect2")
        return
      }
      val host = rtspMatcher.group(1) ?: ""
      val port: Int = (rtspMatcher.group(2) ?: "554").toInt()
      val streamName = if (rtspMatcher.group(4).isNullOrEmpty()) "" else "/" + rtspMatcher.group(4)
      val path = "/" + rtspMatcher.group(3) + streamName

      isStreaming = true
      thread = Executors.newSingleThreadExecutor()
      thread?.execute post@{
        try {
          commandsManager.setUrl(host, port, path)
          rtspSender.setSocketsInfo(commandsManager.protocol,
            commandsManager.videoClientPorts,
            commandsManager.audioClientPorts)
          if (!commandsManager.audioDisabled) {
            rtspSender.setAudioInfo(commandsManager.sampleRate)
          }
          if (!commandsManager.videoDisabled) {
            if (commandsManager.sps == null || commandsManager.pps == null) {
              semaphore.drainPermits()
              Log.i(TAG, "waiting for sps and pps")
              semaphore.tryAcquire(5000, TimeUnit.MILLISECONDS)
            }
            if (commandsManager.sps == null || commandsManager.pps == null) {
              connectCheckerRtsp.onConnectionFailedRtsp("sps or pps is null")
              Log.d("rtsp테스트","connect3")
              return@post
            } else {
              rtspSender.setVideoInfo(commandsManager.sps!!,
                commandsManager.pps!!,
                commandsManager.vps)
            }
          }
          if (!tlsEnabled) {
            connectionSocket = Socket()
            val socketAddress: SocketAddress = InetSocketAddress(host, port)
            connectionSocket?.connect(socketAddress, 5000)
          } else {
            connectionSocket = createSSlSocket(host, port)
            if (connectionSocket == null) throw IOException("Socket creation failed")
          }
          connectionSocket?.soTimeout = 3600000
          reader = BufferedReader(InputStreamReader(connectionSocket?.getInputStream()))
          val outputStream = connectionSocket?.getOutputStream()
          writer = BufferedWriter(OutputStreamWriter(outputStream))
          writer?.write(commandsManager.createOptions()) // rtsp 서버에 요청 시작하는부분
          writer?.flush()
          commandsManager.getResponse(reader, Method.OPTIONS)
          writer?.write(commandsManager.createAnnounce())
          writer?.flush()
          //check if you need credential for stream, if you need try connect with credential
          val announceResponse = commandsManager.getResponse(reader, Method.ANNOUNCE)
          when (announceResponse.status) {
            403 -> {
              connectCheckerRtsp.onConnectionFailedRtsp("Error configure stream, access denied")
              Log.d("rtsp테스트","connect4")
              Log.e(TAG, "Response 403, access denied")
              return@post
            }
            401 -> {
              if (commandsManager.user == null || commandsManager.password == null) {
                connectCheckerRtsp.onAuthErrorRtsp()
                return@post
              } else {
                writer?.write(commandsManager.createAnnounceWithAuth(announceResponse.text))
                writer?.flush()
                when (commandsManager.getResponse(reader, Method.ANNOUNCE).status) {
                  401 -> {
                    connectCheckerRtsp.onAuthErrorRtsp()
                    return@post
                  }
                  200 -> {
                    connectCheckerRtsp.onAuthSuccessRtsp()
                  }
                  else -> {
                    connectCheckerRtsp.onConnectionFailedRtsp("Error configure stream, announce with auth failed")
                    Log.d("rtsp테스트","connect5")
                    return@post
                  }
                }
              }
            }
            200 -> {
              Log.i(TAG, "announce success")
            }
            else -> {
              connectCheckerRtsp.onConnectionFailedRtsp("Error configure stream, announce failed")
              Log.d("rtsp테스트","connect6")
              return@post
            }
          }
          if (!commandsManager.videoDisabled) {
            writer?.write(commandsManager.createSetup(RtpConstants.trackVideo))
            writer?.flush()
            val setupVideoStatus = commandsManager.getResponse(reader, Method.SETUP).status
            if (setupVideoStatus != 200) {
              connectCheckerRtsp.onConnectionFailedRtsp("Error configure stream, setup video $setupVideoStatus")
              Log.d("rtsp테스트","connect7")
              return@post
            }
          }
          if (!commandsManager.audioDisabled) {
            writer?.write(commandsManager.createSetup(RtpConstants.trackAudio))
            writer?.flush()
            val setupAudioStatus = commandsManager.getResponse(reader, Method.SETUP).status
            if (setupAudioStatus != 200) {
              connectCheckerRtsp.onConnectionFailedRtsp("Error configure stream, setup audio $setupAudioStatus")
              Log.d("rtsp테스트","connect8")
              return@post
            }
          }
          writer?.write(commandsManager.createRecord())
          writer?.flush()
          val recordStatus = commandsManager.getResponse(reader, Method.RECORD).status
          if (recordStatus != 200) {
            connectCheckerRtsp.onConnectionFailedRtsp("Error configure stream, record $recordStatus")
            Log.d("rtsp테스트","connect9")
            return@post
          }
          outputStream?.let { out ->
            rtspSender.setDataStream(out, host)
          }
          val videoPorts = commandsManager.videoServerPorts
          val audioPorts = commandsManager.audioServerPorts
          if (!commandsManager.videoDisabled) {
            rtspSender.setVideoPorts(videoPorts[0], videoPorts[1])
          }
          if (!commandsManager.audioDisabled) {
            rtspSender.setAudioPorts(audioPorts[0], audioPorts[1])
          }
          rtspSender.start()
          reTries = numRetry
          connectCheckerRtsp.onConnectionSuccessRtsp()
          handleServerCommands()
        } catch (e: Exception) {
          Log.e(TAG, "connection error", e)
          connectCheckerRtsp.onConnectionFailedRtsp("Error configure stream, " + e.message)
          Log.d("rtsp테스트","connect10")
          return@post
        }
      }
    }
  }

  private fun handleServerCommands() {
    //Read and print server commands received each 2 seconds
    while (!Thread.interrupted()) {
      try {
        if (isAlive()) {
          Thread.sleep(2000)
          reader?.let { r ->
            if (r.ready()) {
              val command = commandsManager.getResponse(r)
              //Do something depend of command if required
            }
          }
        } else {
          Thread.currentThread().interrupt()
          connectCheckerRtsp.onConnectionFailedRtsp("No response from server")
        }
      } catch (ignored: SocketTimeoutException) {
        //new packet not found
      } catch (e: Exception) {
        Thread.currentThread().interrupt()
      }
    }
  }

  /*
    Send a heartbeat to know if server is alive using Echo Protocol.
    Your firewall could block it.
   */
  private fun isAlive(): Boolean {
    val connected = connectionSocket?.isConnected ?: false
    if (!checkServerAlive) return connected
    val reachable = connectionSocket?.inetAddress?.isReachable(5000) ?: false
    return if (connected && !reachable) false else connected
  }

  fun disconnect() {
    runnable?.let { handler?.shutdownNow() }
    Log.d("rtsp테스트", "disConnect1")
    disconnect(true)
  }

  fun pause(){
    pauseExecutor = Executors.newSingleThreadExecutor()
    pauseExecutor?.execute post@{
      try {
        rtspSender.stop()
        writer?.write(commandsManager.createPause())
        writer?.flush()
        isPause = true
        Log.i(TAG, "write pause")
      } catch (e: IOException) {

        Log.e(TAG, "disconnect error", e)
      }
    }
//    try {
//      pauseExecutor.shutdownNow()
//      pauseExecutor.awaitTermination(200, TimeUnit.MILLISECONDS)
//    } catch (e: Exception) { }
  }

  private fun disconnect(clear: Boolean) {
    Log.d("rtsp테스트" , "disConnect")
    if (isStreaming) rtspSender.stop()
    thread?.shutdownNow()
    val executor = Executors.newSingleThreadExecutor()
    executor.execute post@{
      try {
        writer?.write(commandsManager.createTeardown())
        writer?.flush()
        if (clear) {
          commandsManager.clear()
        } else {
          commandsManager.retryClear()
        }
        connectionSocket?.close()
        reader?.close()
        reader = null
        writer?.close()
        writer = null
        connectionSocket = null
        Log.i(TAG, "write teardown success")
      } catch (e: IOException) {
        if (clear) {
          commandsManager.clear()
        } else {
          commandsManager.retryClear()
        }
        Log.e(TAG, "disconnect error", e)
      }
    }
    try {
      executor.shutdownNow()
      executor.awaitTermination(200, TimeUnit.MILLISECONDS)
      thread?.awaitTermination(100, TimeUnit.MILLISECONDS)
      thread = null
      semaphore.release()
    } catch (e: Exception) { }
    if (clear) {
      reTries = numRetry
      doingRetry = false
      isStreaming = false
      connectCheckerRtsp.onDisconnectRtsp()
    }
  }

  fun sendVideo(h264Buffer: ByteBuffer, info: MediaCodec.BufferInfo) {
    if (!commandsManager.videoDisabled) {
      rtspSender.sendVideoFrame(h264Buffer, info)
    }
  }

  fun sendAudio(aacBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {
    if (!commandsManager.audioDisabled) {
      rtspSender.sendAudioFrame(aacBuffer, info)
    }
  }

  fun hasCongestion(): Boolean {
    return rtspSender.hasCongestion()
  }

  @JvmOverloads
  fun reConnect(delay: Long, backupUrl: String? = null) {
    Log.d("rtsp테스트", "reConnect1")
    reTries--
    disconnect(false)
    runnable = Runnable {
      val reconnectUrl = backupUrl ?: url
      connect(reconnectUrl, true)
    }
    runnable?.let {
      handler = Executors.newSingleThreadScheduledExecutor()
      handler?.schedule(it, delay, TimeUnit.MILLISECONDS)
    }
  }

  fun resetSentAudioFrames() {
    rtspSender.resetSentAudioFrames()
  }

  fun resetSentVideoFrames() {
    rtspSender.resetSentVideoFrames()
  }

  fun resetDroppedAudioFrames() {
    rtspSender.resetDroppedAudioFrames()
  }

  fun resetDroppedVideoFrames() {
    rtspSender.resetDroppedVideoFrames()
  }

  @Throws(RuntimeException::class)
  fun resizeCache(newSize: Int) {
    rtspSender.resizeCache(newSize)
  }

  fun setLogs(enable: Boolean) {
    rtspSender.setLogs(enable)
  }
}