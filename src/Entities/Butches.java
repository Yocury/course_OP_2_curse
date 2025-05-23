package Entities;

public class Butches {
    private int id;
    private final String provider; // поставщик
    private final String date;
    private int amount; // Стоимость
    private String status; //Состояние
    private int count;
    private final int avgPrice;
    private int remainder;

    public Butches(int id, String provider, String date, int amount, String status, int count, int avgPrice) {
        this.id = id;
        this.provider = provider;
        this.date = date;
        this.amount = amount;
        this.status = status;
        this.count = count;
        this.avgPrice = avgPrice;
    }

    public String[] toDoubleArray() { // Для записи в таблицу.
        return new String[]{
                String.valueOf(id),     //преобразуем в строку
                provider,
                date,
                String.valueOf(amount),  // если count — число
                status,
                String.valueOf(count),
                String.valueOf(avgPrice),
                String.valueOf(remainder)  // добавляем остаток
        };
    }

    public void setRemainder(int remainder) {
        this.remainder = remainder;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRemainder() {
        return remainder;
    }
}
