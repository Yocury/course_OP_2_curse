package presentation.view.add;

import domain.entities.Batch;
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
    private boolean isEditMode = false;
    private String[] statusOptions;
    private Batch editingBatch = null;

    public AddBatchDialog(JFrame parent) {
        this(parent, null);
    }


    public AddBatchDialog(JFrame parent, Batch batchToEdit) {
        super(parent, batchToEdit == null ? "Добавление новой партии" : "Редактирование партии", true);
        this.addBatchUseCase = presentation.MyConfig.instance().addBatchUseCase();
        this.isEditMode = batchToEdit != null;
        this.editingBatch = batchToEdit;
        if (isEditMode) {
            this.statusOptions = new String[]{"Отменена", "Продана", "В продаже"};
        } else {
            this.statusOptions = new String[]{"В продаже", "В обработке"};
        }
        setupUI();
        if (isEditMode) {
            populateFields();
        }
    }

    private void populateFields() {
        if (editingBatch == null) return;

        providerField.setText(editingBatch.getProvider());
        amountField.setText(String.valueOf(editingBatch.getAmount()));
        dateField.setText(editingBatch.getDate());
        countField.setText(String.valueOf(editingBatch.getCount()));

        // Set status
        for (int i = 0; i < statusComboBox.getItemCount(); i++) {
            if (statusComboBox.getItemAt(i).equals(editingBatch.getStatus())) {
                statusComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Disable non-editable fields based on CheckForEdit rules
        // ID, amount, and date are not editable
        amountField.setEnabled(false);
        dateField.setEnabled(false);
    }

    public void setStatusOptions(String[] statusOptions) {
        this.statusOptions = statusOptions;
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        // Title label
        JLabel titleLabel = new JLabel(isEditMode ? "Редактирование партии" : "Добавление новой партии", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Create components
        providerField = new JTextField(20);
        amountField = new JTextField(20);
        dateField = new JTextField(20);
        countField = new JTextField(20);
        statusComboBox = new JComboBox<>(statusOptions);

        // Add components with labels
        addLabelAndField("Поставщик:", providerField, gbc, 1);
        addLabelAndField("Стоимость:", amountField, gbc, 2);
        addLabelAndField("Статус:", statusComboBox, gbc, 3);
        addLabelAndField("Дата:", dateField, gbc, 4);
        addLabelAndField("Количество:", countField, gbc, 5);

        // Buttons
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
        gbc.gridy = 6;
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