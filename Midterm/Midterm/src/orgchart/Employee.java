//COLLABORATOR - Claude 4.5

package orgchart;

import java.util.*;



public abstract class Employee {

	// ----------------------------------------------------------------------------------
    // All method definations --> did this all in abstract class becasue we willl be using objects of Manager & IC only
    // ----------------------------------------------------------------------------------
    
	private static int numberOfEmployees = 0; // auto-id counter

    private final int id;
    private final String name;
    private final String title;
    private final int baseSalary;

    private Employee manager;                 // nullable for CEO
    private final ArrayList<Employee> directs; // never null; empty for ICs

    
    
    // Constructor
    
    protected Employee(String name, String title, int baseSalary,
                       Employee manager, ArrayList<Employee> directsIn) {
        this.id = ++numberOfEmployees;           // IDs start at 1
        this.name = requireNonBlank(name, "name");
        this.title = requireNonBlank(title, "title");
        if (baseSalary <= 0) throw new IllegalArgumentException("baseSalary must be > 0");
        this.baseSalary = baseSalary;
        this.manager = manager;                  
        

        // Defensive copy to avoid aliasing (matches assignment guidance)
        this.directs = (directsIn == null) ? new ArrayList<>() : new ArrayList<>(directsIn);

        
        
        // this is MAYBE just a good practice
        
        if (this.directs.contains(null))
            throw new IllegalArgumentException("directs cannot contain null");
        HashSet<Employee> uniq = new HashSet<>(this.directs);
        if (uniq.size() != this.directs.size())
            throw new IllegalArgumentException("directs contains duplicates");
        if (this.directs.contains(this))
            throw new IllegalArgumentException("an employee cannot manage themselves");
    }

    
    // ----------------------------------------------------------------------------------
    // Abstract behaviors required by the spec
    // ----------------------------------------------------------------------------------
    public abstract int getHeadCount();
    public abstract int getTotalComp();

    // Getters / utilities (UML methods)

    public final int getId() { return id; }
    public final String getName() { return name; }
    public final String getTitle() { return title; }
    public final int getBaseSalary() { return baseSalary; }
    public final Employee getManager() { return manager; }

    
    // Expose a copy to satisfy return type (ArrayList<Employee>) while preventing external mutation
    public final ArrayList<Employee> getDirects() { return new ArrayList<>(directs); }

    
    
    /**
     * As per new additions in the Code structure --> addDirect(Employee e): void
     * 
     * Adds a direct report under THIS employee. Only valid if THIS is a Manager.
     * Maintains bidirectional consistency and prevents cycles.
     */
    
    
    public final void addDirect(Employee e) {
        Objects.requireNonNull(e, "direct cannot be null");
        if (!(this instanceof Manager))
            throw new IllegalStateException("Only managers can have directs");
        if (e == this)
            throw new IllegalArgumentException("self cannot be a direct");
        
        
        // Prevent adding an ancestor beneath a descendant
        if (isAncestor(e, this))
            throw new IllegalArgumentException("cycle would be created: cannot add an ancestor as a direct");
        // No-op if already present
        if (directs.contains(e)) return;
        // Detach from old manager, attach to this
        if (e.manager != null) {
            e.manager.directs.remove(e);
        }
        e.manager = this;
        directs.add(e);
    }

    /**
     * As per new additions in the Code structure --> changeManager(Employee e): void
     * 
     * Re-parents THIS employee under the given new manager (or null to make CEO).
     */
    
    public final void changeManager(Employee newManager) {
        if (newManager == this)
            throw new IllegalArgumentException("an employee cannot manage themselves");
        if (newManager != null && !(newManager instanceof Manager))
            throw new IllegalArgumentException("new manager must be a Manager");
        // Prevent cycles: newManager cannot be a descendant of THIS
        if (newManager != null && isAncestor(this, newManager))
            throw new IllegalArgumentException("cycle would be created by reparenting");
        // Remove from current manager's list
        if (this.manager != null) {
            this.manager.directs.remove(this);
        }
        // Set new manager and update their directs list
        this.manager = newManager;
        if (newManager != null && !newManager.directs.contains(this)) {
            newManager.directs.add(this);
        }
    }

    
    /** Returns true if 'possibleAncestor' is on the manager-chain above 'node'. */
    static boolean isAncestor(Employee possibleAncestor, Employee node) {
        Employee cur = node.getManager();
        while (cur != null) {
            if (cur == possibleAncestor) return true;
            cur = cur.getManager();
        }
        return false;
    }

    
    /** UML: pathToCEO(): String — returns THIS → ... → CEO (inclusive). */
    
    public final String pathToCEO() {
        List<String> parts = new ArrayList<>();
        Employee cur = this;
        while (cur != null) {
            parts.add(cur.getName() + " [#" + cur.getId() + "]");
            cur = cur.getManager();
        }
        return String.join(" – ", parts);
    }

    @Override public String toString() {
        return String.format(Locale.US,
                "Employee{id=#%d, name='%s', title='%s', base=%d, managerId=%s, directs=%d}",
                id, name, title, baseSalary,
                (manager == null ? "null" : ("#" + manager.getId())), directs.size());
    }

