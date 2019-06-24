package ijopencv.examples;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ijopencv.ij.ImagePlusMatConverter;
import ijopencv.opencv.RectRoiConverter;
import java.util.ArrayList;
import java.util.HashMap;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.indexer.FloatBufferIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_core.NORM_MINMAX;
import static org.bytedeco.javacpp.opencv_core.minMaxLoc;
import static org.bytedeco.javacpp.opencv_core.normalize;
import org.bytedeco.javacpp.opencv_imgproc;
import static org.bytedeco.javacpp.opencv_imgproc.TM_CCOEFF_NORMED;
import static org.bytedeco.javacpp.opencv_imgproc.TM_CCOEFF;
import static org.bytedeco.javacpp.opencv_imgproc.matchTemplate;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jonathan
 */
@Plugin(type = Command.class, headless = true, menuPath = "Plugins>IJ-OpenCV-plugins>Template matching")
public class TemplateMatchingJ_ implements Command {
	

	
    @Parameter(label="Template image")
    private ImagePlus templateImage;

    @Parameter(label="Target image")
    private ImagePlus targetImage;
    
    @Parameter(label="Matching method")
    private String matchMethod;

    @Override
    public void run() {
        
    	// Check if searchRoi and crop accordingly
    	Roi searchRoi = targetImage.getRoi();
        ImagePlus searchedImage = new ImagePlus();
    	if (searchRoi.isArea() && (searchRoi.getType() == Roi.RECTANGLE)) {

            // Crop the target image to the search region
            searchedImage = targetImage.crop();
        }
        
        else {
        	searchedImage = targetImage;
        }
        
        //Converters
        ImagePlusMatConverter ic = new ImagePlusMatConverter();
        RectRoiConverter rc = new RectRoiConverter();

        // Convert the ImageJ images to OpenCV images
        opencv_core.Mat matching = ic.convert(templateImage, Mat.class);
		opencv_core.Mat template = ic.convert(searchedImage, Mat.class);

        opencv_core.Mat gray = new opencv_core.Mat();
        opencv_imgproc.cvtColor(matching, gray, opencv_imgproc.COLOR_BGR2GRAY);
        opencv_imgproc.cvtColor(template, template, opencv_imgproc.COLOR_BGR2GRAY);

        opencv_core.Mat results = new opencv_core.Mat();

        // Matching and normalisation
        matchTemplate(gray, template, results, TM_CCOEFF_NORMED);
        normalize(results, results, 0, 1, NORM_MINMAX, -1, new opencv_core.Mat());

        DoublePointer minVal = new DoublePointer();
        DoublePointer maxVal= new DoublePointer();
        opencv_core.Point minLoc = new opencv_core.Point();
        opencv_core.Point maxLoc = new opencv_core.Point();
        opencv_core.Point matchLoc;

        minMaxLoc(results, minVal, maxVal, minLoc, maxLoc, new opencv_core.Mat());

        ArrayList<opencv_core.Point> locations = obtainLocations(results, 0.99, template.cols(), template.rows());
        RoiManager rm = new RoiManager();
        rm.setVisible(true);

        opencv_core.Rect solution;
        Roi solutionIJ;
        opencv_core.Point p;
        for (int i = 0; i < locations.size(); i++) {
            p = locations.get(i);
            solution = new opencv_core.Rect(p.x(), p.y(), template.cols(), template.rows());
            solutionIJ = rc.convert(solution, Roi.class);
            rm.add(original, solutionIJ, i);

        }

        imp.changes = false;
        imp.close();
        original.show();


    }

    ArrayList<opencv_core.Point> obtainLocations(opencv_core.Mat results, double threshold, int stepX, int stepY) {

        ArrayList<opencv_core.Point> points = new ArrayList<opencv_core.Point>();

        FloatBufferIndexer sI = results.createIndexer();

        for (int y = 0; y < results.rows(); y++) {
            int x = 0;
            while (x < results.cols()) {
                if (sI.get(y, x) > threshold) {
                    opencv_core.Point p = new opencv_core.Point(x, y);
                    points.add(p);
                    x = x + stepX;
                } else {
                    x++;
                }
            }
        }

        return points;

    }

}
