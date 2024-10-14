import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class ExpenseTrackerLL {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTrackGUI::new);
    }
}


class ExpenseTrackGUI extends JFrame {
    // GUI components
    private JTextField budgetField, expenseField; // Text fields for budget and expense input
    private JButton submitBudgetButton, submitExpenseButton, clearButton, stopButton, viewPastExpensesButton;
    private JTextArea infoArea; // Text area to display budget info
    private JComboBox<String> categoryComboBox; // dropdown box for selecting expense category
    private JList<String> pastExpensesList; //  display past expenses list
    private DefaultListModel<String> pastExpensesModel; // Model for the past expenses list
    private JScrollPane pastExpensesScrollPane; // Scroll pane for the past expenses list
    private double budget, expenseInFood, expenseInClothes, expenseInTransportation, expenseInSkincare, expenseInUtilities, expenseInOthers;
    private final Map<String, Integer> categoryMap = new HashMap<>();
    private final ArrayList<String> pastExpenses = new ArrayList<>();

    public ExpenseTrackGUI() {
        initializeComponents();
        setLayout(new BorderLayout()); // Set layout manager
        add(createInputPanel(), BorderLayout.NORTH); // Add input panel
        add(createInfoPanel(), BorderLayout.CENTER); // Add info panel
        add(createPastExpensesPanel(), BorderLayout.WEST); // Add past expenses panel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set close operation
        setTitle("Expense Tracker"); // Set window title
        pack(); // Pack components
        setLocationRelativeTo(null); // Center the window
        setVisible(true); // Make the window visible
    }

// initialize components for the program and set colors for buttons and text areas
    private void initializeComponents() {
        budgetField = new JTextField(10);
        expenseField = new JTextField(10);
        categoryComboBox = new JComboBox<>();

        categoryMap.put("Food", 1);
        categoryMap.put("Clothes", 2);
        categoryMap.put("Transportation", 3);
        categoryMap.put("Skincare", 4);
        categoryMap.put("Utilities", 5);
        categoryMap.put("Others", 6);

        categoryComboBox.addItem("Food");
        categoryComboBox.addItem("Clothes");
        categoryComboBox.addItem("Transportation");
        categoryComboBox.addItem("Skincare");
        categoryComboBox.addItem("Utilities");
        categoryComboBox.addItem("Others");

        submitBudgetButton = new JButton("Submit Budget");
        submitExpenseButton = new JButton("Submit Expense");
        clearButton = new JButton("Clear");
        stopButton = new JButton("Exit");
        viewPastExpensesButton = new JButton("View Past Expenses");
        infoArea = new JTextArea(10, 30);
        infoArea.setEditable(false);
        pastExpensesModel = new DefaultListModel<>();
        pastExpensesList = new JList<>(pastExpensesModel);
        pastExpensesScrollPane = new JScrollPane(pastExpensesList);

        // Set colors
        submitBudgetButton.setBackground(new Color(0, 150, 136));
        submitBudgetButton.setForeground(Color.WHITE);
        submitExpenseButton.setBackground(new Color(0, 150, 136));
        submitExpenseButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(255, 87, 34));
        clearButton.setForeground(Color.WHITE);
        stopButton.setBackground(new Color(76, 175, 80));
        stopButton.setForeground(Color.WHITE);
        viewPastExpensesButton.setBackground(new Color(33, 150, 243));
        viewPastExpensesButton.setForeground(Color.WHITE);
        infoArea.setBackground(new Color(255, 255, 255));
        pastExpensesList.setBackground(new Color(255, 255, 255));

        submitBudgetButton.addActionListener(e -> handleBudgetSubmission());
        submitExpenseButton.addActionListener(e -> handleExpenseSubmission());
        clearButton.addActionListener(e -> clearFields());
        stopButton.addActionListener(e -> stopProgram());
        viewPastExpensesButton.addActionListener(e -> showPastExpenses());
    }
