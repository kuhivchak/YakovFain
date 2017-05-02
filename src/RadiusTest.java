import org.opencv.core.Point;

import static org.junit.Assert.*;

/**
 * Created by Vladik on 01.05.2017.
 */
public class RadiusTest {

    public static void main(String[] args) {
        Radius.A = new Point(2, 6);
        Radius.B = new Point(4, 6);
        Radius radius = new Radius(new Point(4,8));
        System.out.println(radius.kut);
    }

}