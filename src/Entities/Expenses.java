package Entities;

public class Expenses {
    private int id;
    private String description;
    private String type;
    private int amount;
    private String date;

    public Expenses(int id, String description, String type, int amount, String date) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public String[] toDoubleArray() { // Для записи в таблицу.
        return new String[] {
                String.valueOf(id),     //преобразуем в строку
                description,
                type,
                String.valueOf(amount),  // если count — число
                date,
        };
    }

}
