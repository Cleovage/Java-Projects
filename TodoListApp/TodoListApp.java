import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class TodoListApp extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField taskField;
    private JTextField tagField;
    private JComboBox<String> priorityCombo;
    private JTextField searchField;
    private JLabel statusLabel;

    public TodoListApp() {
        setTitle("Modern To-Do List Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Use Nimbus look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Enhanced table model with more columns
        tableModel = new DefaultTableModel(new Object[] { "Task", "Priority", "Tags", "Created", "Completed" }, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 4) ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only completion checkbox is editable
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(100, 149, 237));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(60, 60, 65));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(45, 45, 48));
        table.getTableHeader().setForeground(new Color(240, 240, 240));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 65)));

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(300); // Task
        table.getColumnModel().getColumn(1).setPreferredWidth(80); // Priority
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Tags
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Created
        table.getColumnModel().getColumn(4).setPreferredWidth(80); // Completed

        // Add sorter for search functionality
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 65)), "Tasks",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(240, 240, 240)));
        scrollPane.setBackground(new Color(45, 45, 48));
        scrollPane.getViewport().setBackground(new Color(60, 60, 65));

        // Create modern input panel
        createInputPanel();

        // Create control panel
        createControlPanel();

        // Create search panel
        createSearchPanel(); // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(240, 240, 240));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(45, 45, 48)); // Main layout
        setLayout(new BorderLayout(10, 10));
        add(createTopPanel(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        // Add some padding and set background
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().setBackground(new Color(45, 45, 48));

        updateStatusLabel();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(new Color(45, 45, 48));
        topPanel.add(createSearchPanel(), BorderLayout.NORTH);
        topPanel.add(createInputPanel(), BorderLayout.CENTER);
        return topPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(45, 45, 48));
        bottomPanel.add(createControlPanel(), BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        return bottomPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(60, 60, 65));
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 65)), "Search & Filter",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(240, 240, 240)));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBackground(new Color(45, 45, 48));
        searchField.setForeground(new Color(240, 240, 240));
        searchField.setCaretColor(new Color(100, 149, 237));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 65)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        JButton searchButton = createModernButton("Search", new Color(100, 149, 237));
        JButton clearSearchButton = createModernButton("Clear", new Color(255, 152, 0));

        searchButton.addActionListener(e -> performSearch());
        clearSearchButton.addActionListener(e -> clearSearch());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(new Color(240, 240, 240));
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);

        return searchPanel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(60, 60, 65));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 65)), "Add New Task",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(240, 240, 240)));
        GridBagConstraints gbc = new GridBagConstraints();

        // Task field
        taskField = new JTextField(25);
        taskField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taskField.setBackground(new Color(45, 45, 48));
        taskField.setForeground(new Color(240, 240, 240));
        taskField.setCaretColor(new Color(100, 149, 237));
        taskField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 65)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        // Tag field
        tagField = new JTextField(15);
        tagField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagField.setBackground(new Color(45, 45, 48));
        tagField.setForeground(new Color(240, 240, 240));
        tagField.setCaretColor(new Color(100, 149, 237));
        tagField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 65)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        // Priority combo
        priorityCombo = new JComboBox<>(new String[] { "Low", "Medium", "High", "Urgent" });
        priorityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        priorityCombo.setBackground(new Color(45, 45, 48));
        priorityCombo.setForeground(new Color(240, 240, 240));

        // Add button
        JButton addButton = createModernButton("+ Add Task", new Color(76, 175, 80));
        addButton.addActionListener(e -> addTask());

        // Create labels with dark text
        JLabel taskLabel = new JLabel("Task:");
        taskLabel.setForeground(new Color(240, 240, 240));
        taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setForeground(new Color(240, 240, 240));
        priorityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel tagsLabel = new JLabel("Tags:");
        tagsLabel.setForeground(new Color(240, 240, 240));
        tagsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Layout components
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(taskLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(taskField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        inputPanel.add(priorityLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        inputPanel.add(priorityCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(tagsLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(tagField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(addButton, gbc);

        // Add Enter key listener to task field
        taskField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addTask();
                }
            }
        });

        return inputPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(new Color(60, 60, 65));
        controlPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 65)), "Actions",
                0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(240, 240, 240)));

        JButton editButton = createModernButton("Edit Task", new Color(100, 149, 237));
        JButton deleteButton = createModernButton("Delete Task", new Color(244, 67, 54));
        JButton completeAllButton = createModernButton("Complete All", new Color(76, 175, 80));
        JButton deleteCompletedButton = createModernButton("Clear Completed", new Color(255, 152, 0));

        editButton.addActionListener(e -> editTask());
        deleteButton.addActionListener(e -> deleteTask());
        completeAllButton.addActionListener(e -> markAllComplete());
        deleteCompletedButton.addActionListener(e -> deleteCompletedTasks());

        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(completeAllButton);
        controlPanel.add(deleteCompletedButton);

        return controlPanel;
    }

    private JButton createModernButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add curved edges to buttons
        button.putClientProperty("JButton.buttonType", "roundRect");
        // For Java Swing, we need to set a custom border to get rounded corners
        int radius = 15; // Adjust the radius to control the curve amount
        button.setBorder(new RoundedBorder(radius, backgroundColor));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
                // Update border color on hover
                button.setBorder(new RoundedBorder(radius, backgroundColor.brighter()));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
                // Restore original border color
                button.setBorder(new RoundedBorder(radius, backgroundColor));
            }
        });

        return button;
    }

    // Custom border class for rounded buttons
    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(x, y, width, height, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 12, 6, 12);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 12;
            insets.top = insets.bottom = 6;
            return insets;
        }
    }

    private void addTask() {
        String task = taskField.getText().trim();
        String tags = tagField.getText().trim();
        String priority = (String) priorityCombo.getSelectedItem();

        if (!task.isEmpty()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
            tableModel.addRow(new Object[] { task, priority, tags, timestamp, false });

            // Clear input fields
            taskField.setText("");
            tagField.setText("");
            priorityCombo.setSelectedIndex(0);

            updateStatusLabel();
            // Show confirmation
            statusLabel.setText("Task added successfully!");
            javax.swing.Timer timer = new javax.swing.Timer(3000, e -> updateStatusLabel());
            timer.setRepeats(false);
            timer.start();
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a task description.", "Empty Task",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editTask() {
        int selected = table.getSelectedRow();
        if (selected >= 0) {
            int modelRow = table.convertRowIndexToModel(selected);
            String currentTask = (String) tableModel.getValueAt(modelRow, 0);
            String currentPriority = (String) tableModel.getValueAt(modelRow, 1);
            String currentTags = (String) tableModel.getValueAt(modelRow, 2); // Create edit dialog
            JDialog editDialog = new JDialog(this, "Edit Task", true);
            editDialog.setLayout(new GridBagLayout());
            editDialog.getContentPane().setBackground(new Color(45, 45, 48));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            JTextField editTaskField = new JTextField(currentTask, 25);
            editTaskField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            editTaskField.setBackground(new Color(60, 60, 65));
            editTaskField.setForeground(new Color(240, 240, 240));
            editTaskField.setCaretColor(new Color(100, 149, 237));
            editTaskField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 65)),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)));

            JTextField editTagField = new JTextField(currentTags, 15);
            editTagField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            editTagField.setBackground(new Color(60, 60, 65));
            editTagField.setForeground(new Color(240, 240, 240));
            editTagField.setCaretColor(new Color(100, 149, 237));
            editTagField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 65)),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)));

            JComboBox<String> editPriorityCombo = new JComboBox<>(new String[] { "Low", "Medium", "High", "Urgent" });
            editPriorityCombo.setSelectedItem(currentPriority);
            editPriorityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            editPriorityCombo.setBackground(new Color(60, 60, 65));
            editPriorityCombo.setForeground(new Color(240, 240, 240));

            // Create labels with dark text
            JLabel taskLabelEdit = new JLabel("Task:");
            taskLabelEdit.setForeground(new Color(240, 240, 240));
            JLabel priorityLabelEdit = new JLabel("Priority:");
            priorityLabelEdit.setForeground(new Color(240, 240, 240));
            JLabel tagsLabelEdit = new JLabel("Tags:");
            tagsLabelEdit.setForeground(new Color(240, 240, 240));

            gbc.gridx = 0;
            gbc.gridy = 0;
            editDialog.add(taskLabelEdit, gbc);
            gbc.gridx = 1;
            editDialog.add(editTaskField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            editDialog.add(priorityLabelEdit, gbc);
            gbc.gridx = 1;
            editDialog.add(editPriorityCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            editDialog.add(tagsLabelEdit, gbc);
            gbc.gridx = 1;
            editDialog.add(editTagField, gbc);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(new Color(45, 45, 48));
            JButton saveButton = createModernButton("Save", new Color(76, 175, 80));
            JButton cancelButton = createModernButton("Cancel", new Color(255, 152, 0));

            saveButton.addActionListener(e -> {
                String updatedTask = editTaskField.getText().trim();
                if (!updatedTask.isEmpty()) {
                    tableModel.setValueAt(updatedTask, modelRow, 0);
                    tableModel.setValueAt(editPriorityCombo.getSelectedItem(), modelRow, 1);
                    tableModel.setValueAt(editTagField.getText().trim(), modelRow, 2);
                    editDialog.dispose();
                    updateStatusLabel();
                }
            });

            cancelButton.addActionListener(e -> editDialog.dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            editDialog.add(buttonPanel, gbc);

            editDialog.pack();
            editDialog.setLocationRelativeTo(this);
            editDialog.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to edit.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteTask() {
        int selected = table.getSelectedRow();
        if (selected >= 0) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this task?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                int modelRow = table.convertRowIndexToModel(selected);
                tableModel.removeRow(modelRow);
                updateStatusLabel();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void markAllComplete() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(true, i, 4);
        }
        updateStatusLabel();
    }

    private void deleteCompletedTasks() {
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            if ((Boolean) tableModel.getValueAt(i, 4)) {
                tableModel.removeRow(i);
            }
        }
        updateStatusLabel();
    }

    private void performSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void clearSearch() {
        searchField.setText("");
        sorter.setRowFilter(null);
    }

    private void updateStatusLabel() {
        int total = tableModel.getRowCount();
        int completed = 0;
        for (int i = 0; i < total; i++) {
            if ((Boolean) tableModel.getValueAt(i, 4)) {
                completed++;
            }
        }
        statusLabel.setText(String.format("Total: %d | Completed: %d | Remaining: %d",
                total, completed, total - completed));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TodoListApp app = new TodoListApp();
            app.setVisible(true);
        });
    }
}