//panels for user input and display buttons
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(255, 255, 255));
        inputPanel.add(new JLabel("Budget:"));
        inputPanel.add(budgetField);
        inputPanel.add(submitBudgetButton);
        inputPanel.add(new JLabel("Expense:"));
        inputPanel.add(expenseField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryComboBox);
        inputPanel.add(submitExpenseButton);
        inputPanel.add(clearButton);
        inputPanel.add(stopButton);
        return inputPanel;
    }
// panel to display "Details" for remaining budget + expenses in each category
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(255, 255, 255));
        infoPanel.add(new JLabel("Details:"));
        infoPanel.add(new JScrollPane(infoArea));
        return infoPanel;
    }
// panel to display expenses history
    private JPanel createPastExpensesPanel() {
        JPanel pastExpensesPanel = new JPanel(new BorderLayout());
        pastExpensesPanel.setBackground(new Color(255, 255, 255));
        pastExpensesPanel.add(new JLabel("History"), BorderLayout.NORTH);
        pastExpensesPanel.add(pastExpensesScrollPane, BorderLayout.CENTER);
        pastExpensesPanel.add(viewPastExpensesButton, BorderLayout.SOUTH);
        return pastExpensesPanel;
    }

// error handling for budget and expense input, when user enter invalid/negative values
    private void handleBudgetSubmission() {
        try {
            budget = Double.parseDouble(budgetField.getText());
            if (budget <= 0) {
                throw new IllegalArgumentException("Budget must be a positive number.");
            }
            displayBudgetInfo();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for budget. Please enter a valid number.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // error handling for expenses input + update total amount of expenses to each category
    private void handleExpenseSubmission() {
        try {
            double expense = Double.parseDouble(expenseField.getText());
            String category = (String) categoryComboBox.getSelectedItem();
            int categoryId = categoryMap.get(category);
            if (expense <= 0 || categoryId < 1 || categoryId > 6) {
                throw new IllegalArgumentException("Expense must be a positive number.");
            }
            if (expense > budget) {
                throw new IllegalArgumentException("You have exceeded your current budget.");
            }
            switch (categoryId) {
                case 1 -> expenseInFood += expense;
                case 2 -> expenseInClothes += expense;
                case 3 -> expenseInTransportation += expense;
                case 4 -> expenseInSkincare += expense;
                case 5 -> expenseInUtilities += expense;
                case 6 -> expenseInOthers += expense;
            }
            budget -= expense; // subtract expenses from remaining budget
            addPastExpense(category, expense); //add expense to past expenses list
            displayBudgetInfo();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for expense. Please enter a valid number.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void displayBudgetInfo() {
        String info = String.format("""
                Remaining Budget: %.2f$
                Expense in Food: %.2f$
                Expense in Clothes: %.2f$
                Expense in Transportation: %.2f$
                Expense in Skincare: %.2f$
                Expense in Utilities: %.2f$
                Expense in Others: %.2f$""",
                budget, expenseInFood, expenseInClothes, expenseInTransportation, expenseInSkincare, expenseInUtilities, expenseInOthers);
        infoArea.setText(info);
    }
// clear fields after user input
    private void clearFields() {

        budgetField.setText("");
        expenseField.setText("");
        infoArea.setText("");
        expenseInFood = 0;
        expenseInClothes = 0;
        expenseInTransportation = 0;
        expenseInSkincare = 0;
        expenseInUtilities = 0;
        expenseInOthers = 0;
        pastExpensesModel.clear();

    }
// when user click "Stop" button, show closing message and exit the program
    private void stopProgram() {
        JOptionPane.showMessageDialog(this, "Thanks for using our application.");
        System.exit(0);
    }
// add expenses inputs to the past-expense list
    private void addPastExpense(String category, double expense) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(date);
        pastExpenses.add(formattedDate + " - " + category + ": $" + expense);
        pastExpensesModel.addElement(formattedDate + " - " + category + ": $" + expense);
    }
 // display past expenses lists in separate window
    private void showPastExpenses() {
        JOptionPane.showMessageDialog(this, pastExpensesScrollPane, "Past Expenses", JOptionPane.PLAIN_MESSAGE);
    }
}

