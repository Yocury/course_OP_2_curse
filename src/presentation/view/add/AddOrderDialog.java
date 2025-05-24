package presentation.view.add;

import domain.entities.Batch;
import domain.usecases.batche.GetAllBatchUseCases;
import domain.usecases.order.AddOrderUseCase;
import domain.usecases.order.GetAllOrderUseCase;
import presentation.MyConfig;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class AddOrderDialog extends JDialog {
    private final GetAllOrderUseCase getAllOrderUseCase;
    private final AddOrderUseCase addOrderUseCase;
    private final GetAllBatchUseCases getAllBatchUseCases;
    private JComboBox<BatchItem> batchComboBox;
    private JTextField sourceField;
    private JTextField countField;
    private JTextField streetField;
    private JTextField buildingField;
    private JTextField dateField;
    private JTextField phoneField;
    private JComboBox<String> statusComboBox;
    private JTextField priceField;
    private boolean approved = false;
    private Batch selectedBatch = null;
    private JLabel avgPriceLabel;

    public AddOrderDialog(JFrame parent) {
        super(parent, "Добавление нового заказа", true);
        //this.db = new DB_manager();
        this.addOrderUseCase = MyConfig.instance().addOrderUseCase();
        this.getAllOrderUseCase = MyConfig.instance().getAllOrderUseCase();
        this.getAllBatchUseCases = MyConfig.instance().getAllBatchUseCases();
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Заголовок для выбора партии
        JLabel titleLabel = new JLabel("Выберите партию к заказу", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Получаем список партий в продаже
        List<Batch> batches = getAllBatchUseCases.invoke();
        Vector<BatchItem> batchItems = new Vector<>();
        
        for (Batch batch : batches) {
            if (batch.getStatus().equalsIgnoreCase("В продаже")) {
                batchItems.add(new BatchItem(batch));
            }
        }

        // Создаем выпадающий список партий
        batchComboBox = new JComboBox<>(batchItems);
        gbc.gridy = 1;
        add(batchComboBox, gbc);

        // Информация о средней цене
        avgPriceLabel = new JLabel("Средняя цена за 1 ед. товара: 0", SwingConstants.CENTER);
        avgPriceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 2;
        add(avgPriceLabel, gbc);

        // Обновляем информацию при выборе партии
        batchComboBox.addActionListener(e -> {
            BatchItem selectedItem = (BatchItem) batchComboBox.getSelectedItem();
            if (selectedItem != null) {
                selectedBatch = selectedItem.getBatch();
                avgPriceLabel.setText(String.format("Средняя цена за 1 ед. товара: %d", selectedBatch.getAvgPrice()));
                // Активируем поля ввода
                setFieldsEnabled(true);
            }
        });

        // Сброс gridwidth
        gbc.gridwidth = 1;

        // Создаем компоненты
        sourceField = new JTextField(20);
        countField = new JTextField(20);
        streetField = new JTextField(20);
        buildingField = new JTextField(20);
        dateField = new JTextField(20);
        phoneField = new JTextField(20);
        priceField = new JTextField(20);
        String[] statusOptions = {"В обработке", "В исполнении", };
        statusComboBox = new JComboBox<>(statusOptions);

        // Добавляем компоненты с метками
        addLabelAndField("Источник:", sourceField, gbc, 3);
        addLabelAndField("Кол-во:", countField, gbc, 4);
        addLabelAndField("Улица:", streetField, gbc, 5);
        addLabelAndField("Дом:", buildingField, gbc, 6);
        addLabelAndField("Дата:", dateField, gbc, 7);
        addLabelAndField("Номер телефона:", phoneField, gbc, 8);
        addLabelAndField("Состояние:", statusComboBox, gbc, 9);
        addLabelAndField("Стоимость за ед.:", priceField, gbc, 10);

        // Изначально делаем поля ввода неактивными
        setFieldsEnabled(false);

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
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void setFieldsEnabled(boolean enabled) {
        sourceField.setEnabled(enabled);
        countField.setEnabled(enabled);
        streetField.setEnabled(enabled);
        buildingField.setEnabled(enabled);
        dateField.setEnabled(enabled);
        phoneField.setEnabled(enabled);
        statusComboBox.setEnabled(enabled);
        priceField.setEnabled(enabled);
    }

    private void addLabelAndField(String labelText, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        add(field, gbc);
    }

    private boolean validateFields() {
        if (selectedBatch == null) {
            showError("Выберите партию");
            return false;
        }
        if (sourceField.getText().trim().isEmpty()) {
            showError("Введите источник");
            return false;
        }
        if (countField.getText().trim().isEmpty()) {
            showError("Введите количество");
            return false;
        }
        if (streetField.getText().trim().isEmpty()) {
            showError("Введите улицу");
            return false;
        }
        if (buildingField.getText().trim().isEmpty()) {
            showError("Введите дом");
            return false;
        }
        if (dateField.getText().trim().isEmpty()) {
            showError("Введите дату");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showError("Введите номер телефона");
            return false;
        }
        if (priceField.getText().trim().isEmpty()) {
            showError("Введите стоимость за единицу");
            return false;
        }

        try {
            int count = Integer.parseInt(countField.getText().trim());
            if (count <= 0) {
                showError("Количество должно быть положительным числом");
                return false;
            }
            if (count > selectedBatch.getCount()) {
                showError("Недостаточно товара в партии");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Количество должно быть числом");
            return false;
        }

        try {
            int price = Integer.parseInt(priceField.getText().trim());
            if (price <= 0) {
                showError("Цена должна быть положительным числом");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Цена должна быть числом");
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

    public Batch getSelectedBatch() {
        return selectedBatch;
    }

    public String getSource() {
        return sourceField.getText().trim();
    }

    public String getCount() {
        return countField.getText().trim();
    }

    public String getStreet() {
        return streetField.getText().trim();
    }

    public String getBuilding() {
        return buildingField.getText().trim();
    }

    public String getDate() {
        return dateField.getText().trim();
    }

    public String getPhone() {
        return phoneField.getText().trim();
    }

    public String getStatus() {
        return (String) statusComboBox.getSelectedItem();
    }

    public String getPrice() {
        return priceField.getText().trim();
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
                   " (доступно: " + batch.getCount() + " шт.)";
        }
    }
} 