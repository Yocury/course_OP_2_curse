package view;
//ToDo: Коментарии.

import Data.DB_manager;
import Data.DataService;
import Entities.Butches;
import Entities.Order;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public abstract class PagePanel extends JPanel {
    protected JTable table;
    protected JLabel titleLabel;
    protected JButton addButton;
    protected JButton deleteButton;
    protected JButton editButton;
    protected JButton refreshButton;
    public JPanel buttonPanel;
    private final DB_manager db;
    private final DataService.BatchAnalysis dataService;

    public PagePanel(String title) {
        setLayout(new BorderLayout());
        db = new DB_manager();
        dataService = new DataService.BatchAnalysis();
        // Заголовок страницы
        titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Таблица данных
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        //настройки таблицы
        table.setDefaultEditor(Object.class, null);
        table.setFillsViewportHeight(true);
        table.setCellSelectionEnabled(true);
        table.setColumnSelectionAllowed(true);
        table.setRowSelectionAllowed(true);

        //Создаем и получаем кнопки
        addButton = new JButton("Добавить");
        deleteButton = new JButton("Удалить");
        editButton = new JButton("Изменить");
        refreshButton = new JButton("Обновить");

        // Панель с кнопками справа
        buttonPanel = new JPanel();

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);

        addCustomButtons();

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> updateData());
        editButton.addActionListener(e -> EditCell(title));
        //Обработчики
        if (title.equals("Заказы")) {
            addButton.addActionListener(e -> AddOrder());
            deleteButton.addActionListener(e -> Delete("orders"));

        }
        if (title.equals("Расходы")) {
            addButton.addActionListener(e -> AddExpenses());
            deleteButton.addActionListener(e -> Delete("expenses"));
        }
        if (title.equals("Партии")) {
            addButton.addActionListener(e -> AddButches());
            deleteButton.addActionListener(e -> Delete("batches"));
        }
    }

    private void EditCellOrders() {
        int selectedRow = table.getSelectedRow();
        int selectedColumn = table.getSelectedColumn();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите ячейку.");
            return;
        }
        String value = table.getValueAt(selectedRow, selectedColumn).toString();
        String changed;
        Object id = table.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите изменить ячейку с данными: " + value + "?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (selectedColumn == 8) {
                String[] options = {"В обработке", "В исполнении", "Выполнен", "Отменен"};
                changed = (String) JOptionPane.showInputDialog(this, "Введите статус заказа", "Выбор настройки заказа",
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            }
            if (CheckForEdit(selectedColumn)) {
                changed = JOptionPane.showInputDialog(this, "Введите измененные данные");
                if (changed == null) {
                    JOptionPane.showMessageDialog(this, "Введите новые данные.");
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Данную ячейку нельзя изменить.");
                return;
            }
            db.UpdateDBCell(id, "Заказы", getColumnNameByIndex(selectedColumn), changed);
            updateData();
        }

    }

    private void EditCell(String title) {
        int selectedRow = table.getSelectedRow();
        int selectedColumn = table.getSelectedColumn();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите ячейку.");
            return;
        }
        String value = table.getValueAt(selectedRow, selectedColumn).toString();
        String changed;
        Object id = table.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите изменить ячейку с данными: " + value + "?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if ((selectedColumn == 8) && (title.equals("Заказы"))) {
                String[] options = {"В обработке", "В исполнении", "Выполнен", "Отменен"};
                changed = (String) JOptionPane.showInputDialog(this, "Введите статус заказа", "Выбор настройки заказа",
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            } else {
                if ((selectedColumn == 4) && (title.equals("Партии"))) {
                    String[] options = {"В обработке", "В продаже", "Продана", "Отменена"};
                    changed = (String) JOptionPane.showInputDialog(this, "Введите статус партии", "Выбор статуса партии",
                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]
                    );
                } else {
                    if ((selectedColumn == 2) && (title.equals("Расходы"))) {
                        String[] types = {"Персонал", "Реклама", "Аренда", "Бухгалтерия", "Техника"};
                        changed = (String) JOptionPane.showInputDialog(this, "Введите тип расходов", "Выбор типа",
                                JOptionPane.QUESTION_MESSAGE, null, types, types[0]
                        );
                    } else {
                        if (CheckForEdit(selectedColumn)) {
                            changed = JOptionPane.showInputDialog(this, "Введите измененные данные");
                            if (changed == null) {
                                JOptionPane.showMessageDialog(this, "Введите новые данные.");
                                return;
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Данную ячейку нельзя изменить.");
                            return;
                        }
                    }
                }
            }

            if (changed != null) {
                db.UpdateDBCell(id, title, getColumnNameByIndex(selectedColumn), changed);
                updateData();
            } else {
                JOptionPane.showMessageDialog(this, "Изменения не внесены!");
            }
        }
    }


    private void Delete(String title) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите строку.");
            return;
        }
        int id = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите удалить строку с ID" + id + "?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                this.db.DeleteLineDB(title, id);
                JOptionPane.showMessageDialog(this, "Строка удалена успешно.", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка при удалении строки: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private void AddExpenses() {
        AddExpenseDialog dialog = new AddExpenseDialog(SwingUtilities.getWindowAncestor(this) instanceof JFrame ? 
            (JFrame) SwingUtilities.getWindowAncestor(this) : null);
        dialog.setVisible(true);

        if (!dialog.isApproved()) {
            return;
        }

        // Получаем данные из диалога
        String description = dialog.getDescription();
        String type = dialog.getExpenseType();
        String amount = dialog.getAmount();
        String date = dialog.getDate();

        // Добавляем расход в базу данных
        int id = db.getId("expenses") + 1;
        db.connect();
        db.addExpensesDB(id, description, type, amount, date);
        
        // Обновляем данные в таблице
        updateData();
    }

    public abstract boolean CheckForEdit(int column);

    protected abstract String getColumnNameByIndex(int colummn);

    private void AddButches() {
        AddBatchDialog dialog = new AddBatchDialog(SwingUtilities.getWindowAncestor(this) instanceof JFrame ? 
            (JFrame) SwingUtilities.getWindowAncestor(this) : null);
        dialog.setVisible(true);

        if (!dialog.isApproved()) {
            return;
        }

        // Получаем данные из диалога
        String provider = dialog.getProvider();
        String amount = dialog.getAmount();
        String status = dialog.getStatus();
        String date = dialog.getDate();
        String count = dialog.getCount();

        // Добавляем партию в базу данных
        int id = db.getId("batches") + 1;
        db.connect();
        db.addButchesDB(id, provider, amount, status, date, Integer.parseInt(count));
        
        // Обновляем данные в таблице
        updateData();
    }


    private void AddOrder() {
        // Показываем диалог добавления заказа
        AddOrderDialog dialog = new AddOrderDialog(SwingUtilities.getWindowAncestor(this) instanceof JFrame ? 
            (JFrame) SwingUtilities.getWindowAncestor(this) : null);
        dialog.setVisible(true);

        if (!dialog.isApproved()) {
            return;
        }

        Butches selectedBatch = dialog.getSelectedBatch();
        if (selectedBatch == null) {
            return;
        }

        // Получаем данные из диалога
        String source = dialog.getSource();
        String count = dialog.getCount();
        String street = dialog.getStreet();
        String building = dialog.getBuilding();
        String date = dialog.getDate();
        String phone = dialog.getPhone();
        String status = dialog.getStatus();
        String price = dialog.getPrice();

        // Добавляем заказ в базу данных
        int id = db.getId("orders") + 1;
        db.connect();
        db.addOrderDB(id, selectedBatch.getId(), source, count, street, building, date, phone, status, price);
        
        // Обновляем данные в таблице
        updateData();
    }

    public void updateBatchStatusIfEmpty(int batchId) {
        Butches batch = dataService.getBatchById(batchId);
        if (batch == null) {
            System.out.println("Партия с ID " + batchId + " не найдена.");
            return;
        }

        int totalCount = batch.getCount();

        // Получаем все заказы по партии
        List<Order> orders = db.LoadDBFilterOrders(batchId);

        // Считаем сколько товара уже продано
        int soldCount = 0;
        for (Order order : orders) {
            soldCount += order.getCount();
        }

        int remaining = totalCount - soldCount;

        // Если товара нет, меняем статус
        if (remaining <= 0 && !batch.getStatus().equalsIgnoreCase("Продана")) {
            try {
                db.updateBatchStatus(batchId, "Продана");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Статус партии " + batchId + " обновлен на 'Закончен'");
        } else if (remaining > 0 && !batch.getStatus().equalsIgnoreCase("В продаже")) {
            try {
                db.updateBatchStatus(batchId, "В продаже");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Статус партии " + batchId + " обновлен на 'В продаже'");
        }
    }


    protected void addButtonToPanel(JButton button) {
        buttonPanel.add(button);
    }

    protected abstract void addCustomButtons();

    // Абстрактный метод для обновления данных таблицы
    public abstract void updateData();
}
