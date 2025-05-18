package view;


import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private OrdersPage ordersPage;
    private ExpensesPage expensesPage;
    private BatchesPage batchesPage;

    private JPanel navigationPanel; // Добавляем поле для панели навигации

    public MainFrame() {
        setTitle("Управление данными");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Создаем страницы
        ordersPage = new OrdersPage();
        expensesPage = new ExpensesPage();
        batchesPage = new BatchesPage(this);

        contentPanel.add(ordersPage, "Заказы");
        contentPanel.add(expensesPage, "Расходы");
        contentPanel.add(batchesPage, "Партии");

        add(contentPanel);

        // Верхняя панель навигации
        navigationPanel = new JPanel(new GridLayout(1, 5)); // Доп. баллы за grid Layout.

        JButton ordersButton = createNavButton("Заказы", ordersPage);
        JButton expensesButton = createNavButton("Расходы", expensesPage);
        JButton batchesButton = createNavButton("Партии", batchesPage);

        navigationPanel.add(ordersButton);
        navigationPanel.add(expensesButton);
        navigationPanel.add(batchesButton);

        add(navigationPanel, BorderLayout.NORTH);

        // Показываем первую страницу
        cardLayout.show(contentPanel, "Заказы");
        ordersPage.updateData();
    }

    public void showFilteredOrders(String batchId) {
        cardLayout.show(contentPanel, "Заказы");
        ordersPage.showOrdersForBatch(batchId);
    }


    private JButton createNavButton(String title, PagePanel page) {
        JButton button = new JButton(title);
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, title);
            page.updateData();

        });
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
