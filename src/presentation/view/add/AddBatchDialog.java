package presentation.view.add;

import domain.usecases.batche.AddBatchUseCase;
import presentation.MyConfig;

import javax.swing.*;
import java.awt.*;

public class AddBatchDialog extends JDialog {
    private final AddBatchUseCase addBatchUseCase;
    private JTextField providerField;
    private JTextField amountField;
    private JComboBox<String> statusComboBox;
    private JTextField dateField;
    private JTextField countField;
    private boolean approved = false;

    public AddBatchDialog(JFrame parent) {
        super(parent, "Добавление новой партии", true);
        //this.db = new DB_manager();
        this.addBatchUseCase = MyConfig.instance().addBatchUseCase();
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        // Создаем компоненты
        providerField = new JTextField(20);
        amountField = new JTextField(20);
        dateField = new JTextField(20);
        countField = new JTextField(20);
        String[] statusOptions = {"В обработке", "В продаже"};
        statusComboBox = new JComboBox<>(statusOptions);

        // Добавляем компоненты с метками
        addLabelAndField("Поставщик:", providerField, gbc, 0);
        addLabelAndField("Стоимость:", amountField, gbc, 1);
        addLabelAndField("Статус:", statusComboBox, gbc, 2);
        addLabelAndField("Дата:", dateField, gbc, 3);
        addLabelAndField("Количество:", countField, gbc, 4);

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
        gbc.gridy = 5;
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
        if (providerField.getText().trim().isEmpty()) {
            showError("Введите поставщика");
            return false;
        }
        if (amountField.getText().trim().isEmpty()) {
            showError("Введите стоимость");
            return false;
        }
        if (dateField.getText().trim().isEmpty()) {
            showError("Введите дату");
            return false;
        }
        if (countField.getText().trim().isEmpty()) {
            showError("Введите количество");
            return false;
        }

        try {
            Long.parseLong(amountField.getText().trim());
            Long.parseLong(countField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Стоимость и количество должны быть числами");
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

    public String getProvider() {
        return providerField.getText().trim();
    }

    public String getAmount() {
        return amountField.getText().trim();
    }

    public String getStatus() {
        return (String) statusComboBox.getSelectedItem();
    }

    public String getDate() {
        return dateField.getText().trim();
    }

    public String getCount() {
        return countField.getText().trim();
    }
} 