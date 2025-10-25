## Detailed Implementation Plan - ContactBookManager

### Current State Analysis

**What You Have:**

- ✅ Basic JFrame structure extending JFrame
- ✅ Table model with 6 columns: Name, Street, City, State, Phone, Email
- ✅ JTable and DefaultTableModel initialized
- ✅ JFileChooser skeleton in `saveContacts()` and `loadContacts()`
- ✅ Example dialog method (`showADialog()`)
- ✅ DEFAULT_DIR constant for file operations

**Critical Issues in Current Code:**

- ❌ Constructor doesn't set up any UI components
- ❌ Main method creates a NEW JFrame instead of instantiating ContactBookManager
- ❌ No layout manager configured
- ❌ No components added to the frame
- ❌ No window properties set (size, close operation, etc.)
- ❌ saveContacts() and loadContacts() only get filename, don't use full path

---

## Comprehensive Implementation Plan

### **PHASE 1: Fix Main Method & Constructor Setup**

**1.1 Fix main() method:**

- Create instance of ContactBookManager (not generic JFrame)
- Set default close operation to EXIT_ON_CLOSE
- Set appropriate size (e.g., 800x600)
- Center on screen
- Set visible

**1.2 Complete Constructor - Window Setup:**

- Call super() with title (already done ✓)
- Set layout manager (BorderLayout for main content pane)
- Initialize all UI components
- Assemble the layout
- Configure window properties

---

### **PHASE 2: Create All UI Components**

**2.1 Menu Bar (Top of Window):**

- Create `JMenuBar`
- Create `JMenu` named "File"
- Create three `JMenuItem` objects:
  - "Open" → triggers loadContacts()
  - "Save" → triggers saveContacts()
  - "Exit" → triggers System.exit(0) or dispose()
- Add ActionListeners to each menu item
- Attach menu bar to frame using `setJMenuBar()`

**2.2 Input Form Panel (Left Side - WEST):**
Create a panel for the input form with these components:

- **6 JLabel objects** for: "Name:", "Street:", "City:", "State:", "Phone:", "Email:"
- **6 JTextField objects** corresponding to each label
- Use vertical layout (BoxLayout or GridLayout(6, 2))
- This goes in BorderLayout.WEST for "flush left" appearance

**2.3 Button Panel (Below Input Form or Integrated):**
Create a panel with action buttons:

- **"Add"** button → adds current form data to table
- **"Remove"** button → removes selected row from table
- **"Clear"** button → clears all rows from table
- Add ActionListeners to each button
- Could be part of the WEST panel or separate SOUTH panel

**2.4 Table Display (Center/Right Side):**

- Wrap `contactTable` in `JScrollPane` for scrolling
- Add the JScrollPane to BorderLayout.CENTER
- This allows table to expand and fill remaining space

---

### **PHASE 3: Implement Button Functionality**

**3.1 Add Button ActionListener:**

```
Goals:
- Get text from all 6 text fields
- Validate that fields are not empty (or at least name is required)
- Create String array with 6 values
- Call tableModel.addRow(rowData)
- Clear all text fields after successful add
- Show error dialog if validation fails
```

**3.2 Remove Button ActionListener:**

```
Goals:
- Get selected row index from contactTable.getSelectedRow()
- Check if row is selected (index != -1)
- Call tableModel.removeRow(selectedIndex)
- Show error dialog if no row selected
```

**3.3 Clear Button ActionListener:**

```
Goals:
- Call tableModel.setRowCount(0) to remove all rows
- Optionally show confirmation dialog before clearing
- Clear input fields as well
```

---

### **PHASE 4: Implement File Operations**

**4.1 Complete saveContacts() Method:**

```
Current issues:
- Only gets filename, not full path
- No file writing logic

Needs:
1. Get full file path: chooser.getSelectedFile().getAbsolutePath()
2. Add .csv extension if not present
3. Use try-catch for IOException
4. Create BufferedWriter/FileWriter
5. Write CSV header: "Name,Street,City,State,Phone,Email"
6. Loop through tableModel rows:
   - Get row count: tableModel.getRowCount()
   - For each row, get all 6 column values
   - Format as CSV: value1,value2,value3,value4,value5,value6
   - Handle commas in data (wrap in quotes if needed)
7. Close writer
8. Show success dialog with JOptionPane
9. Show error dialog if exception occurs
```

**4.2 Complete loadContacts() Method:**

```
Current issues:
- Only gets filename, not full path
- No file reading logic

Needs:
1. Get full file path: chooser.getSelectedFile().getAbsolutePath()
2. Use try-catch for IOException
3. Create BufferedReader/FileReader
4. Clear existing table data: tableModel.setRowCount(0)
5. Read first line (header) and skip it
6. Loop through remaining lines:
   - Split by comma: line.split(",")
   - Handle quoted fields if implemented in save
   - Create String array with 6 values
   - Call tableModel.addRow(rowData)
7. Close reader
8. Show success dialog with row count
9. Show error dialog if:
   - File not found
   - Invalid format (wrong number of columns)
   - Parse error
```

---

### **PHASE 5: Layout Configuration for Extra Credit**

**5.1 Main Layout Structure (BorderLayout):**

```
Frame (BorderLayout):
├── WEST: Input form panel with labels and text fields
├── CENTER: JScrollPane containing JTable
└── (Optional) SOUTH: Button panel if not in WEST
```

**5.2 WEST Panel Layout (Input Form):**

```
Options:
A. BoxLayout (vertical) - simple stacking
B. GridLayout(7, 2) - 7 rows (6 fields + buttons), 2 columns (label, textfield)
C. GridBagLayout - more control but complex

Recommendation: GridLayout(7, 2) with buttons in last row
- Gives clean label-field pairs
- Automatically aligns everything
- Easy to implement
```

