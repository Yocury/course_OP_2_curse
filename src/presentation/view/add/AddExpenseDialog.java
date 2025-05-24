package presentation.view.add;

import data.DBManager;
import javax.swing.*;
import java.awt.*;

public class AddExpenseDialog extends JDialog {
    private final DBManager db;
    private JTextField descriptionField;
    private JComboBox<String> typeComboBox;
    private JTextField amountField;
    private JTextField dateField;
    private boolean approved = false;

    public AddExpenseDialog(JFrame parent) {
        super(parent, "Добавление нового расхода", true);
        this.db = new DBManager();
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        // Создаем компоненты
        descriptionField = new JTextField(20);
        amountField = new JTextField(20);
        dateField = new JTextField(20);
        String[] types = {"Персонал", "Реклама", "Аренда", "Бухгалтерия", "Техника"};
        typeComboBox = new JComboBox<>(types);

        // Добавляем компоненты с метками
        addLabelAndField("Описание:", descriptionField, gbc, 0);
        addLabelAndField("Тип:", typeComboBox, gbc, 1);
        addLabelAndField("Сумма:", amountField, gbc, 2);
        addLabelAndField("Дата:", dateField, gbc, 3);

        // Кнопки
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Отмена");

        okButton.addActionListener(e -> {
            if (validateFields()) {
                approved = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void addLabelAndField(String labelText, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        add(field, gbc);
    }

    private boolean validateFields() {
        if (descriptionField.getText().trim().isEmpty()) {
            showError("Введите описание");
            return false;
        }
        if (amountField.getText().trim().isEmpty()) {
            showError("Введите сумму");
            return false;
        }
        if (dateField.getText().trim().isEmpty()) {
            showError("Введите дату");
            return false;
        }

        try {
            Long.parseLong(amountField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Сумма должна быть числом");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isApproved() {
        return approved;
    }

    public String getDescription() {
        return descriptionField.getText().trim();
    }

    public String getExpenseType() {
        return (String) typeComboBox.getSelectedItem();
    }

    public String getAmount() {
        return amountField.getText().trim();
    }

    public String getDate() {
        return dateField.getText().trim();
    }
} 