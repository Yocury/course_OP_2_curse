import presentation.view.MainFrame;
import presentation.MyConfig;

public class Main {
    public static void main(String[] args) {
        // Запуск главного окна приложения в EDT (Event Dispatch Thread)
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame mainFrame = new MainFrame(
                    MyConfig.instance().addBatchUseCase(),
                    MyConfig.instance().getAllBatchUseCases(),
                    MyConfig.instance().addOrderUseCase(),
                    MyConfig.instance().getAllOrderUseCase(),
                    MyConfig.instance().filterOrdersUseCase(),
                    MyConfig.instance().filterOrdersByBatchUseCase(),
                    MyConfig.instance().filterExpensesUseCase(),
                    MyConfig.instance().getAllExpensesUseCase(),
                    MyConfig.instance().deleteBatchUseCase(),
                    MyConfig.instance().updateBatchUseCase(),
                    MyConfig.instance().analyzeBatchUseCase(),
                    MyConfig.instance().filterBatchesUseCase(),
                    MyConfig.instance().getNextBatchIdUseCase(),
                    MyConfig.instance().getNextOrderIdUseCase(),
                    MyConfig.instance().getNextExpenseIdUseCase(),
                    MyConfig.instance().deleteOrderUseCase(),
                    MyConfig.instance().updateOrderUseCase(),
                    MyConfig.instance().addExpensesUseCase(),
                    MyConfig.instance().deleteExpensesUseCase(),
                    MyConfig.instance().updateExpensesUseCase()
                );
                mainFrame.setVisible(true);
            }
        });
    }
} 