**5.3 Panel Borders and Spacing:**

```
- Add titled borders to panels: BorderFactory.createTitledBorder("Contact Entry")
- Add padding: BorderFactory.createEmptyBorder(10, 10, 10, 10)
- Combine borders: BorderFactory.createCompoundBorder()
```

**5.4 Component Sizing:**

```
- Set preferred size for text fields: textField.setPreferredSize()
- Set column widths for table (optional): contactTable.getColumnModel().getColumn(i).setPreferredWidth()
- Set panel preferred sizes if needed
```

---

### **PHASE 6: Data Validation & Error Handling**

**6.1 Input Validation:**

- Check for empty required fields (at least Name)
- Trim whitespace from inputs
- Validate email format (optional, regex)
- Validate phone format (optional, basic length check)
- Show specific error messages

**6.2 File Operation Error Handling:**

- FileNotFoundException when loading
- IOException during read/write
- Invalid CSV format (wrong column count)
- Empty file handling
- File already exists confirmation (for save)

**6.3 User Feedback:**

- Success messages for save/load operations
- Error dialogs with specific reasons
- Confirmation dialogs for destructive actions (Clear)

---

### **PHASE 7: Helper Methods to Add**

**7.1 clearInputFields():**

```
- Loops through all 6 text fields
- Calls setText("") on each
- Called after successful Add
- Called by Clear button
```

**7.2 validateInput():**

```
- Returns boolean
- Checks if required fields are filled
- Shows error dialog if validation fails
- Called before adding to table
```

**7.3 formatCSVValue(String value):**

```
- Escapes commas in data
- Wraps in quotes if contains comma
- Returns formatted string
- Used when writing CSV
```

**7.4 parseCSVLine(String line):**

```
- Handles quoted fields
- Splits by comma respecting quotes
- Returns String array
- Used when reading CSV
```

---

### **PHASE 8: Instance Variables to Add**

```java
// UI Components - Input Form
private JTextField nameField;
private JTextField streetField;
private JTextField cityField;
private JTextField stateField;
private JTextField phoneField;
private JTextField emailField;

// UI Components - Buttons
private JButton addButton;
private JButton removeButton;
private JButton clearButton;

// UI Components - Panels
private JPanel inputPanel;
private JPanel buttonPanel;
private JScrollPane tableScrollPane;

// Menu Components
private JMenuBar menuBar;
private JMenu fileMenu;
private JMenuItem openItem;
private JMenuItem saveItem;
private JMenuItem exitItem;
```

---

## Implementation Order (Recommended)

### **Step-by-Step Execution Order:**

1. **Fix main() and basic window setup** (Phase 1)

   - Get window displaying properly first
2. **Create menu bar** (Phase 2.1)

   - Test File menu and Exit functionality
3. **Create input form panel** (Phase 2.2)

   - Build and test layout in WEST
4. **Add table to center** (Phase 2.4)

   - Verify scrolling works
5. **Create button panel** (Phase 2.3)

   - Add to WEST or SOUTH
6. **Implement Add button** (Phase 3.1)

   - Test adding data to table
7. **Implement Remove button** (Phase 3.2)

   - Test removing selected rows
8. **Implement Clear button** (Phase 3.3)

   - Test clearing table
9. **Implement saveContacts()** (Phase 4.1)

   - Test CSV writing
10. **Implement loadContacts()** (Phase 4.2)

    - Test CSV reading
11. **Add validation and error handling** (Phase 6)

    - Polish user experience
12. **Fine-tune layout for extra credit** (Phase 5)

    - Match video exactly

---

## Key Technical Considerations

### **CSV Format:**

```csv
Name,Street,City,State,Phone,Email
John Doe,123 Main St,Springfield,IL,555-1234,john@email.com
Jane Smith,456 Oak Ave,Portland,OR,555-5678,jane@email.com
```

### **Handling Commas in Data:**

If a field contains a comma, wrap in quotes:

```csv
Name,Street,City,State,Phone,Email
"Doe, John",123 Main St,Springfield,IL,555-1234,john@email.com
```

### **File Path Issue to Fix:**

```java
// Current (WRONG):
String fileName = chooser.getSelectedFile().getName(); // Only "contacts.csv"

// Corrected (RIGHT):
String filePath = chooser.getSelectedFile().getAbsolutePath(); // Full path: "/Users/.../contacts.csv"
```

### **Table Operations:**

```java
// Add row
tableModel.addRow(new Object[]{"John", "123 St", "City", "ST", "555-1234", "john@email.com"});

// Remove row
tableModel.removeRow(rowIndex);

// Clear all
tableModel.setRowCount(0);

// Get row count
int count = tableModel.getRowCount();

// Get cell value
String value = (String) tableModel.getValueAt(row, column);
```

---

## Summary: What Needs to Be Added

**Critical (Must Have):**

1. Fix main() method instantiation
2. Complete constructor with UI assembly
3. Create all 6 input text fields with labels
4. Create Add/Remove/Clear buttons with functionality
5. Create File menu with Open/Save/Exit
6. Implement CSV writing in saveContacts()
7. Implement CSV reading in loadContacts()
8. Add JScrollPane around table
9. Fix file path retrieval in file operations

**Important (Should Have):**
10. Input validation
11. Error handling with try-catch
12. Clear input fields after add
13. Confirmation dialogs
14. Success/error messages

**Polish (Extra Credit):**
15. Exact layout matching video (BorderLayout WEST)
16. Proper spacing and borders
17. Professional appearance

This plan gives you a complete roadmap. Ready to start coding when you are!
