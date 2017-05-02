import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 */
public class Triangle implements Comparable {
    private ArrayList<Integer> sides;
    public final int AB;
    public final int BC;
    public final int AC;

    public Triangle(Point A, Point B, Point C) {
        sides = new ArrayList<>();
        sides.add((int)(Math.sqrt((Math.pow((B.x - A.x), 2)) + (Math.pow((B.y - A.y), 2)))));
        sides.add((int)(Math.sqrt((Math.pow((C.x - B.x), 2)) + (Math.pow((C.y - B.y), 2)))));
        sides.add((int)(Math.sqrt((Math.pow((C.x - A.x), 2)) + (Math.pow((C.y - A.y), 2)))));
        Collections.sort(sides);
        AB = sides.get(2);
        BC = sides.get(1);
        AC = sides.get(0);
    }

    @Override
    public int compareTo(Object o) {
        Triangle t = (Triangle) o;
        try {
            if (this.AB >= t.AB) {
                return (((double)this.AB / t.AB == (double)this.AC / t.AC) && ((double)this.AB / t.AB == (double)this.BC / t.BC)) ? 0 : 1;
            } else {
                return (((double)t.AB / this.AB == (double)t.AC / this.AC) && ((double)t.AB / this.AB == (double)t.BC / this.BC)) ? 0 : 1;
            }
        } catch (ArithmeticException e) {
            return -1;
        }
    }
}
