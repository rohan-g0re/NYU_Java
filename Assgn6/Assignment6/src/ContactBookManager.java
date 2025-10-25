//COLLABORATORS - Perplexity, Claude, Cursor

/*ASSUMPTIONS:
 * 
 * 1. In video demo after importing the sample csv we can see 2 rows - but the given smaple csv does not have a header row
 * --> AS THAT IS A BAD PRACTICE --> I am assuming that the csv loader should work such that it loads from line 2 
 * --> This happens dues to the code around line 320 <reader.readLine();> 
 * 
 * 
 * 2. The task sheet and video demo DOES NOT suggest if we can have multiple file uploads such that they can append data.
 * --> Hence I am uploading the data such that it CLEARS the current data. 
 * --> Code Snippet Line 317 <tableModel.setRowCount(0);>
 * 
 * 
 * 3. The error boxes which are "j optino panes" are not EXACT as to video, inly because they lack the java logo which I was not able to add in there.
 * 
 * 4. The "Remove Selected" button as shown in the video only works for single select.
 * --> Implemented similarly: You can select multiple on UI, but "remove selected" only deletes one of it.
 * 
 * 
 * 5. ADDITION --> Validation added to phone number adn email field.
 * 
 * */




 


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ContactBookManager extends JFrame {
    private JTable contactTable;
    private DefaultTableModel tableModel;
    
    private static final String DEFAULT_DIR = System.getProperty("user.dir");
    
    // Text fields for contact entry
    private JTextField nameField;
    private JTextField streetField;
    private JTextField cityField;
    private JTextField stateField;
    private JTextField phoneField;
    private JTextField emailField;

    
    public ContactBookManager() {
        super("Contact Book Manager");
        
        // Window setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        
        // Initialize table model and table
        String[] columnNames = {"Name", "Street", "City", "State", "Phone", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable - other option we had was to apply validation to cell edits as well
            }
        };
        contactTable = new JTable(tableModel);
        
        
        // Build UI components
        createMenuBar();
        createFormPanel();
        createTablePanel();
        createButtonPanel();
    }
    
    
    
    
    
    
    
    // Create menu bar with File menu
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> loadContacts());
        
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveContacts());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        setJMenuBar(menuBar);
    }
    
    
    
    
    // Create form panel with input fields
    private void createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(2, 6, 5, 5));
        
        // Initialize text fields
        nameField = new JTextField(10);
        streetField = new JTextField(10);
        cityField = new JTextField(10);
        stateField = new JTextField(10);
        phoneField = new JTextField(10);
        emailField = new JTextField(10);
        
        // Row 1: Name, Street, City
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Street:"));
        formPanel.add(streetField);
        formPanel.add(new JLabel("City:"));
        formPanel.add(cityField);
        
        // Row 2: State, Phone, Email
        formPanel.add(new JLabel("State:"));
        formPanel.add(stateField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        
        add(formPanel, BorderLayout.NORTH);
    }
    
    
    
    // Create table panel with scrolling
    private void createTablePanel() {
        JScrollPane scrollPane = new JScrollPane(contactTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    // Create button panel at bottom
    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton addButton = new JButton("Add Contact");
        addButton.addActionListener(e -> addContact());
        
        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeSelected());
        
        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> clearAll());
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // Add contact to table
    private void addContact() {
        String name = nameField.getText().trim();
        String street = streetField.getText().trim();
        String city = cityField.getText().trim();
        String state = stateField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        
        // Validate that name is not empty
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Name is required!", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate phone number format and length (if provided)
        if (!phone.isEmpty() && !isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this, 
                "Phone number must be exactly 10 digits and may contain dashes, spaces, or parentheses.\nExample: 555-123-4567", 
                "Invalid Phone Number", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate email format (if provided)
        if (!email.isEmpty() && !isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid email address.\nExample: user@example.com", 
                "Invalid Email", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Add row to table
        tableModel.addRow(new Object[]{name, street, city, state, phone, email});
        
        // Clear all fields
        clearFields();
    }
    
    // Validate phone number format
    private boolean isValidPhone(String phone) {
        // Remove common formatting characters
        String digits = phone.replaceAll("[\\s\\-\\(\\)\\.]", "");
        
        // Check if remaining characters are exactly 10 digits
        return digits.matches("\\d{10}");
    }
    
    // Validate email format
    private boolean isValidEmail(String email) {
        // Basic email validation regex
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    // Remove selected contact
    private void removeSelected() {
        int selectedRow = contactTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a contact to remove.", 
                "No Selection", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        tableModel.removeRow(selectedRow);
    }
    
    // Clear all contacts
    private void clearAll() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear all contacts?", 
            "Confirm Clear", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.setRowCount(0);
            clearFields();
        }
    }
    
    // Clear input fields
    private void clearFields() {
        nameField.setText("");
        streetField.setText("");
        cityField.setText("");
        stateField.setText("");
        phoneField.setText("");
        emailField.setText("");
    }
    
    // Save contacts to CSV file
    private void saveContacts() {
        JFileChooser chooser = new JFileChooser(DEFAULT_DIR);
        int returnVal = chooser.showSaveDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filePath = chooser.getSelectedFile().getAbsolutePath();
            
            // Add .csv extension if not present
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                // Write header
                writer.println("Name,Street,City,State,Phone,Email");
                
                // Write data rows
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String name = csvEscape((String) tableModel.getValueAt(i, 0));
                    String street = csvEscape((String) tableModel.getValueAt(i, 1));
                    String city = csvEscape((String) tableModel.getValueAt(i, 2));
                    String state = csvEscape((String) tableModel.getValueAt(i, 3));
                    String phone = csvEscape((String) tableModel.getValueAt(i, 4));
                    String email = csvEscape((String) tableModel.getValueAt(i, 5));
                    
                    writer.println(name + "," + street + "," + city + "," + 
                                   state + "," + phone + "," + email);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Successfully saved " + tableModel.getRowCount() + " contact(s).", 
                    "Save Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving file: " + ex.getMessage(), 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Load contacts from CSV file
    private void loadContacts() {
        JFileChooser chooser = new JFileChooser(DEFAULT_DIR);
        int returnVal = chooser.showOpenDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filePath = chooser.getSelectedFile().getAbsolutePath();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                // Clear existing data
                tableModel.setRowCount(0);
                
                // Skip header line
                reader.readLine();
                
                // Read data lines
                String line;
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    List<String> fields = parseCsvLine(line);
                    
                    if (fields.size() == 6) {
                        tableModel.addRow(fields.toArray());
                        count++;
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Successfully loaded " + count + " contact(s).", 
                    "Load Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, 
                    "File not found: " + filePath, 
                    "Load Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error reading file: " + ex.getMessage(), 
                    "Load Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Escape CSV field (wrap in quotes if contains comma or quote)
    private String csvEscape(String s) {
        if (s == null) return "";
        
        boolean needsQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        
        if (needsQuotes) {
            String escaped = s.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        
        return s;
    }
    
    // Parse CSV line handling quoted fields
    private List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (inQuotes) {
                if (c == '\"') {
                    // Check for escaped quote
                    if (i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                        current.append('\"');
                        i++; // Skip next quote
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == ',') {
                    fields.add(current.toString());
                    current.setLength(0);
                } else if (c == '\"') {
                    inQuotes = true;
                } else {
                    current.append(c);
                }
            }
        }
        
        
        
        
        // Add last field
        fields.add(current.toString());
        
        return fields;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ContactBookManager frame = new ContactBookManager();
            frame.setSize(980, 420);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
