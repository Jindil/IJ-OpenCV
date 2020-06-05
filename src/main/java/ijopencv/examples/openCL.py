#@ImagePlus imp
from org.bytedeco.javacpp.opencv_core import Mat, GpuMat, UMat, Size
from org.bytedeco.javacpp.opencv_imgproc import blur
from ijopencv.ij     import ImagePlusMatConverter as ImpToMat
from ijopencv.opencv import MatImagePlusConverter as MatToImp
from org.bytedeco.javacpp.opencv_core import Device

dev = Device.getDefault()

# I - Convert the ImagePlus to an opencv matrix

imCV = ImpToMat.toMat(imp.getProcessor())
print imCV
#print imCL

# CPU -> OK
blured = Mat()
kernel = Size(5,5)
blur(imCV, blured, kernel)
print blured

# GPU
imCL = UMat(imCV)
bluredCL = UMat(imp.height, imp.width, 0) # 8bit
blur(imCL, bluredCL, kernel)
print bluredCL
