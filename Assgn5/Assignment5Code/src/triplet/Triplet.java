//Collaborator - Claude

package triplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Part III: Generic numeric Triplet<A,B,C> with getters/setters, toString,
 * magnitude, and Comparable by magnitude. (Professor's text says "Vector";
 * keeping your file/class name Triplet to match your structure.)
 *
 * Run: java triplet.Triplet
 */

public class Triplet<A extends Number, B extends Number, C extends Number>
        implements Comparable<Triplet<A, B, C>> {

    private A first;
    private B second;
    private C third;

    public Triplet(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public A getFirst()  { return first; }
    public void setFirst(A first) { this.first = first; }
    public B getSecond() { return second; }
    public void setSecond(B second) { this.second = second; }
    public C getThird()  { return third; }
    public void setThird(C third) { this.third = third; }

    
    
    /** Euclidean magnitude using Number.doubleValue() */
    public double magnitude() {
        double x = first.doubleValue();
        double y = second.doubleValue();
        double z = third.doubleValue();
        // numerically stable magnitude
        return Math.hypot(Math.hypot(x, y), z);
    }

    
    
    @Override
    public int compareTo(Triplet<A, B, C> o) {
        return Double.compare(this.magnitude(), o.magnitude());
    }

    @Override
    public String toString() {
        return String.format("Triplet{first=%s, second=%s, third=%s, |v|=%.4f}",
                first, second, third, magnitude());
    }

    
    
    
//    IMMP
    public static void main(String[] args) {
        List<Triplet<Double, Double, Double>> list = new ArrayList<>();
        Random rng = new Random();

        for (int i = 0; i < 8; i++) {
            double a = -10 + 20 * rng.nextDouble();

            double b = -10 + 20 * rng.nextDouble();
            double c = -10 + 20 * rng.nextDouble();
            list.add(new Triplet<>(a, b, c));
        }

        System.out.println("Before sort:");
        for (Triplet<Double, Double, Double> t : list) System.out.println(t);

        list.sort(null); // uses Comparable<Triplet<?>>
        System.out.println("\nAfter sort (ascending by magnitude):");
        for (Triplet<Double, Double, Double> t : list) System.out.println(t);
    }
}
