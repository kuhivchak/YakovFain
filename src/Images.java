import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

import java.io.Serializable;

/**
 *
 */
public class Images implements Serializable {
    private byte[] image;
    private int imageColumns;
    private int imageRows;
    private int imageType;
    private float[] keyPoints;
    private int keyPointsColumns;
    private int keyPointsRows;
    private int keyPointsType;
    private byte[] descriptor;
    private int descriptorColumns;
    private int descriptorRows;
    private int descriptorType;

    public Images(Mat image, MatOfKeyPoint keyPoints, Mat descriptor) {
        this.image = matToBytes(image);
        this.imageColumns = image.cols();
        this.imageRows = image.rows();
        this.imageType = image.type();

        this.keyPoints = matOfKeyPointsToBytes(keyPoints);
        this.keyPointsColumns = keyPoints.cols();
        this.keyPointsRows = keyPoints.rows();
        this.keyPointsType = keyPoints.type();

        this.descriptor = matToBytes(descriptor);
        this.descriptorColumns = descriptor.cols();
        this.descriptorRows = descriptor.rows();
        this.descriptorType = descriptor.type();
    }

    private float[] matOfKeyPointsToBytes(MatOfKeyPoint keyPoints) {
        float[] buffer = new float[keyPoints.rows() * 7];
        keyPoints.get(0, 0, buffer);

        return buffer;
    }

    private byte[] matToBytes(Mat mat) {
        int length = (int) (mat.total() * mat.elemSize());
        byte buffer[] = new byte[length];
        mat.get(0, 0, buffer);

        return buffer;
    }

    private MatOfKeyPoint bytesToMatOfKeyPoints(float[] keyPoints, int rows, int columns, int type) {
        MatOfKeyPoint keyPoint = new MatOfKeyPoint();
        keyPoint.create(rows, columns, type);
        keyPoint.put(0, 0, keyPoints);

        return keyPoint;
    }

    public MatOfKeyPoint getKeyPoints() {
        return bytesToMatOfKeyPoints(keyPoints,keyPointsRows,keyPointsColumns,keyPointsType);
    }

    public Mat getImage() {
        return bytesToMat(image, imageRows, imageColumns, imageType);
    }

    public Mat getDescriptor() {
        return bytesToMat(descriptor, descriptorRows, descriptorColumns, descriptorType);
    }

    private Mat bytesToMat(byte[] buffer, int rows, int columns, int type) {
        Mat mat = new Mat(rows, columns, type);
        mat.put(0, 0, buffer);

        return mat;
    }
}
