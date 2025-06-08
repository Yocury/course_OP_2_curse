package presentation.controller;

import domain.entities.Batch;
import domain.entities.Order;
import domain.usecases.batche.AddBatchUseCase;
import domain.usecases.batche.DeleteBatchUseCase;
import domain.usecases.batche.UpdateBatchUseCase;
import domain.usecases.batche.AnalyzeBatchUseCase;
import domain.usecases.batche.FilterBatchesUseCase;
import domain.usecases.batche.GetAllBatchUseCases;
import domain.usecases.batche.GetNextBatchIdUseCase;
import domain.usecases.order.GetAllOrderUseCase;
import presentation.view.MainFrame;
import presentation.view.page.BatchesPageView;
import presentation.view.add.AddBatchDialog;

import javax.swing.*;
import java.util.List;

import presentation.MyConfig;

public class BatchesController {
    private final BatchesPageView view;
    private final AddBatchUseCase addBatchUseCase;
    private final DeleteBatchUseCase deleteBatchUseCase;
    private final UpdateBatchUseCase updateBatchUseCase;
    private final AnalyzeBatchUseCase analyzeBatchUseCase;
    private final FilterBatchesUseCase filterBatchesUseCase;
    private final GetAllBatchUseCases getAllBatchUseCases;
    private final GetNextBatchIdUseCase getNextBatchIdUseCase;
    private final GetAllOrderUseCase getAllOrderUseCase;
    private final OrdersController ordersController;
    private final MainFrame mainFrame;

    public BatchesController(BatchesPageView view, OrdersController ordersController, MainFrame mainFrame) {
        this.view = view;
        this.ordersController = ordersController;
        this.mainFrame = mainFrame;
        this.addBatchUseCase = MyConfig.instance().addBatchUseCase();
        this.deleteBatchUseCase = MyConfig.instance().deleteBatchUseCase();
        this.updateBatchUseCase = MyConfig.instance().updateBatchUseCase();
        this.analyzeBatchUseCase = MyConfig.instance().analyzeBatchUseCase();
        this.filterBatchesUseCase = MyConfig.instance().filterBatchesUseCase();
        this.getAllBatchUseCases = MyConfig.instance().getAllBatchUseCases();
        this.getNextBatchIdUseCase = MyConfig.instance().getNextBatchIdUseCase();
        this.getAllOrderUseCase = MyConfig.instance().getAllOrderUseCase();
        this.view.setController(this);
    }

    public void initButtonListeners(JButton add, JButton delete, JButton edit, JButton update, JButton filter, JButton analyze, JButton viewOrders) {
        add.addActionListener(e -> onAdd());
        delete.addActionListener(e -> onDelete());
        edit.addActionListener(e -> onEdit());
        update.addActionListener(e -> onUpdate());
        filter.addActionListener(e -> onFilterByStatus());
        analyze.addActionListener(e -> onAnalyze());
        viewOrders.addActionListener(e -> onViewOrders());
    }

    public void onAdd() {
        AddBatchDialog dialog = new AddBatchDialog(null);
        String[] statusOptions = {"В обработке", "В продаже"};
        dialog.setStatusOptions(statusOptions);
        dialog.setVisible(true);
        if (!dialog.isApproved()) return;
        String provider = dialog.getProvider();
        int amount = Integer.parseInt(dialog.getAmount());
        String status = dialog.getStatus();
        String date = dialog.getDate();
        int count = Integer.parseInt(dialog.getCount());
        int id = getNextBatchIdUseCase.invoke();
        Batch batch = new Batch();
        batch.newBatch(id, provider, date, amount, status, count);
        addBatchUseCase.invoke(batch);
        onUpdate();
    }

    public void onDelete() {
        int selectedRow = view.table.getSelectedRow();
        if (selectedRow != -1) {
            int id = Integer.parseInt(view.table.getValueAt(selectedRow, 0).toString());
            deleteBatchUseCase.invoke(id);
            onUpdate();
        }
    }

    public void onEdit() {
        int selectedRow = view.table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Выберите партию для редактирования");
            return;
        }
        int id = Integer.parseInt(view.table.getValueAt(selectedRow, 0).toString());
        Batch oldBatch = getBatchById(id);
        if (oldBatch == null) {
            JOptionPane.showMessageDialog(view, "Партия не найдена или не выбрана");
            return;
        }

        AddBatchDialog dialog = new AddBatchDialog(null, oldBatch);
        String[] statusOptions = {"Продана", "В обработке", "В продаже"};
        dialog.setStatusOptions(statusOptions);
        dialog.setVisible(true);
        if (!dialog.isApproved()) return;

        String provider = dialog.getProvider();
        int amount = Integer.parseInt(dialog.getAmount());
        String status = dialog.getStatus();
        String date = dialog.getDate();
        int count = Integer.parseInt(dialog.getCount());

        Batch updatedBatch = new Batch();
        updatedBatch.newBatch(id, provider, date, amount, status, count);
        updateBatchUseCase.invoke(updatedBatch);
        onUpdate();
    }

    private Batch getBatchById(int id) {
        for (Batch b : getAllBatchUseCases.invoke()) {
            if (b.getId() == id) return b;
        }
        return null;
    }

    public void onUpdate() {
        List<Batch> batches = getAllBatchUseCases.invoke();
        List<Order> orders = getAllOrderUseCase.invoke();

        for (Batch batch : batches) {
            int initialCount = batch.getCount();
            int orderedCount = 0;
            for (Order order : orders) {
                if (order.getId_batches() == batch.getId() && !order.getStatus().equalsIgnoreCase("отмененно")) {
                    orderedCount += order.getCount();
                }
            }
            batch.setRemainder(initialCount - orderedCount);
        }
        view.updateTable(batches);
    }

    public void onFilterByStatus() {
        String status = view.FilteringToTypes();
        if (status.equals("all")) {
            return;
        } else {
            List<Batch> filtered = filterBatchesUseCase.invoke(status);
            view.updateTable(filtered);
        }
    }

    public void onViewOrders() {
        int selectedRow = view.table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(view, "Выберите партию для просмотра заказов");
            return;
        }
        int batchId = Integer.parseInt(view.table.getValueAt(selectedRow, 0).toString());
        ordersController.onFilterByBatch(batchId);
        mainFrame.switchToPage("Заказы");
    }

    public void onAnalyze() {
        int selectedRow = view.table.getSelectedRow();
        if (selectedRow != -1) {
            int id = Integer.parseInt(view.table.getValueAt(selectedRow, 0).toString());
            Object analysis = analyzeBatchUseCase.invoke(id);
            JOptionPane.showMessageDialog(view, analysis != null ? analysis.toString() : "Нет данных для анализа");
        }
    }
} 