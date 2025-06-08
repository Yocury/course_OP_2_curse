package presentation.view.add;

import domain.entities.Expenses;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;
import domain.entities.Batch;
import domain.usecases.batche.GetAllBatchUseCases;
import presentation.MyConfig;

public class AddExpenseDialog extends JDialog {
    private JTextField descriptionField;
    private JComboBox<String> typeComboBox;
    private JTextField amountField;
    private JTextField dateField;
    private JComboBox<BatchItem> batchComboBox;
    private boolean approved = false;
    private boolean isEditMode = false;
    private Expenses editingExpense = null;
    private Batch selectedBatch = null;
    private final GetAllBatchUseCases getAllBatchUseCases;

    public AddExpenseDialog(JFrame parent) {
        this(parent, null);
    }

    public AddExpenseDialog(JFrame parent, Expenses expenseToEdit) {
        super(parent, expenseToEdit == null ? "Добавление нового расхода" : "Редактирование расхода", true);
        this.isEditMode = expenseToEdit != null;
        this.editingExpense = expenseToEdit;
        this.getAllBatchUseCases = MyConfig.instance().getAllBatchUseCases();
        setupUI();
        if (isEditMode) {
            populateFields();
        }
    }

    private void populateFields() {
        if (editingExpense == null) return;

        descriptionField.setText(editingExpense.getDescription());
        amountField.setText(String.valueOf(editingExpense.getAmount()));
        dateField.setText(editingExpense.getDate());

        // Set type
        for (int i = 0; i < typeComboBox.getItemCount(); i++) {
            if (typeComboBox.getItemAt(i).equals(editingExpense.getType())) {
                typeComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Найти и выбрать партию
        List<Batch> batches = getAllBatchUseCases.invoke();
        for (Batch batch : batches) {
            if (batch.getId() == editingExpense.getBatchId()) {
                for (int i = 0; i < batchComboBox.getItemCount(); i++) {
                    BatchItem item = batchComboBox.getItemAt(i);
                    if (item.getBatch().getId() == batch.getId()) {
                        batchComboBox.setSelectedIndex(i);
                        selectedBatch = batch;
                        break;
                    }
                }
                break;
            }
        }

        // Disable non-editable fields based on CheckForEdit rules
        // ID, type, date и batchComboBox не редактируются в режиме редактирования
        typeComboBox.setEnabled(false);
        dateField.setEnabled(false);
        batchComboBox.setEnabled(false);
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        // Title label
        JLabel titleLabel = new JLabel(isEditMode ? "Редактирование расхода" : "Добавление нового расхода", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Получаем список партий в продаже или в обработке
        List<Batch> batches = getAllBatchUseCases.invoke();
        Vector<BatchItem> batchItems = new Vector<>();
        for (Batch batch : batches) {
            if (batch.getStatus().equalsIgnoreCase("В продаже") || batch.getStatus().equalsIgnoreCase("В обработке")) {
                batchItems.add(new BatchItem(batch));
            }
        }

        // Создаем выпадающий список партий
        batchComboBox = new JComboBox<>(batchItems);
        // Устанавливаем обработчик выбора партии только для режима добавления
        if (!isEditMode) {
            batchComboBox.addActionListener(e -> {
                BatchItem selectedItem = (BatchItem) batchComboBox.getSelectedItem();
                if (selectedItem != null) {
                    selectedBatch = selectedItem.getBatch();
                    setFieldsEnabled(true); // Активируем поля ввода после выбора партии
                } else {
                    selectedBatch = null;
                    setFieldsEnabled(false); // Отключаем поля, если партия не выбрана
                }
            });
        }

        // Создаем компоненты
        descriptionField = new JTextField(20);
        typeComboBox = new JComboBox<>(new String[]{"Персонал", "Реклама", "Аренда", "Бухгалтерия", "Техника"});
        amountField = new JTextField(20);
        dateField = new JTextField(20);

        // Добавляем компоненты с метками
        addLabelAndField("Партия:", batchComboBox, gbc, 1); // Добавляем выбор партии
        addLabelAndField("Описание:", descriptionField, gbc, 2);
        addLabelAndField("Тип:", typeComboBox, gbc, 3);
        addLabelAndField("Сумма:", amountField, gbc, 4);
        addLabelAndField("Дата:", dateField, gbc, 5);

        // Изначально делаем поля ввода неактивными для нового заказа
        if (!isEditMode) {
            setFieldsEnabled(false);
        }

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
        if (selectedBatch == null && !isEditMode) {
            showError("Выберите партию");
            return false;
        }
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

    public Batch getSelectedBatch() {
        return selectedBatch;
    }

    private void setFieldsEnabled(boolean enabled) {
        descriptionField.setEnabled(enabled);
        typeComboBox.setEnabled(enabled);
        amountField.setEnabled(enabled);
        dateField.setEnabled(enabled);
        batchComboBox.setEnabled(enabled);
    }

    public void setSelectedBatch(int id) {
        List<Batch> batches = getAllBatchUseCases.invoke();
        for(Batch batch : batches) {
            if(batch.getId() == id)
            {
                this.selectedBatch = batch;
            };
        }
    }

    // Вспомогательный класс для отображения партий в выпадающем списке
    private static class BatchItem {
        private final Batch batch;

        public BatchItem(Batch batch) {
            this.batch = batch;
        }

        public Batch getBatch() {
            return batch;
        }

        @Override
        public String toString() {
            return "Партия #" + batch.getId() + " - " + batch.getProvider() + 
                   " (доступно: " + batch.getRemainder() + " шт.)";
        }
    }
} 