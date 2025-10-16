//Collaborator - Claude

package person;

import task.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Part I driver: abstract Person with three concrete roles that enqueue tasks
 * onto a shared, parameterized jobQueue<ArrayList<Task>>. Demonstrates:
 *  - creating one of each Person type
 *  - generating 5 random tasks per person (4-digit module, time 1..10)
 *  - sorting (Part II) and printing
 *  - performing tasks and printing total time
 *
 * Run:  java person.Person
 */

public abstract class Person {
    protected final List<Task> jobQueue;

    public Person(List<Task> jobQueue) {
        this.jobQueue = jobQueue;
    }

    public abstract void startTask(String moduleName, int time);

    
    
    /** ===== Concrete people who enqueue the correct Task subtype ===== */
    public static final class Developer extends Person {
        public Developer(List<Task> jobQueue) { super(jobQueue); }
        @Override public void startTask(String moduleName, int time) {
            jobQueue.add(new Task.DevelopModule(moduleName, time));
        }
    }

    
    
    public static final class Tester extends Person {
        public Tester(List<Task> jobQueue) { super(jobQueue); }
        @Override public void startTask(String moduleName, int time) {
            jobQueue.add(new Task.TestModule(moduleName, time));
        }
    }
    
    

    public static final class TechWriter extends Person {
        public TechWriter(List<Task> jobQueue) { super(jobQueue); }
        @Override public void startTask(String moduleName, int time) {
            jobQueue.add(new Task.DocumentModule(moduleName, time));
        }
    }

    
    
    
    /** ===== Helper RNGs  ===== */
    private static final Random RNG = new Random();
    private static String random4Digit() {
        int n = 1000 + RNG.nextInt(9000); // wchih means [ 1000 ,9999]
        return Integer.toString(n);
    }
    private static int randomTime() {
        return 1 + RNG.nextInt(10); // [1..10]
    }

    
    
    
    
    
    
    
    
    
    /** ===== Part I/II Demo ===== */
    public static void main(String[] args) {
        List<Task> jobQueue = new ArrayList<>();

        Person dev  = new Developer(jobQueue);
        Person test = new Tester(jobQueue);
        Person tw   = new TechWriter(jobQueue);

        // 5 random tasks per role
        
        
        for (Person p : new Person[]{dev, test, tw}) {
            for (int i = 0; i < 5; i++) {
                p.startTask(random4Digit(), randomTime());
            }
        }

        System.out.println("=== Unsorted jobQueue ===");
        for (Task t : jobQueue) System.out.println(t);

        
        
//        ----------
        
        
        
        
        // Part II: sort by time using Task's Comparable implementation
        jobQueue.sort(null);
        System.out.println("\n=== Sorted by time (Part II) ===");
        for (Task t : jobQueue) System.out.println(t);

        // Perform and total time (Part I)
        int totalTime = 0;
        System.out.println("\n=== Performing tasks (Part I) ===");
        for (Task t : jobQueue) {
            t.performTask();
            totalTime += t.getTime();
        }
        System.out.println("\nTotal time to process all tasks = " + totalTime);
    }
}