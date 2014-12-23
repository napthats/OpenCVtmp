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
import org.opencv.android.Utils

class MainActivity extends Activity with CvCameraViewListener2 {
  private var mCameraView: CameraBridgeViewBase = null
  private var mOutputFrame: Mat = null
  private var imageMatcher : ImageMatcher = null
  private var frameImageMat : Mat = null

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
    if (clicked) {
      clicked = false
      diff_picture match {
        case None =>
          picture match {
            case None => picture = Some(input_frame.rgba())
            case Some(p) =>
              val q = new Mat()
              Core.absdiff(input_frame.rgba(), p, q)
              diff_picture = Some(q)
          }
        case Some(_) =>
          None
      }
   }

    diff_picture match {
      case None => input_frame.rgba()
      case Some(p) => p
    }




    //Imgproc.Canny(inputFrame.gray, mOutputFrame, 50, 100)
    //Core.bitwise_not(mOutputFrame, mOutputFrame)
    //mOutputFrame

    /*var input_mat_list  = new util.ArrayList[Mat]()
    Core.split(input_frame.rgba(), input_mat_list)
    input_mat_list.remove(3)
    var input_mat : Mat = new Mat()
    Core.merge(input_mat_list, input_mat)
    //Log.i("input_type", input_mat.`type`().toString())
    imageMatcher.matchWith(input_mat) match {
      case Some((x, y)) =>
        Log.i("x", x.toString())
        Log.i("y", y.toString())
        //Log.i("input_mat_width", input_mat.size().width.toString())
        //Log.i("input_mat_height", input_mat.size().height.toString())
        val rect = new Rect(x, y, 364, 364)
        val input_mat_copy = new Mat(input_mat, rect)
        //frameImageMat.copyTo(input_mat_copy)
        new Mat(364, 364, CvType.CV_8UC3).copyTo(input_mat_copy)
        input_mat
      case None => input_mat
    }*/
  }
}
