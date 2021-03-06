/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ijopencv.opencv;

import ij.gui.Line;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.List;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_core.cvRound;
import static org.bytedeco.javacpp.opencv_core.split;
import org.scijava.Prioritized;
import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.log.LogService;
import org.scijava.plugin.Plugin;

@Plugin(type = Converter.class, priority = Priority.LOW_PRIORITY)
public class MatListLineConverter extends AbstractConverter<Mat, List<Line>> {

    @Override
    public int compareTo(Prioritized o) {
        return super.compareTo(o);
    }

    @Override
    public LogService log() {
        return super.log();
    }

    @Override
    public String getIdentifier() {
        return super.getIdentifier();
    }

    @Override
    public < T> T convert(Object o, Class< T> type) {
        Mat lines = (Mat) o;
        ArrayList<Line> ijlines = new ArrayList<Line>();
        opencv_core.MatVector xy = new opencv_core.MatVector(2);
        split(lines, xy);
        for (int i = 0; i < lines.rows(); i++) {

            float rho = xy.get(0).getFloatBuffer().get(i);
            float theta = xy.get(1).getFloatBuffer().get(i);

            double a = cos(theta);
            double b = sin(theta);
            double x0 = a * rho, y0 = b * rho;

            opencv_core.Point pt1 = new opencv_core.Point(cvRound(x0 + 1000 * (-b)), cvRound(y0 + 1000 * (a))),
                    pt2 = new opencv_core.Point(cvRound(x0 - 1000 * (-b)), cvRound(y0 - 1000 * (a)));
            Line line = new Line(x0 + 1000 * (-b), y0 + 1000 * (a), x0 - 1000 * (-b), y0 - 1000 * (a));
            ijlines.add(line);
        }
        return (T) ijlines;
    }

    @Override
    public Class< List<Line>> getOutputType() {
        return (Class<List<Line>>) (Object) List.class;
    }

    @Override
    public Class< Mat> getInputType() {
        return Mat.class;
    }
}
