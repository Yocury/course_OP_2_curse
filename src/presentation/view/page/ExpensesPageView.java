package presentation.view.page;

import domain.entities.Expenses;
import domain.usecases.expenses.FilterExpensesUseCase;
import domain.usecases.expenses.GetAllExpensesUseCase;

import javax.swing.*;
import java.util.List;

import presentation.controller.ExpensesController;

public class ExpensesPageView extends PagePanelView {
    private final FilterExpensesUseCase filterExpensesUseCase;
    private final GetAllExpensesUseCase getAllExpensesUseCase;
    private ExpensesController controller;
    public JButton addButton, deleteButton, editButton, updateButton, filterButton;

    public ExpensesPageView(FilterExpensesUseCase filterExpensesUseCase, GetAllExpensesUseCase getAllExpensesUseCase) {
        super("Расходы");
        this.filterExpensesUseCase = filterExpensesUseCase;
        this.getAllExpensesUseCase = getAllExpensesUseCase;
    }

    public void setController(ExpensesController controller) {
        this.controller = controller;
    }

    @Override
    protected void addCustomButtons() {
        addButton = new JButton("Добавить");
        deleteButton = new JButton("Удалить");
        editButton = new JButton("Изменить");
        updateButton = new JButton("Обновить");
        filterButton = new JButton("Фильтрация по типу");

        addButtonToPanel(addButton);
        addButtonToPanel(deleteButton);
        addButtonToPanel(editButton);
        addButtonToPanel(updateButton);
        addButtonToPanel(filterButton);
    }


    @Override
    public void updateData() {
        controller.onUpdate();
    }

    public void updateTable(List<Expenses> expenses) {
        String[][] tableData = new String[expenses.size()][6];
        for (int i = 0; i < expenses.size(); i++) {
            tableData[i] = expenses.get(i).toDoubleArray();
        }
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,
                new String[]{"ID", "Описание", "Тип", "Сумма", "Дата", "ID партии"}
        ));
    }

    @Override
    public String FilteringToTypes()
    {
        String type;
        String[] types = {"Персонал", "Реклама", "Аренда", "Бухгалтерия", "Техника"};
        type = (String) JOptionPane.showInputDialog(this, "Введите тип расходов", "Фильтрация",
                JOptionPane.QUESTION_MESSAGE, null, types, types[0]
        );
        if(type == null ) {
            JOptionPane.showMessageDialog(this, "Изменения не внесены! Выберите тип. ");
            return "all";
        }
        return type;
    }

    @Override
    public boolean checkForEdit(int column) {
        // ID, тип, дата и ID партии не редактируются
        return column != 0 && column != 2 && column != 4 && column != 5;
    }

    @Override
    public String getColumnNameByIndex(int column) {
        String[] columnNames = {"id", "description", "type", "amount", "date", "batch_id"};
        return columnNames[column];
    }

    @Override
    protected boolean isRowEditable(int selectedRow) {
        // Для расходов, все строки считаются редактируемыми, если не указано иное
        return true;
    }
}
