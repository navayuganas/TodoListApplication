package main.java.com.todo.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import main.java.com.todo.dao.TodoDAO;
import main.java.com.todo.model.Todo;

import java.awt.*;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class TodoGUI extends JFrame {
    private TodoDAO todoDAO;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JCheckBox completedCheckBox;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private DefaultTableModel tableModel;
    private JTable todoTable;
    private JComboBox<String> filterComboBox;

    public TodoGUI() {
        this.todoDAO = new TodoDAO();
        initializeComponents();
        setupLayout();
        loadTodos();
        setupEventListner();
    }

    private void initializeComponents() {
        setTitle("Todo List");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        titleField = new JTextField(20);
        descriptionArea = new JTextArea(5, 20);
        completedCheckBox = new JCheckBox("Completed");

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");

        String[] filterOptions = { "All", "Completed", "Pending" };
        filterComboBox = new JComboBox<>(filterOptions);

        String[] columnNames = { "ID", "Title", "Description", "Completed", "Created At", "Updated At" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        todoTable = new JTable(tableModel);
        todoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Title:");
        inputPanel.add(titleLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(titleField, gbc);

        JLabel descriptionLabel = new JLabel("Description:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(descriptionLabel, gbc);

        gbc.gridx = 1;
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        inputPanel.add(descriptionScrollPane, gbc);

        gbc.gridy = 2;
        inputPanel.add(completedCheckBox, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel filterLabel = new JLabel("Filter:");
        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(inputPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);
        northPanel.add(filterPanel, BorderLayout.NORTH);

        add(northPanel, BorderLayout.NORTH);
        JScrollPane tabJScrollPane = new JScrollPane(todoTable);
        add(tabJScrollPane, BorderLayout.CENTER);

    }

    private void setupEventListner() {
        addButton.addActionListener(
                (e) -> {
                    addTodo();
                });
        updateButton.addActionListener(
                (e) -> {
                    updateTodo();
                });
        deleteButton.addActionListener(
                (e) -> {
                    deleteTodo();
                });
        refreshButton.addActionListener(
                (e) -> {
                    refreshTodo();
                });
        filterComboBox.addActionListener(
                (e) -> {
                    filterTodo();
                });
        todoTable.getSelectionModel().addListSelectionListener(
                (e) -> {
                    if (!e.getValueIsAdjusting()) {
                        loadSelectedTodo();
                    }
                });

    }

    private void addTodo() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completedCheckBox.isSelected();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Title cannot be empty",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setCompleted(completed);
        todo.setCreated_at(LocalDateTime.now());
        todo.setUpdated_at(LocalDateTime.now());
        try {
            todoDAO.addTodo(todo);
            Object[] rowData = { todo.getId(),
                    todo.getTitle(),
                    todo.getDescription(),
                    todo.isCompleted(),
                    todo.getCreated_at(),
                    todo.getUpdated_at()
            };
            tableModel.addRow(rowData);
            titleField.setText("");
            descriptionArea.setText("");
            completedCheckBox.setSelected(false);
            JOptionPane.showMessageDialog(this, "Todo added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding todo: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

    }

    private void updateTodo() {
        Todo todo = new Todo();
        int selectedRow = todoTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a todo to update",
                    "Selection Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        int tableId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String tableTitle = (String) tableModel.getValueAt(selectedRow, 1);
        String tableDescription = (String) tableModel.getValueAt(selectedRow, 2);
        boolean tableCompleted = (Boolean) tableModel.getValueAt(selectedRow, 3);
        LocalDateTime tableCreatedAt = (LocalDateTime) tableModel.getValueAt(selectedRow, 4);
        LocalDateTime tableUpdatedAt = (LocalDateTime) tableModel.getValueAt(selectedRow, 5);

        String uiTitle = titleField.getText().trim();
        String uiDescription = descriptionArea.getText().trim();
        boolean uiCompleted = completedCheckBox.isSelected();

        if (uiTitle.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Title cannot be empty",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (uiDescription.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Description cannot be empty",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tableTitle.equals(uiTitle) &&
                tableDescription.equals(uiDescription) &&
                tableCompleted == uiCompleted) {
            JOptionPane.showMessageDialog(this,
                    "No changes detected to update",
                    "Update Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        todo.setId(tableId);
        todo.setTitle(uiTitle);
        todo.setDescription(uiDescription);
        todo.setCompleted(uiCompleted);
        todo.setCreated_at(tableCreatedAt);
        todo.setUpdated_at(LocalDateTime.now());
        try {
            boolean status = todoDAO.updateTodo(todo);
            if (status) {
                tableModel.setValueAt(todo.getTitle(), selectedRow, 1);
                tableModel.setValueAt(todo.getDescription(), selectedRow, 2);
                tableModel.setValueAt(todo.isCompleted(), selectedRow, 3);
                tableModel.setValueAt(todo.getUpdated_at(), selectedRow, 5);
                JOptionPane.showMessageDialog(this,
                        "Todo updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "Error updating todo",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating todo: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

    }

    private void deleteTodo() {

    }

    private void refreshTodo() {

    }

    private void filterTodo() {

    }

    private void loadSelectedTodo() {
        int selectedRow = todoTable.getSelectedRow();
        if (selectedRow != -1) {
            String title = (String) tableModel.getValueAt(selectedRow, 1);
            String description = (String) tableModel.getValueAt(selectedRow, 2);
            boolean completed = (boolean) tableModel.getValueAt(selectedRow, 3);
            titleField.setText(title);
            descriptionArea.setText(description);
            completedCheckBox.setSelected(completed);
        }

    }

    private void loadTodos() {
        List<Todo> todos;

        try {
            todos = todoDAO.getAllTodos();
            for (Todo todo : todos) {
                Object[] rowData = { todo.getId(),
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.isCompleted(),
                        todo.getCreated_at(),
                        todo.getUpdated_at()
                };
                tableModel.addRow(rowData);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erros fetching todos" + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
