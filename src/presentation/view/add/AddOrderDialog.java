package presentation.view.add;

import domain.entities.Batch;
import domain.entities.Order;
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
    private boolean isEditMode = false;
    private Order editingOrder = null;
    private String[] statusOptions;

    public AddOrderDialog(JFrame parent) {
        this(parent, null);
    }

    public AddOrderDialog(JFrame parent, Order orderToEdit) {
        super(parent, orderToEdit == null ? "Добавление нового заказа" : "Редактирование заказа", true);
        this.addOrderUseCase = presentation.MyConfig.instance().addOrderUseCase();
        this.getAllOrderUseCase = presentation.MyConfig.instance().getAllOrderUseCase();
        this.getAllBatchUseCases = presentation.MyConfig.instance().getAllBatchUseCases();
        this.isEditMode = orderToEdit != null;
        this.editingOrder = orderToEdit;

        // Устанавливаем доступные статусы до инициализации UI
        if (isEditMode) {
            this.statusOptions = new String[]{"В исполнении", "Выполнен", "В обработке", "Отменен"};
        } else {
            this.statusOptions = new String[]{"В обработке", "В исполнении"};
        }

        setupUI();
        if (isEditMode) {
            populateFields();
        } else {
            setFieldsEnabled(true); // Все поля должны быть активны для нового заказа
        }
    }

    private void populateFields() {
        if (editingOrder == null) return;
        
        // Сначала включаем все поля для редактирования
        setFieldsEnabled(true);

        // Найти и выбрать партию
        List<Batch> batches = getAllBatchUseCases.invoke();
        for (Batch batch : batches) {
            if (batch.getId() == editingOrder.getId_batches()) {
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

        // Заполняем остальные поля
        sourceField.setText(editingOrder.getSourse());
        countField.setText(String.valueOf(editingOrder.getCount()));
        streetField.setText(editingOrder.getStreet());
        buildingField.setText(editingOrder.getBuilding());
        dateField.setText(editingOrder.getDate());
        phoneField.setText(editingOrder.getNumber());
        priceField.setText(String.valueOf(editingOrder.getPrice()));

        // Устанавливаем статус
        for (int i = 0; i < statusComboBox.getItemCount(); i++) {
            if (statusComboBox.getItemAt(i).equals(editingOrder.getStatus())) {
                statusComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Теперь отключаем конкретные поля, которые не должны быть редактируемыми
        sourceField.setEnabled(false); // Источник не редактируется согласно правилам checkForEdit
        batchComboBox.setEnabled(false); // ID партии не редактируется
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Заголовок для выбора партии
        JLabel titleLabel = new JLabel(isEditMode ? "Редактирование заказа" : "Выберите партию к заказу", SwingConstants.CENTER);
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
        
        // Используем статусы, установленные в конструкторе
        statusComboBox = new JComboBox<>(this.statusOptions);

        // Добавляем компоненты с метками
        addLabelAndField("Источник:", sourceField, gbc, 3);
        addLabelAndField("Кол-во:", countField, gbc, 4);
        addLabelAndField("Улица:", streetField, gbc, 5);
        addLabelAndField("Дом:", buildingField, gbc, 6);
        addLabelAndField("Дата:", dateField, gbc, 7);
        addLabelAndField("Номер телефона:", phoneField, gbc, 8);
        addLabelAndField("Состояние:", statusComboBox, gbc, 9);
        addLabelAndField("Стоимость за ед.:", priceField, gbc, 10);

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

    public void setSelectedBatch(int id) {
        List<Batch> batches = getAllBatchUseCases.invoke();
        for(Batch batch : batches) {
            if(batch.getId() == id)
            {
                this.selectedBatch = batch;
            };
        }
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
        if (selectedBatch == null && !isEditMode) {
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

    public void setStatusOptions(String[] statusOptions) {
        this.statusOptions = statusOptions;
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
                   " (доступно: " + batch.getRemainder() + " шт.)";
        }
    }
} 