package seamcarving;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dynamic programming implementation of the {@link SeamFinder} interface.
 *
 * @see SeamFinder
 * @see SeamCarver
 */
public class DynamicProgrammingSeamFinder implements SeamFinder {
    @Override
    public List<Integer> findVerticalSeam(double[][] energies) {
        double[][] table = new double[energies.length][energies[0].length];
        Map<Point, Point> predecessor = new HashMap<>();
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < energies.length; i++) {
            table[i][0] = energies[i][0];
        }
        double min;
        Point leastEnd = new Point();
        int yLeast = Integer.MAX_VALUE;
        double shortestDist = Double.POSITIVE_INFINITY;

        for (int j = 1; j < energies[0].length; j++) {
            for (int i = 0; i < energies.length; i++) {
                if (i == 0) { // top
                    Point location = new Point(i, j - 1);
                    min = Math.min(table[i][j - 1], table[i + 1][j - 1]);
                    if (min == table[i + 1][j - 1]) {
                        location.x = i + 1;
                    }
                    table[i][j] = min + energies[i][j];
                    predecessor.put(new Point(i, j), location);
                } else if (i == energies.length - 1) { // bottom
                    Point location = new Point(i, j - 1);
                    min = Math.min(table[i][j - 1], table[i - 1][j - 1]);
                    if (min == table[i - 1][j - 1]) {
                        location.x = i - 1;
                    }
                    table[i][j] = min + energies[i][j];
                    predecessor.put(new Point(i, j), location);
                } else { // else
                    Point location = new Point(i, j - 1);
                    min = Math.min(table[i][j - 1], table[i - 1][j - 1]);
                    if (min == table[i - 1][j - 1]) {
                        location.x = i - 1;
                    }
                    min = Math.min(min, table[i + 1][j - 1]);
                    if (min == table[i + 1][j - 1]) {
                        location.x = i + 1;
                    }
                    table[i][j] = min + energies[i][j];
                    predecessor.put(new Point(i, j), location);
                }
                if (j == energies[0].length - 1 && Math.min(shortestDist, table[i][j]) == table[i][j]) {
                    shortestDist = table[i][j];
                    leastEnd = new Point(i, j);
                    yLeast = i;
                }
            }
        }
        result.add(yLeast);
        for (int i = 1; i < energies[0].length; i++) {
            Point pred = predecessor.get(leastEnd);
            result.add(pred.x);
            leastEnd = pred;
        }
        Collections.reverse(result);

        return result;
    }

    @Override
    public List<Integer> findHorizontalSeam(double[][] energies) {
        double[][] table = new double[energies.length][energies[0].length];
        Map<Point, Point> predecessor = new HashMap<>();
        List<Integer> result = new ArrayList<>();

        for (int j = 0; j < energies[0].length; j++) {
            table[0][j] = energies[0][j];
        }
        double min;
        Point leastEnd = new Point();
        int yLeast = Integer.MAX_VALUE;
        double shortestDist = Double.POSITIVE_INFINITY;

        for (int i = 1; i < energies.length; i++) {
            for (int j = 0; j < energies[0].length; j++) {
                if (j == 0) { // left
                    Point location = new Point(i - 1, j);
                    min = Math.min(table[i - 1][j], table[i - 1][j + 1]);
                    if (min == table[i - 1][j + 1]) {
                        location.y = j + 1;
                    }
                    table[i][j] = min + energies[i][j];
                    predecessor.put(new Point(i, j), location);
                } else if (j == energies[0].length - 1) { // right
                    Point location = new Point(i - 1, j);
                    min = Math.min(table[i - 1][j], table[i - 1][j - 1]);
                    if (min == table[i - 1][j - 1]) {
                        location.y = j - 1;
                    }
                    table[i][j] = min + energies[i][j];
                    predecessor.put(new Point(i, j), location);
                } else { // else
                    Point location = new Point(i - 1, j);
                    min = Math.min(table[i - 1][j], table[i - 1][j - 1]);
                    if (min == table[i - 1][j - 1]) {
                        location.y = j - 1;
                    }
                    min = Math.min(min, table[i - 1][j + 1]);
                    if (min == table[i - 1][j + 1]) {
                        location.y = j + 1;
                    }
                    table[i][j] = min + energies[i][j];
                    predecessor.put(new Point(i, j), location);
                }
                if (i == energies.length - 1 && Math.min(shortestDist, table[i][j]) == table[i][j]) {
                    shortestDist = table[i][j];
                    leastEnd = new Point(i, j);
                    yLeast = j;
                }
            }
        }
        result.add(yLeast);
        for (int i = 1; i < energies.length; i++) {
            Point pred = predecessor.get(leastEnd);
            result.add(pred.y);
            leastEnd = pred;
        }
        Collections.reverse(result);

        return result;
    }
}
