package presentation.view;

import presentation.view.page.BatchesPageView;
import presentation.view.page.ExpensesPageView;
import presentation.view.page.OrdersPageView;
import presentation.view.page.PagePanelView;
import domain.usecases.batche.AddBatchUseCase;
import domain.usecases.batche.GetAllBatchUseCases;
import domain.usecases.order.AddOrderUseCase;
import domain.usecases.order.GetAllOrderUseCase;
import domain.usecases.expenses.AddExpensesUseCase;
import domain.usecases.expenses.GetAllExpensesUseCase;
import domain.usecases.expenses.DeleteExpensesUseCase;
import domain.usecases.order.FilterOrdersByStatusUseCase;
import domain.usecases.order.FilterOrdersByBatchUseCase;
import domain.usecases.expenses.FilterExpensesUseCase;
import domain.usecases.batche.DeleteBatchUseCase;
import domain.usecases.batche.UpdateBatchUseCase;
import domain.usecases.batche.AnalyzeBatchUseCase;
import domain.usecases.batche.FilterBatchesUseCase;
import domain.usecases.batche.GetNextBatchIdUseCase;
import domain.usecases.order.GetNextOrderIdUseCase;
import domain.usecases.expenses.GetNextExpenseIdUseCase;
import domain.usecases.order.DeleteOrderUseCase;
import domain.usecases.order.UpdateOrderUseCase;
import domain.usecases.expenses.UpdateExpensesUseCase;
import presentation.controller.OrdersController;
import presentation.controller.ExpensesController;
import presentation.controller.BatchesController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final OrdersPageView ordersPage;

    public MainFrame(AddBatchUseCase addBatchUseCase, GetAllBatchUseCases getAllBatchUseCases,
                     AddOrderUseCase addOrderUseCase, GetAllOrderUseCase getAllOrderUseCase,
                     FilterOrdersByStatusUseCase filterOrdersByStatusUseCase, FilterOrdersByBatchUseCase filterOrdersByBatchUseCase,
                     FilterExpensesUseCase filterExpensesUseCase, GetAllExpensesUseCase getAllExpensesUseCase,
                     DeleteBatchUseCase deleteBatchUseCase, UpdateBatchUseCase updateBatchUseCase,
                     AnalyzeBatchUseCase analyzeBatchUseCase, FilterBatchesUseCase filterBatchesUseCase,
                     GetNextBatchIdUseCase getNextBatchIdUseCase, GetNextOrderIdUseCase getNextOrderIdUseCase,
                     GetNextExpenseIdUseCase getNextExpenseIdUseCase,
                     DeleteOrderUseCase deleteOrderUseCase, UpdateOrderUseCase updateOrderUseCase,
                     AddExpensesUseCase addExpensesUseCase, DeleteExpensesUseCase deleteExpensesUseCase, UpdateExpensesUseCase updateExpensesUseCase) {
        setTitle("Управление данными");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Создаем страницы
        ordersPage = new OrdersPageView(filterOrdersByStatusUseCase, getAllOrderUseCase, filterOrdersByBatchUseCase);
        ExpensesPageView expensesPage = new ExpensesPageView(filterExpensesUseCase, getAllExpensesUseCase);
        BatchesPageView batchesPage = new BatchesPageView(addBatchUseCase, deleteBatchUseCase, updateBatchUseCase, analyzeBatchUseCase, filterBatchesUseCase, getAllBatchUseCases);

        contentPanel.add(ordersPage, "Заказы");
        contentPanel.add(expensesPage, "Расходы");
        contentPanel.add(batchesPage, "Партии");

        add(contentPanel);

        // Верхняя панель навигации
        // Добавляем поле для панели навигации
        JPanel navigationPanel = new JPanel(new GridLayout(1, 5)); // Доп. баллы за grid Layout.

        JButton ordersButton = createNavButton("Заказы", ordersPage);
        JButton expensesButton = createNavButton("Расходы", expensesPage);
        JButton batchesButton = createNavButton("Партии", batchesPage);

        navigationPanel.add(ordersButton);
        navigationPanel.add(expensesButton);
        navigationPanel.add(batchesButton);

        add(navigationPanel, BorderLayout.NORTH);

        // MVC: связываем контроллеры
        OrdersController ordersController = new OrdersController(ordersPage);
        ordersController.initButtonListeners(
            ordersPage.addButton, ordersPage.deleteButton, ordersPage.editButton,
            ordersPage.updateButton, ordersPage.filterButton, ordersPage.filterBatchButton
        );
        ExpensesController expensesController = new ExpensesController(expensesPage);
        expensesController.initButtonListeners(
            expensesPage.addButton, expensesPage.deleteButton, expensesPage.editButton,
            expensesPage.updateButton, expensesPage.filterButton
        );
        BatchesController batchesController = new BatchesController(batchesPage, ordersController, this);
        batchesController.initButtonListeners(
            batchesPage.addButton, batchesPage.deleteButton, batchesPage.editButton,
            batchesPage.updateButton, batchesPage.filterButton, batchesPage.analyzeButton, batchesPage.viewOrdersButton
        );

        // Показываем первую страницу
        cardLayout.show(contentPanel, "Заказы");
        ordersPage.updateData();
    }


    private JButton createNavButton(String title, PagePanelView page) {
        JButton button = new JButton(title);
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, title);
            page.updateData();

        });
        return button;
    }

    public void switchToPage(String pageName) {
        cardLayout.show(contentPanel, pageName);
    }
}
