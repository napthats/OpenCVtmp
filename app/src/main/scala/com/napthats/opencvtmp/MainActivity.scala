package com.napthats.opencvtmp

import java.util

import android.util.Log
import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import org.opencv.android.CameraBridgeViewBase.{CvCameraViewFrame, CvCameraViewListener2}
import org.opencv.android.{BaseLoaderCallback, CameraBridgeViewBase, OpenCVLoader, LoaderCallbackInterface}
import org.opencv.core.{Rect, Mat, Core, CvType}
import org.opencv.video.BackgroundSubtractor
import org.opencv.video.BackgroundSubtractorMOG2
import org.opencv.android.Utils

class MainActivity extends Activity with CvCameraViewListener2 {
  private var mCameraView: CameraBridgeViewBase = null
  private var mOutputFrame: Mat = null
  private var imageMatcher : ImageMatcher = null
  private var frameImageMat : Mat = null
  private var bs : BackgroundSubtractor = null

  private val mLoaderCallback = new BaseLoaderCallback(this) {
    override def onManagerConnected(status: Int) {
      status match {
        case LoaderCallbackInterface.SUCCESS =>
          mCameraView.enableView()
          val twicat = Utils.loadResource(getApplicationContext, R.drawable.twicat)
          //Log.i("twicat_type", twicat.`type`().toString())
          imageMatcher = new ImageMatcher()
          imageMatcher.registerImage(twicat)
          frameImageMat = Utils.loadResource(getApplicationContext, R.drawable.frame)
          bs = new BackgroundSubtractorMOG2(1000,36)
         case _ =>
          super.onManagerConnected(status)
      }
    }
  }

  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    mCameraView = findViewById(R.id.camera_view).asInstanceOf[CameraBridgeViewBase]
    mCameraView.setCvCameraViewListener(this)
 }

  override def onPause() {
    if (mCameraView != null) {
      mCameraView.disableView()
    }
    super.onPause()
  }

  protected override def onResume() {
    super.onResume()
    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_4, this, mLoaderCallback)
  }

  override def onDestroy() {
    super.onDestroy()
    if (mCameraView != null) {
      mCameraView.disableView()
    }
  }

  def onCameraViewStarted(width: Int, height: Int) {
    mOutputFrame = new Mat(height, width, CvType.CV_8UC1)
  }

  def onCameraViewStopped() {
    mOutputFrame.release()
  }

  var clicked = false
  def onButtonClick(view : View): Unit = {
    view.getId match {
      case R.id.button_p => clicked = true
      case R.id.button_r =>
        picture = None
        diff_picture = None
    }
  }

  var picture : Option[Mat] = None
  var diff_picture : Option[Mat] = None
  def onCameraFrame(input_frame: CvCameraViewFrame): Mat = {
    val input_mat = input_frame.rgba()
    val output_mat = new Mat()
    val fmask = new Mat()
    bs.apply(input_mat, fmask)
    Core.bitwise_and(input_mat, input_mat, output_mat, fmask)
    //output_mat
    fmask
  }
}
