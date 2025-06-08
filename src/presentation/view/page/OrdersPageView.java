package presentation.view.page;

import domain.entities.Order;
import domain.usecases.order.FilterOrdersByStatusUseCase;
import domain.usecases.order.GetAllOrderUseCase;
import domain.usecases.order.FilterOrdersByBatchUseCase;
import javax.swing.*;
import java.util.List;
import presentation.controller.OrdersController;

public class OrdersPageView extends PagePanelView {
    private final FilterOrdersByStatusUseCase filterOrdersByStatusUseCase;
    private final GetAllOrderUseCase getAllOrderUseCase;
    private final FilterOrdersByBatchUseCase filterOrdersByBatchUseCase;
    private OrdersController controller;
    public JButton addButton, deleteButton, editButton, updateButton, filterButton, filterBatchButton;

    public OrdersPageView(FilterOrdersByStatusUseCase filterOrdersByStatusUseCase, GetAllOrderUseCase getAllOrderUseCase, FilterOrdersByBatchUseCase filterOrdersByBatchUseCase) {
        super("Заказы");
        this.filterOrdersByStatusUseCase = filterOrdersByStatusUseCase;
        this.getAllOrderUseCase = getAllOrderUseCase;
        this.filterOrdersByBatchUseCase = filterOrdersByBatchUseCase;
    }

    public void setController(OrdersController controller) {
        this.controller = controller;
    }

    @Override
    protected void addCustomButtons() {
        addButton = new JButton("Добавить");
        deleteButton = new JButton("Удалить");
        editButton = new JButton("Изменить");
        updateButton = new JButton("Обновить");
        filterButton = new JButton("Фильтрация по статусу");
        filterBatchButton = new JButton("Показать заказы партии");

        addButtonToPanel(addButton);
        addButtonToPanel(deleteButton);
        addButtonToPanel(editButton);
        addButtonToPanel(updateButton);
        addButtonToPanel(filterButton);
        addButtonToPanel(filterBatchButton);
    }

    @Override
    public void updateData() {
        controller.onUpdate();
    }

    public void updateTable(List<Order> orders) {
        String[][] tableData = new String[orders.size()][9];
        for (int i = 0; i < orders.size(); i++) {
            tableData[i] = orders.get(i).toDoubleArray();
        }
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,
                new String[]{"ID", "Источник", "Кол-во", "Улица", "Дом", "ID партии", "Дата", "Номер телефона", "Состояние"}
        ));
    }



    @Override
    public String FilteringToTypes() {
        String type;
        String[] types = {"Выполнен", "В исполнении", "В обработке", "Отменен"};
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
        // ID, ID партии и статус не редактируются
        return column != 0 && column != 1 && column != 5;
    }

    @Override
    public String getColumnNameByIndex(int column) {
        String[] columnNames = {"id", "id_batches", "client", "date", "count", "status", "price"};
        return columnNames[column];
    }

    @Override
    protected boolean isRowEditable(int selectedRow) {
        // Проверяем статус заказа - если "Отменено", то редактировать нельзя
        String status = (String) table.getValueAt(selectedRow, 5);
        return !"Отменено".equalsIgnoreCase(status);
    }
}