    @Override public boolean equals(Object o) { return (o instanceof Employee) && ((Employee)o).id == id; }
    @Override public int hashCode() { return Integer.hashCode(id); }

    
    // ----------------------------------------------------------------------------------
    // Org-wide validation (kept here to stay single-file)
    // ----------------------------------------------------------------------------------
    
    public static boolean validate(Collection<Employee> everyone) {
        Objects.requireNonNull(everyone, "everyone");
        if (everyone.isEmpty()) throw new IllegalStateException("no employees provided");

        // 1) Unique root (CEO)
        List<Employee> roots = new ArrayList<>();
        for (Employee e : everyone) if (e.getManager() == null) roots.add(e);
        if (roots.size() != 1)
            throw new IllegalStateException("there must be exactly one root (CEO); found " + roots.size());
        Employee root = roots.get(0);

        // 2) Reachability + cycle detection via DFS coloring
        Map<Employee, Color> color = new IdentityHashMap<>();
        for (Employee e : everyone) color.put(e, Color.WHITE);
        int visited = dfs(root, color);
        if (visited != everyone.size())
            throw new IllegalStateException("unreachable employees exist from root");

        // 3) Parent↔child consistency + IC constraint
        for (Employee e : everyone) {
            if (e instanceof IndividualContributor) {
                if (!e.getDirects().isEmpty())
                    throw new IllegalStateException("IC has directs: #" + e.getId());
            }
            if (e.getManager() != null && !e.getManager().getDirects().contains(e))
                throw new IllegalStateException("manager does not list employee #" + e.getId() + " as a direct");
            // duplicates sanity check
            HashSet<Employee> uniq = new HashSet<>(e.getDirects());
            if (uniq.size() != e.getDirects().size())
                throw new IllegalStateException("duplicates in directs for #" + e.getId());
        }
        return true;
    }
    
    
    
//    VERY IMPORTANT --> GRAPH TRAVERSAL

    private static int dfs(Employee u, Map<Employee, Color> color) {
        color.put(u, Color.GRAY);
        int count = 1;
        for (Employee v : u.getDirects()) {
            Color c = color.get(v);
            if (c == Color.GRAY) throw new IllegalStateException("cycle detected involving #" + v.getId());
            if (c == Color.WHITE) count += dfs(v, color);
        }
        color.put(u, Color.BLACK);
        return count;
    }

    private enum Color { WHITE, GRAY, BLACK }

    private static String requireNonBlank(String s, String field) {
        if (s == null) throw new IllegalArgumentException(field + " must not be null");
        if (s.trim().isEmpty()) throw new IllegalArgumentException(field + " must not be blank");
        return s;
    }

    
    
    
    
    
    
    
    
    
    public static void main(String[] args) {
        // Create ICs first (manager null; will be attached by Managers)
        IndividualContributor terrance = new IndividualContributor(
                "Terrance Ng", "Senior Engineer", 225_000, null);
        IndividualContributor sarah = new IndividualContributor(
                "Sarah Blinken", "Engineer", 230_000, null);
        IndividualContributor raymond = new IndividualContributor(
                "Raymond Zhang", "Director of Sales", 265_000, null);

        // VP John with directs Terrance & Sarah (manager set via addDirect)
        Manager john = new Manager("John Vice", "VP", 250_000, null,
                new ArrayList<>(Arrays.asList(terrance, sarah)));

        // CEO Bob with directs John & Raymond; sets their manager to Bob
        Manager bob = new Manager("Bob Ceo", "CEO", 350_000, null,
                new ArrayList<>(Arrays.asList(john, raymond)));

        List<Employee> everyone = Arrays.asList(terrance, sarah, raymond, john, bob);

        // Print a compact table
        System.out.println("ID  NAME                        (TITLE)               BASE     HEADCOUNT   TOTAL-COMP            PATH-TO-CEO");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------");
        for (Employee e : everyone) {
            String row = String.format(Locale.US,
                    "#%-2d %-24s (%-18s) %8d %11d %14d   %s",
                    e.getId(), e.getName(), e.getTitle(), e.getBaseSalary(),
                    e.getHeadCount(), e.getTotalComp(), e.pathToCEO());
            System.out.println(row);
        }

        boolean ok = Employee.validate(everyone);
        System.out.println("validate(all) => " + ok);
    }
}






final class IndividualContributor extends Employee {
    public IndividualContributor(String name, String title, int baseSalary, Employee manager) {
        super(name, title, baseSalary, manager, new ArrayList<>());
    }
    @Override public int getHeadCount() { return 1; }
    @Override public int getTotalComp() { return getBaseSalary(); }
}






final class Manager extends Employee {
    // Build with an explicit list of directs; we will defensively copy and attach using addDirect()
    public Manager(String name, String title, int baseSalary, Employee manager, ArrayList<Employee> directsIn) {
        super(name, title, baseSalary, manager, new ArrayList<>()); // start empty to avoid aliasing
        if (directsIn != null) {
            for (Employee d : directsIn) {
                // Use canonical API to enforce invariants & backlinks
                this.addDirect(d);
            }
        }
    }

    @Override public int getHeadCount() {
        int sum = 1; // self
        for (Employee d : getDirects()) sum += d.getHeadCount();
        return sum;
    }

    @Override public int getTotalComp() {
        int sum = getBaseSalary();
        for (Employee d : getDirects()) sum += d.getTotalComp();
        return sum;
    }
}
