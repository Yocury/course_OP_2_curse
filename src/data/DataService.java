package data;

import data.db_manager.BatchDBManager;
import domain.entities.Batch;
import domain.entities.Expenses;
import domain.entities.Order;

import java.util.ArrayList;
import java.util.List;

public class DataService {

    protected final BatchDBManager db;

    public DataService() {
        this.db = new BatchDBManager();
    }



    public static class BatchAnalysis extends DataService {
        public int batchId;
        public int totalCount;       // Общее количество товара в партии
        public int ordersCount;      // Количество заказов
        public int soldCount;        // Сколько продано
        public int remainingCount;   // Остаток
        public int batchAmount;      // Стоимость партии (amount)
        public int ordersTotalSum;   // Общая сумма по заказам (price * count)
        public double avgPricePerUnit; // Средняя цена за единицу
        public double avgPurchasePricePerUnit;  //Средняя цена за единицу (покупка)
        public int profit;           // Доход с партии
        public int expensesTotalSum; // Новое поле: общая сумма расходов
        public int netProfit;        // Новое поле: чистая прибыль
        // поля добавления заказа
        public boolean canAdd;       //Флаг, можно ли добавить в партию заказ
        public String messageCanAdd; //Сообщение, можно ли продать + причина

        public BatchAnalysis() {

        }

        public BatchAnalysis(int batchId, int totalCount, int ordersCount, int soldCount, int remainingCount,
                             int batchAmount, int ordersTotalSum, double avgPricePerUnit, double avgPurchasePricePerUnit, int profit,
                             int expensesTotalSum, int netProfit) {
            this.batchId = batchId;
            this.totalCount = totalCount;
            this.ordersCount = ordersCount;
            this.soldCount = soldCount;
            this.remainingCount = remainingCount;
            this.batchAmount = batchAmount;
            this.ordersTotalSum = ordersTotalSum;
            this.avgPricePerUnit = avgPricePerUnit;
            this.avgPurchasePricePerUnit = avgPurchasePricePerUnit;
            this.profit = profit;
            this.expensesTotalSum = expensesTotalSum;
            this.netProfit = netProfit;
        }

        @Override
        public String toString() {
            return "Анализ партии №" + batchId + ":\n" +
                    "Общее количество: " + totalCount + "\n" +
                    "Количество заказов: " + ordersCount + "\n" +
                    "Продано: " + soldCount + "\n" +
                    "Остаток: " + remainingCount + "\n" +
                    "Себестоимость партии: " + batchAmount + "\n" +
                    "Общая сумма заказов: " + ordersTotalSum + "\n" +
                    "Общая сумма расходов: " + expensesTotalSum + "\n" +
                    "Средняя цена продажи: " + String.format("%.2f", avgPricePerUnit) + "\n" +
                    "Средняя закупочная цена: " + String.format("%.2f", avgPurchasePricePerUnit) + "\n" +
                    "Прибыль (без учета расходов): " + profit + "\n" +
                    "Чистая прибыль: " + netProfit;
        }
    }


    public BatchAnalysis analyzeBatch(int batchId) {
        db.connect();
        List<Batch> batches = db.LoadDBButhes();
        Batch batch = null;
        for (Batch b : batches) {
            if (b.getId() == batchId) {
                batch = b;
                break;
            }
        }
        if (batch == null) return null;

        int totalCount = batch.getCount();
        int batchAmount = batch.getAmount();
        int remainingCount = batch.getRemainder();
        List<Order> allOrders = db.LoadDBFilterOrders(batchId);

        // Фильтруем только неотменённые заказы
        List<Order> activeOrders = new ArrayList<>();
        for (Order o : allOrders) {
            String status = o.getStatus();
            if (status == null) status = "";
            if (!status.equalsIgnoreCase("Отменено")) {
                activeOrders.add(o);
            }
        }

        int ordersCount = activeOrders.size();
        int soldCount = 0;
        int ordersTotalSum = 0;
        for (Order o : activeOrders) {
            soldCount += o.getCount();
            ordersTotalSum += o.getCount() * o.getPrice();
        }

        // Расчет общей суммы расходов, связанных с партией
        int expensesTotalSum = 0;
        List<Expenses> allExpenses = db.LoadDBExpenses(); // Загружаем все расходы
        for (Expenses expense : allExpenses) {
            if (expense.getBatchId() == batchId) { // Если расход связан с этой партией
                expensesTotalSum += expense.getAmount();
            }
        }

        double avgPricePerUnit = soldCount > 0 ? (double) ordersTotalSum / soldCount : 0.0;
        double avgPurchasePricePerUnit = totalCount > 0 ? (double) batchAmount / totalCount : 0.0;
        int profit = ordersTotalSum - batchAmount;
        int netProfit = ordersTotalSum - batchAmount - expensesTotalSum; // Чистая прибыль

        return new BatchAnalysis(
                batchId, totalCount, ordersCount, soldCount, remainingCount,
                batchAmount, ordersTotalSum, avgPricePerUnit, avgPurchasePricePerUnit, profit,
                expensesTotalSum, netProfit // Передаем новые значения
        );
    }

}


