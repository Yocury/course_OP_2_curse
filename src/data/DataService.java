package data;

import domain.entities.Batch;
import domain.entities.Expenses;
import domain.entities.Order;

import java.util.ArrayList;
import java.util.List;

public class DataService {

    protected DBManager db = new DBManager();


    public List<String[]> getOrdersFromDB() {
        db.connect();
        List<Order> ordersDB;
        ordersDB = db.LoadDBOrders();
        List<String[]> orders = new ArrayList<>();
        for (Order order : ordersDB) {
            orders.add(order.toDoubleArray());
        }
        return orders;
    }

    public List<String[]> getExpensesFromDB() {
        db.connect();
        List<Expenses> expensesDB;
        expensesDB = db.LoadDBExpenses();
        List<String[]> expenses = new ArrayList<>();
        for (Expenses value : expensesDB) {
            expenses.add(value.toDoubleArray());
        }
        return expenses;
    }

    public List<String[]> getFilterExpensesFromDB(String type) {
        db.connect();
        List<Expenses> expensesDB;
        expensesDB = db.LoadFilterExpensesTypes(type);
        List<String[]> expenses = new ArrayList<>();
        for (Expenses value : expensesDB) {
            expenses.add(value.toDoubleArray());
        }
        return expenses;
    }

    public List<String[]> getFilterOrdersDB(String id) {
        int butchesId = Integer.parseInt(id);
        db.connect();
        List<Order> ordersDB;
        ordersDB = db.LoadDBFilterOrders(butchesId);
        List<String[]> orders = new ArrayList<>();
        for (Order order : ordersDB) {
            orders.add(order.toDoubleArray());
        }
        return orders;
    }

    public Batch getBatchById(int batchId) {
        db.connect();
        List<Batch> batches = db.LoadDBButhes();
        for (Batch batch : batches) {
            if (batch.getId() == batchId) {
                return batch;
            }
        }
        return null; // Партия не найдена
    }


    public List<String[]> getButchesFromDB() {
        db.connect();
        List<Batch> ButhesDB;
        ButhesDB = db.LoadDBButhes();
        List<String[]> butches = new ArrayList<>();
        for (Batch value : ButhesDB) {
            // Получаем все заказы для текущей партии
            List<Order> orders = db.LoadDBFilterOrders(value.getId());
            int soldCount = 0;
            // Подсчитываем общее количество проданного товара
            for (Order order : orders) {
                if (!order.getStatus().equalsIgnoreCase("Отменено")) {
                    soldCount += order.getCount();
                }
            }
            // Вычисляем остаток
            int remainder = value.getCount() - soldCount;
            value.setRemainder(remainder);
            butches.add(value.toDoubleArray());
        }
        return butches;
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
        // поля добавления заказа
        public boolean canAdd;       //Флаг, можно ли добавить в партию заказ
        public String messageCanAdd; //Сообщение, можно ли продать + причина

        public BatchAnalysis() {

        }

        public void canAddOrder(int batchId, int orderCount) {
            Batch batch = getBatchById(batchId);
            if (batch == null) {
                this.canAdd = false;
                this.messageCanAdd = "Партия не найдена";
                return;
            }
            if (!batch.getStatus().equalsIgnoreCase("В продаже")) { //
                this.messageCanAdd = "Партия не в продаже";
                this.canAdd = false;
                return;
            }

            // Получаем сумму уже проданного товара из заказов по этой партии
            List<Order> orders = this.db.LoadDBFilterOrders(batchId);
            int soldCount = 0;
            for (Order order : orders) {
                soldCount += order.getCount();
            }

            int availableCount = batch.getCount() - soldCount;
            if (orderCount > availableCount) {
                this.canAdd = false;
                this.messageCanAdd = "Недостаточно товара в партии. Доступно: " + availableCount;
                return;
            }

            canAdd = true;
        }

        public BatchAnalysis(int batchId, int totalCount, int ordersCount, int soldCount, int remainingCount,
                             int batchAmount, int ordersTotalSum, double avgPricePerUnit, double avgPurchasePricePerUnit, int profit) {
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
        double avgPricePerUnit = soldCount > 0 ? (double) ordersTotalSum / soldCount : 0.0;
        double avgPurchasePricePerUnit = totalCount > 0 ? (double) batchAmount / totalCount : 0.0;
        int profit = ordersTotalSum - batchAmount;

        return new BatchAnalysis(
                batchId, totalCount, ordersCount, soldCount, remainingCount,
                batchAmount, ordersTotalSum, avgPricePerUnit, avgPurchasePricePerUnit, profit
        );
    }

}


