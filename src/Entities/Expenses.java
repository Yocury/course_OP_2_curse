package Entities;

public class Expenses {
    private final int id;
    private final String description;
    private final String type;
    private final int amount;
    private final String date;

    public Expenses(int id, String description, String type, int amount, String date) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public String[] toDoubleArray() { // Для записи в таблицу.
        return new String[]{
                String.valueOf(id),     //преобразуем в строку
                description,
                type,
                String.valueOf(amount),  // если count — число
                date,
        };
    }

}
