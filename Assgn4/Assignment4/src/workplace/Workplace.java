package workplace;
import employees.Employee;
import employees.Employee.Manager;
import employees.Employee.Engineer;
import employees.Employee.Intern;

// Now all subclass references are valid!

//import employees.*;
import java.util.ArrayList;

public class Workplace {
    private String name;
    private ArrayList<Employee> employees;

    public Workplace(String name) {
        this.name = name;
        this.employees = new ArrayList<>();
    }

    public void addEmployee(Employee e) {
        employees.add(e);
    }

    
    
    public Employee removeEmployee (Employee e) {
    	if (employees.remove(e)) {
    		return e;
    	}
    	return null;
    }
    
    
    
    

    public int numEmployees() {
        return employees.size();
    }

    
    
    public void listEmployees() {
        System.out.println("Employee list for " + name + ":");
        for (Employee e : employees) {
            e.displayInfo();
        }
    }

    
    
    public void processWorkday() {
        System.out.println("Processing workday at " + name + ":");
        for (Employee e : employees) {
            e.work();
        }
    }

    @Override
    public String toString() {
        return "Workplace[" + name + "] with " + employees.size() + " employees.";
    }
    
    
    
    

    public static void main(String[] args) {
    	
    	
        // Created same employees as given in part1
        
    	
    	Manager m1 = new Manager("Alice", 5);
        Employee e1 = new Employee("Bob");
        Employee e2 = new Employee("Charlie");
        Intern i1 = new Intern("David", "NYU");
        Intern i2 = new Intern("Eva", "Columbia");
        Engineer l1 = new Engineer ("Rohan", "Micro Saas");



        Workplace company = new Workplace("TechCorp");

        company.addEmployee(m1);
        company.addEmployee(e1);
        company.addEmployee(e2);
        company.addEmployee(i1);
        company.addEmployee(i2);
        company.addEmployee(l1);

        System.out.println("--- Executing listEmployees() ---");
        company.listEmployees();

        System.out.println();

        System.out.println("--- Executing processWorkday() ---");
        company.processWorkday();
        
        
        System.out.println("\nRemoving employee: " + company.removeEmployee(e1));
        System.out.println("Updated count = " + company.numEmployees());
        
//        System.out.println();
//        System.out.println("List after deleting --> Bob has been FIRED!");
//        System.out.println();
//
//        
//        company.listEmployees();

    }
}