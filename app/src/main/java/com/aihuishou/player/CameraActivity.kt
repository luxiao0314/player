package com.aihuishou.player

import android.app.Activity
import android.content.res.Configuration
import android.hardware.Camera
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.github.faucamp.simplertmp.RtmpHandler
import com.github.faucamp.simplertmp.RtmpHandler.RtmpListener
import com.seu.magicfilter.utils.MagicFilterType
import kotlinx.android.synthetic.main.activity_camera.*
import net.ossrs.yasea.SrsCameraView
import net.ossrs.yasea.SrsEncodeHandler
import net.ossrs.yasea.SrsEncodeHandler.SrsEncodeListener
import net.ossrs.yasea.SrsPublisher
import net.ossrs.yasea.SrsRecordHandler
import net.ossrs.yasea.SrsRecordHandler.SrsRecordListener
import java.io.IOException
import java.net.SocketException

/**
 * Created by Sikang on 2017/5/2.
 */
class CameraActivity : Activity(), SrsEncodeListener, RtmpListener, SrsRecordListener,
    View.OnClickListener {

    private lateinit var publisher: SrsPublisher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_camera)

        url.setText("rtmp://10.184.18.148:1935/abcs/room")
        publish.setOnClickListener(this)
        swCam.setOnClickListener(this)
        swEnc.setOnClickListener(this)
        publisher = SrsPublisher(glsurfaceview_camera as SrsCameraView)
        //编码状态回调
        publisher.setEncodeHandler(SrsEncodeHandler(this))
        publisher.setRecordHandler(SrsRecordHandler(this))
        //rtmp推流状态回调
        publisher.setRtmpHandler(RtmpHandler(this))
        //预览分辨率
        publisher.setPreviewResolution(1280, 720)
        //推流分辨率
        publisher.setOutputResolution(720, 1280)
        //传输率
        publisher.setVideoHDMode()
        //开启美颜（其他滤镜效果在MagicFilterType中查看）
        publisher.switchCameraFilter(MagicFilterType.BEAUTY)
        //打开摄像头，开始预览（未推流）
        publisher.startCamera()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.publish -> if (publish.text.toString().contentEquals("开始")) {
                val rtmpUrl = url.text.toString()
                if (TextUtils.isEmpty(rtmpUrl)) {
                    Toast.makeText(applicationContext, "地址不能为空！", Toast.LENGTH_SHORT).show()
                }
                publisher.startPublish(rtmpUrl)
                publisher.startCamera()
                if (swEnc.text.toString().contentEquals("软编码")) {
                    Toast.makeText(applicationContext, "当前使用硬编码", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "当前使用软编码", Toast.LENGTH_SHORT).show()
                }
                publish.text = "停止"
                swEnc.isEnabled = false
            } else if (publish.text.toString().contentEquals("停止")) {
                publisher.stopPublish()
                publisher.stopRecord()
                publish.text = "开始"
                swEnc.isEnabled = true
            }
            R.id.swCam -> publisher.switchCameraFace((publisher.cameraId + 1) % Camera.getNumberOfCameras())
            R.id.swEnc -> if (swEnc.text.toString().contentEquals("软编码")) {
                publisher.switchToSoftEncoder()
                swEnc.text = "硬编码"
            } else if (swEnc.text.toString().contentEquals("硬编码")) {
                publisher.switchToHardEncoder()
                swEnc.text = "软编码"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        publisher.resumeRecord()
    }

    override fun onPause() {
        super.onPause()
        publisher.pauseRecord()
    }

    override fun onDestroy() {
        super.onDestroy()
        publisher.stopPublish()
        publisher.stopRecord()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        publisher.stopEncode()
        publisher.stopRecord()
        publisher.setScreenOrientation(newConfig.orientation)
        if (publish.text.toString().contentEquals("停止")) {
            publisher.startEncode()
        }
        publisher.startCamera()
    }

    override fun onNetworkWeak() {
        Toast.makeText(this, "网络型号弱", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkResume() {}
    override fun onEncodeIllegalArgumentException(e: IllegalArgumentException) {
        handleException(e)
    }

    private fun handleException(e: Exception) {
        try {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            publisher.stopPublish()
            publisher.stopRecord()
            publish.text = "开始"
        } catch (e1: Exception) {
            //
        }
    }

    override fun onRtmpConnecting(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onRtmpConnected(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onRtmpVideoStreaming() {}
    override fun onRtmpAudioStreaming() {}
    override fun onRtmpStopped() {
        Toast.makeText(applicationContext, "已停止", Toast.LENGTH_SHORT).show()
    }

    override fun onRtmpDisconnected() {
        Toast.makeText(applicationContext, "未连接服务器", Toast.LENGTH_SHORT).show()
    }

    override fun onRtmpVideoFpsChanged(fps: Double) {}
    override fun onRtmpVideoBitrateChanged(bitrate: Double) {}
    override fun onRtmpAudioBitrateChanged(bitrate: Double) {}
    override fun onRtmpSocketException(e: SocketException) {
        handleException(e)
    }

    override fun onRtmpIOException(e: IOException) {
        handleException(e)
    }

    override fun onRtmpIllegalArgumentException(e: IllegalArgumentException) {
        handleException(e)
    }

    override fun onRtmpIllegalStateException(e: IllegalStateException) {
        handleException(e)
    }

    override fun onRecordPause() {
        Toast.makeText(applicationContext, "Record paused", Toast.LENGTH_SHORT).show()
    }

    override fun onRecordResume() {
        Toast.makeText(applicationContext, "Record resumed", Toast.LENGTH_SHORT).show()
    }

    override fun onRecordStarted(msg: String) {
        Toast.makeText(applicationContext, "Recording file: $msg", Toast.LENGTH_SHORT).show()
    }

    override fun onRecordFinished(msg: String) {
        Toast.makeText(applicationContext, "MP4 file saved: $msg", Toast.LENGTH_SHORT).show()
    }

    override fun onRecordIOException(e: IOException) {
        handleException(e)
    }

    override fun onRecordIllegalArgumentException(e: IllegalArgumentException) {
        handleException(e)
    }
}