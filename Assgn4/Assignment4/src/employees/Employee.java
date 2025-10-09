package employees;

import employees.Employee.Intern;
import employees.Employee.Manager;
import workplace.Workplace;

public class Employee {
    private static int numberOfEmployees = 0;
    private int id;
    private String name;

    public Employee(String name) {
        this.name = name;
        this.id = ++numberOfEmployees;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void work() {
        System.out.println("Employee " + name + " is doing work.");
    }

    public void displayInfo() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return "Employee[ID=" + id + ", Name=" + name + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Employee)) return false;
        Employee other = (Employee) obj;
        return this.id == other.id && this.name.equals(other.name);
    }


    
    
// Manager subclass
public static class Manager extends Employee {
    private int teamSize;

    public Manager(String name, int teamSize) {
        super(name);
        this.teamSize = teamSize;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    @Override
    public void work() {
        System.out.println("Manager " + getName() + " is managing a team of " + teamSize + ".");
    }

    @Override
    public void displayInfo() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return "Manager[ID=" + getId() + ", Name=" + getName() + ", Team Size=" + teamSize + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof Manager)) return false;
        Manager other = (Manager) obj;
        return this.teamSize == other.teamSize;
    }
}



// Engineer subclass
public static class Engineer extends Employee {
	
    private String specialty;

    public Engineer(String name, String specialty) {
        super(name);
        this.specialty = specialty;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
    public void work() {
        System.out.println("Engineer " + getName() + " is working on " + specialty + ".");
    }

    @Override
    public void displayInfo() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return "Engineer[ID=" + getId() + ", Name=" + getName() + ", Specialty=" + specialty + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof Engineer)) return false;
        Engineer other = (Engineer) obj;
        return this.specialty.equals(other.specialty);
    }
}




// Intern subclass
public static class Intern extends Employee {
    private String university;

    public Intern(String name, String university) {
        super(name);
        this.university = university;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    @Override
    public void work() {
        System.out.println("Intern " + getName() + " is interning from " + university + ".");
    }

    @Override
    public void displayInfo() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return "Intern[ID=" + getId() + ", Name=" + getName() + ", University=" + university + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof Intern)) return false;
        Intern other = (Intern) obj;
        return this.university.equals(other.university);
    }
    
  
    
  }




// ------------ Part I deliverable) ------------
public static void main(String[] args) {
    Manager m1 = new Manager("Alice", 5);
    Employee e1 = new Employee("Bob");
    Employee e2 = new Employee("Charlie");
    Intern i1 = new Intern("David", "NYU");
    Intern i2 = new Intern("Eva", "Columbia");
    Engineer l1 = new Engineer ("Rohan", "Micro SaaS");

    System.out.println("--- Employees Created ---");
    m1.displayInfo();
    e1.displayInfo();
    e2.displayInfo();
    i1.displayInfo();
    i2.displayInfo();
    l1.displayInfo();

    System.out.println("\n--- Work Outputs ---");
    m1.work();
    e1.work();
    e2.work();
    i1.work();
    i2.work();
    l1.work();
	

	}



}

