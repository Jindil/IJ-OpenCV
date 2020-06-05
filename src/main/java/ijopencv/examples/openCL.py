#@ImagePlus imp
from org.bytedeco.javacpp.opencv_core import Mat, GpuMat, UMat, Size, ACCESS_READ
from org.bytedeco.javacpp.opencv_imgproc import blur
from ijopencv.ij     import ImagePlusMatConverter as ImpToMat
from ijopencv.opencv import MatImagePlusConverter as MatToImp
from org.bytedeco.javacpp.opencv_core import Device
from ij import ImagePlus

dev = Device.getDefault()

# I - Convert the ImagePlus to an opencv matrix
imCV = ImpToMat.toMat(imp.getProcessor())
#print imCV
#print imCL

# CPU -> OK
blured = Mat()
kernel = Size(5,5)
blur(imCV, blured, kernel)
print blured

# GPU
#imCL = UMat(imCV) # this crashes when called then
imCL = imCV.getUMat(ACCESS_READ) 
bluredCL = imCL.clone()

blur(imCL, bluredCL, kernel)
print bluredCL

# CL to CV
bluredCV = bluredCL.getMat(ACCESS_READ)

# CV to ImageJ
imProc = MatToImp.toImageProcessor(bluredCV)
impNew = ImagePlus("test", imProc)
impNew.show()