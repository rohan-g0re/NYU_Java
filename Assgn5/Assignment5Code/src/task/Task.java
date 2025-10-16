//Collaborator - Claude

package task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Public abstract Task with nested concrete types and Comparable by time.
 * Also contains a small demo main that creates/sorts a few tasks.
 */


public abstract class Task implements Comparable<Task> {
    private String module;
    private int time;

    public Task(String module, int time) {
        this.module = module;
        this.time = time;
    }

    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }

    
    
    /** Each subclass prints an action line including the module name. */
    public abstract void performTask();

    /** Natural order: by time ascending; then by subclass name; then by module. */
    @Override
    public int compareTo(Task other) {
        int c = Integer.compare(this.time, other.time);
        if (c != 0) return c;
        c = this.getClass().getSimpleName().compareTo(other.getClass().getSimpleName());
        if (c != 0) return c;
        return this.module.compareTo(other.module);
    }
    
    

    @Override
    public String toString() {
        return String.format("%s{module=%s, time=%d}",
                this.getClass().getSimpleName(), module, time);
    }
    

    /** ===== IMPORTANT --> Concrete task types (public so other packages can use them) ===== */
    
    
    public static final class DevelopModule extends Task {
        public DevelopModule(String module, int time) { super(module, time); }
        @Override public void performTask() {
            System.out.println("Developing Module " + getModule());
        }
    }

    public static final class DocumentModule extends Task {
        public DocumentModule(String module, int time) { super(module, time); }
        @Override public void performTask() {
            System.out.println("Documenting Module " + getModule());
        }
    }

    
    
    public static final class TestModule extends Task {
        public TestModule(String module, int time) { super(module, time); }
        @Override public void performTask() {
            System.out.println("Testing module " + getModule()); // wording per sheet
        }
    }

    
    
    /** Small self-test for Part II (optional). Run: java task.Task */
    public static void main(String[] args) {
        
    	List<Task> jobQueue = new ArrayList<>();
        
    	
    	jobQueue.add(new DevelopModule("1001", 7));
        jobQueue.add(new DocumentModule("2002", 3));
        jobQueue.add(new TestModule("3003", 5));

        System.out.println("Unsorted:");
        jobQueue.forEach(System.out::println);

        jobQueue.sort(null); // uses Comparable<Task>
        System.out.println("\nSorted by time:");
        jobQueue.forEach(System.out::println);

        System.out.println("\nPerforming:");
        
        
        
        
        
        int total = 0;
        for (Task t : jobQueue) {
            t.performTask();
            total += t.getTime();
        }
        System.out.println("\nTotal time = " + total);

        
        
        
        
        
        // Add a couple rand0ms to see variety 
        
        for (int i = 0; i < 2; i++) {
            String mod = Integer.toString(ThreadLocalRandom.current().nextInt(1000, 10000));
            int tm = ThreadLocalRandom.current().nextInt(1, 11);
            jobQueue.add(new DevelopModule(mod, tm));
        }
        jobQueue.sort(null);
        System.out.println("\nAfter adding random tasks, resorted:");
        jobQueue.forEach(System.out::println);
    }
}
