package image;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.*;

/**
 *
 */
public class Main {
    public static void main(String args[]) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.FAST);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

// DETECTION
// first image
        Mat img1 = Imgcodecs.imread("ip1.jpg", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat descriptors1 = new Mat();
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();

        detector.detect(img1, keypoints1);
        descriptor.compute(img1, keypoints1, descriptors1);

// second image
        Mat img2 = Imgcodecs.imread("ip2.jpg", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat descriptors2 = new Mat();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();

        detector.detect(img2, keypoints2);
        descriptor.compute(img2, keypoints2, descriptors2);

// MATCHING
// match these two keypoints sets
        List<MatOfDMatch> matches = new ArrayList<>();
        matcher.knnMatch(descriptors1, descriptors2, matches, 5);

        LinkedList<DMatch> good_matches = new LinkedList<>();
        for (MatOfDMatch matOfDMatch : matches) {
            if (matOfDMatch.toArray()[0].distance / matOfDMatch.toArray()[1].distance < 0.9) {
                good_matches.add(matOfDMatch.toArray()[0]);
            }
        }

        // get keypoint coordinates of good matches to find homography and remove outliers using ransac
        List<Point> pts1 = new ArrayList<>();
        List<Point> pts2 = new ArrayList<>();
        for (DMatch good_match : good_matches) {
            pts1.add(keypoints1.toList().get(good_match.queryIdx).pt);
            pts2.add(keypoints2.toList().get(good_match.trainIdx).pt);
        }

        // convertion of data types - there is maybe a more beautiful way


        Mat outputMask = new Mat();
        MatOfPoint2f pts1Mat = new MatOfPoint2f();
        pts1Mat.fromList(pts1);
        MatOfPoint2f pts2Mat = new MatOfPoint2f();
        pts2Mat.fromList(pts2);

        // Find homography - here just used to perform match filtering with RANSAC, but could be used to e.g. stitch images
        // the smaller the allowed reprojection error (here 15), the more matches are filtered
        Calib3d.findHomography(pts1Mat, pts2Mat, Calib3d.RANSAC, 15, outputMask, 2000, 0.995);

        // outputMask contains zeros and ones indicating which matches are filtered
        LinkedList<DMatch> better_matches = new LinkedList<>();
        for (int i = 0; i < good_matches.size(); i++) {
            if (outputMask.get(i, 0)[0] != 0.0) {
                better_matches.add(good_matches.get(i));
            }
        }

        // DRAWING OUTPUT
        Mat outputImg = new Mat();
        // this will draw all matches, works fine
        MatOfDMatch better_matches_mat = new MatOfDMatch();
        better_matches_mat.fromList(better_matches);
        Features2d.drawMatches(img1, keypoints1, img2, keypoints2, better_matches_mat, outputImg);
        System.out.println(keypoints1.toArray().length);
        System.out.println(keypoints2.toArray().length);
        System.out.println(better_matches_mat.toArray().length);

        Mat resize = new Mat();
        // save image
        Imgcodecs.imwrite("result.jpg", outputImg);
    }
}
