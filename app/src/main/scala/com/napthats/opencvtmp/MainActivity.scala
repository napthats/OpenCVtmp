package com.napthats.opencvtmp

import android.app.Activity
import android.os.Bundle
import org.opencv.android.CameraBridgeViewBase.{CvCameraViewFrame, CvCameraViewListener2}
import org.opencv.android.{BaseLoaderCallback, CameraBridgeViewBase, OpenCVLoader, LoaderCallbackInterface}
import org.opencv.core.{Mat, Core, CvType}
import org.opencv.imgproc.Imgproc

class MainActivity extends Activity with CvCameraViewListener2 {
  private var mCameraView: CameraBridgeViewBase = null
  private var mOutputFrame: Mat = null
  private val mLoaderCallback = new BaseLoaderCallback(this) {
    override def onManagerConnected(status: Int) {
      status match {
        case LoaderCallbackInterface.SUCCESS =>
          mCameraView.enableView()
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

  def onCameraFrame(inputFrame: CvCameraViewFrame): Mat = {
    Imgproc.Canny(inputFrame.gray, mOutputFrame, 80, 100)
    Core.bitwise_not(mOutputFrame, mOutputFrame)
    mOutputFrame
  }

}
