package presentation.controller;

import domain.entities.Order;
import domain.usecases.order.*;
import presentation.view.page.OrdersPageView;
import java.util.List;

import presentation.view.add.AddOrderDialog;
import domain.entities.Batch;
import javax.swing.JOptionPane;

import presentation.MyConfig;
import javax.swing.JButton;
import domain.usecases.batche.GetAllBatchUseCases;
import domain.usecases.batche.UpdateBatchUseCase;

public class OrdersController {
    private final OrdersPageView view;
    private final FilterOrdersByStatusUseCase filterOrdersByStatusUseCase;
    private final GetAllOrderUseCase getAllOrderUseCase;
    private final FilterOrdersByBatchUseCase filterOrdersByBatchUseCase;
    private final GetNextOrderIdUseCase getNextOrderIdUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;
    private final UpdateOrderUseCase updateOrderUseCase;
    private final AddOrderUseCase addOrderUseCase;
    private final GetAllBatchUseCases getAllBatchUseCases;
    private final UpdateBatchUseCase updateBatchUseCase;

    public OrdersController(OrdersPageView view) {
        this.view = view;
        this.filterOrdersByStatusUseCase = MyConfig.instance().filterOrdersUseCase();
        this.getAllOrderUseCase = MyConfig.instance().getAllOrderUseCase();
        this.filterOrdersByBatchUseCase = MyConfig.instance().filterOrdersByBatchUseCase();
        this.getNextOrderIdUseCase = MyConfig.instance().getNextOrderIdUseCase();
        this.deleteOrderUseCase = MyConfig.instance().deleteOrderUseCase();
        this.updateOrderUseCase = MyConfig.instance().updateOrderUseCase();
        this.addOrderUseCase = MyConfig.instance().addOrderUseCase();
        this.getAllBatchUseCases = MyConfig.instance().getAllBatchUseCases();
        this.updateBatchUseCase = MyConfig.instance().updateBatchUseCase();
        this.view.setController(this);
    }

    public void onFilterByStatus() {
        String status = view.FilteringToTypes();
        if (status.equals("all")) {
            this.onUpdate();
            return;
        }
        List<Order> filtered = this.filterOrdersByStatusUseCase.invoke(status);
        view.updateTable(filtered);
    }

    public void onFilterByBatch(int batchId) {
        List<Order> filtered = filterOrdersByBatchUseCase.invoke(batchId);
        view.updateTable(filtered);
    }

    public void onUpdate() {
        List<Order> orders = getAllOrderUseCase.invoke();
        view.updateTable(orders);
    }

    private void RefreshStatusBatches(int batchId){
        List<Batch> allBatches = getAllBatchUseCases.invoke();
        for (Batch batch : allBatches) {
            if (batch.getId() == batchId) {
                // Recalculate remainder for the affected batch
                int initialBatchCount = batch.getCount();
                int orderedCount = 0;
                List<Order> allOrders = getAllOrderUseCase.invoke();
                for (Order order : allOrders) {
                    if (order.getId_batches() == batch.getId() && !order.getStatus().equalsIgnoreCase("отменено")) {
                        orderedCount += order.getCount();
                    }
                }
                batch.setRemainder(initialBatchCount - orderedCount);

                if (batch.getRemainder() == 0 && !batch.getStatus().equalsIgnoreCase("Продана")) {
                    batch.setStatus("Продана");
                    updateBatchUseCase.invoke(batch);
                }
                break;
            }
        }
    }

    public void onAdd() {
        AddOrderDialog dialog = new AddOrderDialog(null);
        dialog.setVisible(true);
        if (!dialog.isApproved()) return;
        Batch selectedBatch = dialog.getSelectedBatch();
        if (selectedBatch == null) return;
        int id = getNextOrderIdUseCase.invoke();
        String source = dialog.getSource();
        String countStr = dialog.getCount();
        String street = dialog.getStreet();
        String building = dialog.getBuilding();
        String date = dialog.getDate();
        String phone = dialog.getPhone();
        String status = dialog.getStatus();
        String priceStr = dialog.getPrice();

        int count = Integer.parseInt(countStr);
        int price = Integer.parseInt(priceStr);

        domain.entities.Order newOrder = new domain.entities.Order(id, source, count, street, building, selectedBatch.getId(), date, phone, status, price);
        addOrderUseCase.invoke(newOrder);

        // Update batch remainder and status
        RefreshStatusBatches(selectedBatch.getId());
        onUpdate();
    }

    public void onDelete() {
        int selectedRow = view.table.getSelectedRow();
        if (selectedRow != -1) {
            int id = Integer.parseInt(view.table.getValueAt(selectedRow, 0).toString());
            Order order = getOrderById(id);
            deleteOrderUseCase.invoke(order);
            onUpdate();
        }
    }

    public void onEdit() {
        int selectedRow = view.table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Выберите заказ для редактирования");
            return;
        }
        int id = Integer.parseInt(view.table.getValueAt(selectedRow, 0).toString());
        Order oldOrder = getOrderById(id);
        if (oldOrder == null) {
            JOptionPane.showMessageDialog(view, "Заказ не найден");
            return;
        }
        
        if ("Отменено".equalsIgnoreCase(oldOrder.getStatus())) {
            JOptionPane.showMessageDialog(view, "Нельзя редактировать отмененный заказ");
            return;
        }
        
        AddOrderDialog dialog = new AddOrderDialog(null, oldOrder);
        String[] statusOptions = {"В исполнении", "Выполнен", "В обработке", "Отменен"};
        dialog.setStatusOptions(statusOptions);
        dialog.setSelectedBatch(oldOrder.getId_batches());
        dialog.setVisible(true);
        if (!dialog.isApproved()) return;
        
        Batch selectedBatch = dialog.getSelectedBatch();
        if (selectedBatch == null) return;
        
        String source = dialog.getSource();
        String count = dialog.getCount();
        String street = dialog.getStreet();
        String building = dialog.getBuilding();
        String date = dialog.getDate();
        String phone = dialog.getPhone();
        String status = dialog.getStatus();
        String price = dialog.getPrice();
        
        Order updatedOrder = new Order(id, source, Integer.parseInt(count), street, building, selectedBatch.getId(), date, phone, status, Integer.parseInt(price));
        updateOrderUseCase.invoke(updatedOrder);
        RefreshStatusBatches(selectedBatch.getId());
        onUpdate();
    }

    private Order getOrderById(int id) {
        for (Order o : getAllOrderUseCase.invoke()) {
            if (o.getId() == id) return o;
        }
        return null;
    }

    public void initButtonListeners(JButton add, JButton delete, JButton edit, JButton update, JButton filter, JButton filterBatch) {
        add.addActionListener(e -> onAdd());
        delete.addActionListener(e -> onDelete());
        edit.addActionListener(e -> onEdit());
        update.addActionListener(e -> onUpdate());
        filter.addActionListener(e -> onFilterByStatus());
        filterBatch.addActionListener(e -> {
            int selectedRow = view.table.getSelectedRow();
            if (selectedRow != -1) {
                int batchId = Integer.parseInt(view.table.getValueAt(selectedRow, 5).toString());
                onFilterByBatch(batchId);
            }
        });
    }
} 