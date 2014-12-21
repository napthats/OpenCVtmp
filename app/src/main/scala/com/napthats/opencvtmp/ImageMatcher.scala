package com.napthats.opencvtmp

import android.graphics.Bitmap
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.android.Utils
import org.opencv.imgproc.Imgproc


object  ImageMatcher {
  private val CV_TM_CCOEFF_NORMED = 5
  private val CV_TM_SQDIFF_NORMED = 1
}

class ImageMatcher {
  var templateImageMat : Mat = null

  def registerImage(image : Mat) {
    templateImageMat = image
  }

  //should get Bitmap instead of Mat?
  def matchWith(image_mat : Mat): Option[(Int,Int)] = {
    val result_mat = new Mat()
    Imgproc.matchTemplate(image_mat, templateImageMat, result_mat, ImageMatcher.CV_TM_CCOEFF_NORMED)
    val result = Core.minMaxLoc(result_mat)
    //if (result.minVal < ...
    //Some(result.minLoc.x.toInt, result.minLoc.y.toInt)
    Some(result.maxLoc.x.toInt, result.maxLoc.y.toInt)
  }
}
