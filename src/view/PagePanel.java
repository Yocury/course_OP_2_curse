package view;
//ToDo: Коментарии.

import Data.DB_manager;
import Data.DataService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public abstract class PagePanel extends JPanel {
    protected JTable table;
    protected JLabel titleLabel;
    protected JButton addButton;
    protected JButton deleteButton;
    protected JButton editButton;
    protected JButton refreshButton;
    public JPanel buttonPanel;
    private final DB_manager db;
    private DataService.BatchAnalysis dataService;

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
            db.UpdateDBCell(id, title, getColumnNameByIndex(selectedColumn), changed);
            updateData();
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
        String description;
        String type;
        String amount;
        String date;

        description = JOptionPane.showInputDialog(this, "Введите название расходов");

        String[] types = {"Персонал", "Реклама", "Аренда", "Бухгалтерия", "Техника"};
        type = (String) JOptionPane.showInputDialog(this, "Введите тип расходов", "Выбор типа",
                JOptionPane.QUESTION_MESSAGE, null, types, types[0]
        );
        amount = JOptionPane.showInputDialog(this, "Введите сумму расходов");
        date = JOptionPane.showInputDialog(this, "Введите дату расходов");
        int id = (db.getId("expenses") + 1);
        db.connect();
        db.addExpensesDB(id, description, type, amount, date);
    }

    public abstract boolean CheckForEdit(int column);

    protected abstract String getColumnNameByIndex(int colummn);

    private void AddButches() {
        String provider;
        String amount;
        String status;
        String date;
        int count;
        String temp;

        provider = JOptionPane.showInputDialog(this, "Введите поставщика");
        amount = JOptionPane.showInputDialog(this, "Введите стоимость партии");
        String[] options = {"В обработке", "В продаже", "Продана",};
        status = (String) JOptionPane.showInputDialog(this, "Введите статус партии", "Выбор статуса партии",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]
        );
        date = JOptionPane.showInputDialog(this, "Введите дату партии");
        temp = JOptionPane.showInputDialog(this, "Введите кол-во товара");
        try { //
            count = Integer.parseInt(temp);
        } catch (NumberFormatException e) {
            return;
        }
        if (temp == null || temp.trim().isEmpty()) {
            return;
        }

        int id = (db.getId("batches") + 1);
        db.connect();
        db.addButchesDB(id, provider, amount, status, date, count);
    }


    private void AddOrder() {
        String sourse;
        int count;
        String temp;
        String street;
        String building;
        String date;
        String number;
        String status;
        String id_batches;
        int price;

        sourse = JOptionPane.showInputDialog(this, "Введите источник заказа");
        if (sourse == null || sourse.trim().isEmpty()) {
            return;
        }
        temp = JOptionPane.showInputDialog(this, "Введите кол-во товара.");
        try {
            count = Integer.parseInt(temp);
        } catch (NumberFormatException e) {
            return;
        }
        if (temp == null || temp.trim().isEmpty()) {
            return;
        }

        id_batches = JOptionPane.showInputDialog(this, "Введите ID партии");

        dataService.canAddOrder(Integer.parseInt(id_batches), count);
            if(dataService.canAdd) {

                street = JOptionPane.showInputDialog(this, "Введите улицу");
                if (street == null || street.trim().isEmpty()) {
                    return;
                }
                building = JOptionPane.showInputDialog(this, "Введите дом и корпус");
                if (building == null || building.trim().isEmpty()) {
                    return;
                }
                date = JOptionPane.showInputDialog(this, "Введите дату в формате день-месяц-год");
                if (date == null || date.trim().isEmpty()) {
                    return;
                }
                number = JOptionPane.showInputDialog(this, "Введите номер клиента"); //TODO
                if (number == null || number.trim().isEmpty()) {
                    return;
                }


                String[] options = {"В обработке", "В исполнении", "Выполнен", "Отменен"};
                status = (String) JOptionPane.showInputDialog(this, "Введите статус заказа", "Выбор статуса заказа",
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]
                );

                temp = JOptionPane.showInputDialog(this, "Введите стоимость товара за ед.");
                try { //
                    price = Integer.parseInt(temp);
                } catch (NumberFormatException e) {
                    return;
                }
                if (temp == null || temp.trim().isEmpty()) {
                    return;
                }

                int id = (db.getId("orders") + 1);
                db.connect();
                db.addOrderToDB(id, sourse, count, street, building, date, number, status, id_batches, price);
            }
            else {
                JOptionPane.showMessageDialog(null,dataService.messageCanAdd);
            }
    }

    protected void addButtonToPanel(JButton button) {
        buttonPanel.add(button);
    }

    protected abstract void addCustomButtons();

    // Абстрактный метод для обновления данных таблицы
    public abstract void updateData();
}
