package presentation.controller;

import domain.entities.Expenses;
import domain.usecases.expenses.*;
import presentation.view.add.AddExpenseDialog;
import presentation.view.page.ExpensesPageView;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JButton;

import presentation.MyConfig;
import domain.entities.Batch;

public class ExpensesController {
    private final ExpensesPageView view;
    private final AddExpensesUseCase addExpensesUseCase;
    private final FilterExpensesUseCase filterExpensesUseCase;
    private final GetAllExpensesUseCase getAllExpensesUseCase;
    private final GetNextExpenseIdUseCase getNextExpenseIdUseCase;
    private final DeleteExpensesUseCase deleteExpensesUseCase;
    private final UpdateExpensesUseCase updateExpensesUseCase;

    public ExpensesController(ExpensesPageView view) {
        this.view = view;
        this.addExpensesUseCase = MyConfig.instance().addExpensesUseCase();
        this.filterExpensesUseCase = MyConfig.instance().filterExpensesUseCase();
        this.getAllExpensesUseCase = MyConfig.instance().getAllExpensesUseCase();
        this.getNextExpenseIdUseCase = MyConfig.instance().getNextExpenseIdUseCase();
        this.deleteExpensesUseCase = MyConfig.instance().deleteExpensesUseCase();
        this.updateExpensesUseCase = MyConfig.instance().updateExpensesUseCase();
        this.view.setController(this);
    }

    public void initButtonListeners(JButton add, JButton delete, JButton edit, JButton update, JButton filter) {
        add.addActionListener(e -> onAdd());
        delete.addActionListener(e -> onDelete());
        edit.addActionListener(e -> onEdit());
        update.addActionListener(e -> onUpdate());
        filter.addActionListener(e -> onFilterByType());
    }


        public void onFilterByType() {
            String type = view.FilteringToTypes();
            if (type.equals("all")) {
                return;
            } else {
                List<Expenses> filtered = filterExpensesUseCase.invoke(type);
                view.updateTable(filtered);
            }
        }

        public void onUpdate () {
            List<Expenses> expenses = getAllExpensesUseCase.invoke();
            view.updateTable(expenses);
        }

        public void onAdd () {
            AddExpenseDialog dialog = new AddExpenseDialog(null);
            dialog.setVisible(true);
            if (!dialog.isApproved()) return;
            int id = getNextExpenseIdUseCase.invoke();
            String description = dialog.getDescription();
            String type = dialog.getExpenseType();
            String amount = dialog.getAmount();
            String date = dialog.getDate();
            Batch selectedBatch = dialog.getSelectedBatch();
            if (selectedBatch == null) {
                JOptionPane.showMessageDialog(view, "Выберите партию для расхода");
                return;
            }
            domain.entities.Expenses expense = new domain.entities.Expenses(id, description, type, Integer.parseInt(amount), date, selectedBatch.getId());
            addExpensesUseCase.invoke(expense);
            onUpdate();
        }

        public void onDelete () {
            int selectedRow = view.table.getSelectedRow();
            if (selectedRow != -1) {
                int id = Integer.parseInt(view.table.getValueAt(selectedRow, 0).toString());
                Expenses expense = getExpenseById(id);
                deleteExpensesUseCase.invoke(id);
                onUpdate();
            }
        }

        public void onEdit () {
            int selectedRow = view.table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(view, "Выберите расход для редактирования");
                return;
            }
            int id = Integer.parseInt(view.table.getValueAt(selectedRow, 0).toString());
            Expenses oldExpense = getExpenseById(id);
            if (oldExpense == null) {
                JOptionPane.showMessageDialog(view, "Расход не найден");
                return;
            }

            AddExpenseDialog dialog = new AddExpenseDialog(null, oldExpense);
            dialog.setVisible(true);
            if (!dialog.isApproved()) return;

            String description = dialog.getDescription();
            String type = dialog.getExpenseType();
            String amount = dialog.getAmount();
            String date = dialog.getDate();
            dialog.setSelectedBatch(oldExpense.getBatchId());

            Expenses updatedExpense = new Expenses(id, description, type, Integer.parseInt(amount), date, oldExpense.getBatchId());
            updateExpensesUseCase.invoke(updatedExpense);
            onUpdate();
        }

        private Expenses getExpenseById ( int id){
            for (Expenses e : getAllExpensesUseCase.invoke()) {
                if (e.getId() == id) return e;
            }
            return null;
        }
    }