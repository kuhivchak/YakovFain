package image;

import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Vladik on 29.04.2017.
 */
public class Myclass {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.ORB);

        //TODO Read first image
        Mat frame1 = new Mat();
        Mat frameOut = new Mat();
        frame1 = Imgcodecs.imread("not1.jpg");
        MatOfKeyPoint matOfKeyPoint1 = new MatOfKeyPoint();
        featureDetector.detect(frame1, matOfKeyPoint1);

        //TODO Read second image
        Mat frame2 = new Mat();
        Mat frameOut2 = new Mat();
        frame2 = Imgcodecs.imread("not2.jpg");

        MatOfKeyPoint matOfKeyPoint2 = new MatOfKeyPoint();
        featureDetector.detect(frame2, matOfKeyPoint2);

        //TODO Descriptor
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        Mat descriptor1 = new Mat();
        Mat descriptor2 = new Mat();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        descriptor.compute(frame1, matOfKeyPoint1, descriptor1);
        descriptor.compute(frame2, matOfKeyPoint2, descriptor2);

        // TODO Matching
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptor1, descriptor2, matches);

        List<DMatch> bestMatchList = matches.toList();
        Collections.sort(bestMatchList, (o1, o2) -> {
            if (o1.distance < o2.distance) {
                return -1;
            }
            if (o1.distance > o2.distance) {
                return 1;
            }
            return 0;
        });
        MatOfDMatch bestMatches = new MatOfDMatch();
        bestMatches.fromList(bestMatchList.subList(0, 10));

        //TODO init best keyPoints
        ArrayList<KeyPoint> bestKeyPoints1 = new ArrayList<>();
        ArrayList<KeyPoint> bestKeyPoints2 = new ArrayList<>();

        List<KeyPoint> keyPoints = matOfKeyPoint1.toList();
        List<KeyPoint> keyPoints2 = matOfKeyPoint2.toList();

        for (int i = 0; i < 10; i++) {
            bestKeyPoints1.add(keyPoints.get(bestMatchList.get(i).queryIdx));
            bestKeyPoints2.add(keyPoints2.get(bestMatchList.get(i).trainIdx));
        }
        //__________________________________________________________
        //_____________________________________________________
        List<Triangle> triangles = new ArrayList<>();
        for (int i = 0; i < bestKeyPoints1.size(); i++) {
            for (int j = i + 1; j < bestKeyPoints1.size(); j++) {
                for (int k = j + 1; k < bestKeyPoints1.size(); k++) {
                    triangles.add(new Triangle(bestKeyPoints1.get(i).pt, bestKeyPoints1.get(j).pt, bestKeyPoints1.get(k).pt));
                }
            }
        }
        List<Triangle> triangles2 = new ArrayList<>();

        for (int i = 0; i < bestKeyPoints2.size(); i++) {
            for (int j = i + 1; j < bestKeyPoints2.size(); j++) {
                for (int k = j + 1; k < bestKeyPoints2.size(); k++) {
                    triangles2.add(new Triangle(bestKeyPoints2.get(i).pt, bestKeyPoints2.get(j).pt, bestKeyPoints2.get(k).pt));
                }
            }
        }

        int counter = 0;
        Iterator<Triangle> iter = triangles.iterator();
        while (iter.hasNext()) {
            Triangle i = iter.next();
            Iterator<Triangle> iter2 = triangles2.iterator();
            while (iter2.hasNext()) {
                if (i.compareTo(iter2.next()) == 0) {
                    iter.remove();
                    iter2.remove();
                    counter++;
                    break;
                }
            }
        }
        //TODO Write first
        MatOfKeyPoint write1 = new MatOfKeyPoint();
        write1.fromList(bestKeyPoints1);
        Features2d.drawKeypoints(frame1, write1, frameOut);
        Imgcodecs.imwrite("my1" + ".jpg", frameOut);

        //TODO Write second
        MatOfKeyPoint write2 = new MatOfKeyPoint();
        write2.fromList(bestKeyPoints2);
        Features2d.drawKeypoints(frame2, write2, frameOut2);
        Imgcodecs.imwrite("my2" + ".jpg", frameOut2);
        Mat frameOut3 = new Mat();
        Features2d.drawMatches(frame1, matOfKeyPoint1, frame2, matOfKeyPoint2, bestMatches, frameOut3);
        Imgcodecs.imwrite("my3" + ".jpg", frameOut3);

        System.out.println("Згенеровано КТ1: " + matOfKeyPoint1.toArray().length);
        System.out.println("Згенеровано КТ2: " + matOfKeyPoint2.toArray().length);
        System.out.println("Спільних точок: " + bestMatchList.size());
        System.out.println("The same triangles: " + counter);
//
    }
}
