package presentation.view.page;

import domain.entities.Batch;
import domain.usecases.batche.*;

import javax.swing.*;
import java.util.List;

import presentation.controller.BatchesController;

public class BatchesPageView extends PagePanelView {
    private final AddBatchUseCase addBatchUseCase;
    private final DeleteBatchUseCase deleteBatchUseCase;
    private final UpdateBatchUseCase updateBatchUseCase;
    private final AnalyzeBatchUseCase analyzeBatchUseCase;
    private final FilterBatchesUseCase filterBatchesUseCase;
    private final GetAllBatchUseCases getAllBatchUseCases;
    private BatchesController controller;
    public JButton addButton, deleteButton, updateButton, filterButton, analyzeButton, editButton, viewOrdersButton;

    public BatchesPageView(
            AddBatchUseCase addBatchUseCase,
            DeleteBatchUseCase deleteBatchUseCase,
            UpdateBatchUseCase updateBatchUseCase,
            AnalyzeBatchUseCase analyzeBatchUseCase,
            FilterBatchesUseCase filterBatchesUseCase,
            GetAllBatchUseCases getAllBatchUseCases
    ) {
        super("Партии");
        this.addBatchUseCase = addBatchUseCase;
        this.deleteBatchUseCase = deleteBatchUseCase;
        this.updateBatchUseCase = updateBatchUseCase;
        this.analyzeBatchUseCase = analyzeBatchUseCase;
        this.filterBatchesUseCase = filterBatchesUseCase;
        this.getAllBatchUseCases = getAllBatchUseCases;
    }

    public void setController(BatchesController controller) {
        this.controller = controller;
    }

    @Override
    protected void addCustomButtons() {
        addButton = new JButton("Добавить");
        deleteButton = new JButton("Удалить");
        editButton = new JButton("Изменить");
        updateButton = new JButton("Обновить");
        filterButton = new JButton("Фильтр по статусу");
        analyzeButton = new JButton("Анализ");
        viewOrdersButton = new JButton("Посмотреть заказы");

        addButtonToPanel(addButton);
        addButtonToPanel(deleteButton);
        addButtonToPanel(editButton);
        addButtonToPanel(updateButton);
        addButtonToPanel(filterButton);
        addButtonToPanel(analyzeButton);
        addButtonToPanel(viewOrdersButton);
    }

    @Override
    public void updateData() {
        controller.onUpdate();
    }

    public void updateTable(List<Batch> batches) {
        String[][] tableData = new String[batches.size()][8];
        for (int i = 0; i < batches.size(); i++) {
            tableData[i] = batches.get(i).toDoubleArray();
        }
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,
                new String[]{"ID", "Поставщик", "Дата", "Сумма", "Статус", "Количество", "Средняя цена", "Остаток"}
        ));
    }


    @Override
    public String FilteringToTypes() {
        String type;
        String[] types = {"В продаже", "В обработке", "Продана", "Отменена"};
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
        return ((column > 1) && (column != 3)) && (column != 6) && (column != 7);
    }

    @Override
    public String getColumnNameByIndex(int column) {
        String[] columnNames = {"id", "provider", "date", "amount", "status", "count", "avg_price", "remainder"};
        return columnNames[column];
    }

    @Override
    protected boolean isRowEditable(int selectedRow) {
        // Для партий, все строки считаются редактируемыми, если не указано иное
        return true;
    }
}
