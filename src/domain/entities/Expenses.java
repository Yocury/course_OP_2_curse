package domain.entities;

public class Expenses {
    private final int id;
    private final String description;
    private final String type;
    private final int amount;
    private final String date;
    private int batchId;

    public Expenses(int id, String description, String type, int amount, String date, int batchId) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.batchId = batchId;
    }

    public String[] toDoubleArray() { // Для записи в таблицу.
        return new String[]{
                String.valueOf(id),     //преобразуем в строку
                description,
                type,
                String.valueOf(amount),  // если count — число
                date,
                String.valueOf(batchId)
        };
    }


    public int getId() { return id; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public int getAmount() { return amount; }
    public String getDate() { return date; }
    public int getBatchId() { return batchId; }

}
