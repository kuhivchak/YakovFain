import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;

import static org.opencv.imgproc.Imgproc.resize;

public class MainTest {
    public static void main(String args[]) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

// DETECTION
// first image
        Mat img1 = Imgcodecs.imread("car1.jpg", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        //img1 = MainTest.resizeImage(img1, new Size(256,256)); //TODO
        Mat descriptors1 = new Mat();
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();

        detector.detect(img1, keypoints1);
        descriptor.compute(img1, keypoints1, descriptors1);

// second image
        Mat img2 = Imgcodecs.imread("car2.jpg", Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        //img2 = MainTest.resizeImage(img2, new Size(256,256));//TODO
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

        // TODO Added MyCLass code
        Collections.sort(better_matches, (o1, o2) -> {
            if (o1.distance < o2.distance) {
                return -1;
            }
            if (o1.distance > o2.distance) {
                return 1;
            }
            return 0;
        });
        MatOfDMatch bestMatches = new MatOfDMatch();
        try {
            bestMatches.fromList(better_matches.subList(0, 3));
        } catch (Exception e) {
            bestMatches.fromList(better_matches);
        }
        //TODO init best keyPoints
        ArrayList<KeyPoint> bestKeyPoints1 = new ArrayList<>();
        ArrayList<KeyPoint> bestKeyPoints2 = new ArrayList<>();

        List<KeyPoint> keyPoints = keypoints1.toList();
        List<KeyPoint> keyPoints2 = keypoints2.toList();

        for (int i = 0; i < bestMatches.toList().size(); i++) {
            bestKeyPoints1.add(keyPoints.get(better_matches.get(i).queryIdx));
            bestKeyPoints2.add(keyPoints2.get(better_matches.get(i).trainIdx));
        }
        // TODO Added triangles
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

        // DRAWING OUTPUT
        Mat outputImg = new Mat();
        // this will draw all matches, works fine
        MatOfDMatch better_matches_mat = new MatOfDMatch();
        better_matches_mat.fromList(bestMatches.toList());
        Features2d.drawMatches(img1, keypoints1, img2, keypoints2, bestMatches, outputImg);
        // save image
        Imgcodecs.imwrite("result.jpg", outputImg);

        System.out.println("Згенеровано КТ1: " + keypoints1.toArray().length);
        System.out.println("Згенеровано КТ2: " + keypoints2.toArray().length);
        System.out.println("Спільних точок: " + better_matches.size());
        System.out.println("The same triangles: " + counter);
    }

    public static Mat resizeImage(Mat sourse, Size newSize) {
        Mat resizeMat = new Mat();
        resize(sourse, resizeMat, newSize);

        return resizeMat;
    }

}
