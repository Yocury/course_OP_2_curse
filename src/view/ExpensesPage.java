package view;

import Data.DataService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class ExpensesPage extends PagePanel {
    private DataService dataService;
    private JButton searchButton;
    private JButton filterButton;
    private JButton historyButton;


    public ExpensesPage() {
        super("Расходы");
        dataService = new DataService();
    }

    @Override
    protected void addCustomButtons() {
    filterButton = new JButton("Фильтрация");

    addButtonToPanel(filterButton);
    filterButton.addActionListener(e -> FilteringExpensesToTypes());
    }

    private void FilteringExpensesToTypes()
    {
        String type;
        String[] types = {"Персонал", "Реклама", "Аренда", "Бухгалтерия", "Техника"};
        type = (String) JOptionPane.showInputDialog(this, "Введите тип расходов", "Фильтрация",
                JOptionPane.QUESTION_MESSAGE, null, types, types[0]
        );
        var expenses = dataService.getFilterExpensesFromDB(type);
        String[][] tableData = new String[expenses.size()][5]; // Увеличиваем размер массива
        for (int i = 0; i < expenses.size(); i++) {
            tableData[i] = expenses.get(i); // Данные расходов
        }
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,
                new String[]{"ID", "Описание", "Тип", "Сумма", "Дата"} // Добавляем "ID"
        ));

    }

    @Override
    public String getColumnNameByIndex(int column) {
        ArrayList<String> columnNames = new ArrayList<String>();
        Collections.addAll(columnNames, "id", "description", "type", "amount", "date");
        return columnNames.get(column);
    }

    @Override
    public boolean CheckForEdit(int column)
    {
        return ((column != 0) || (column != 2) || (column != 4));
    }

    @Override
    public void updateData() {
        var expenses = dataService.getExpensesFromDB();
        String[][] tableData = new String[expenses.size()][5]; // Увеличиваем размер массива
        for (int i = 0; i < expenses.size(); i++) {
            tableData[i] = expenses.get(i); // Данные расходов
        }
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,
                new String[]{"ID", "Описание", "Тип", "Сумма", "Дата"} // Добавляем "ID"
        ));
    }
}
