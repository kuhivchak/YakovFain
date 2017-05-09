package image;

import org.opencv.core.Point;

/**
 *
 */
public class Radius {
    public static Point A;
    public static Point B;
    public Point C;
    public int kut;

    public Radius(Point searchedPoint) {
        this.C = searchedPoint;
        Point AB = new Point(B.x - A.x, B.y - A.y);
        Point AC = new Point(C.x - A.x, C.y - A.y);
        kut = (int) (57.2957795 * Math.acos(((AB.x * AC.x + AB.y * AC.y) / (Math.sqrt(Math.pow(AB.x, 2) + Math.pow(AB.y, 2)) *
                Math.sqrt(Math.pow(AC.x, 2) + Math.pow(AC.y, 2))))) + 1);
        if (kut > 180) {
            kut = 360 - kut;
        }
    }


}
