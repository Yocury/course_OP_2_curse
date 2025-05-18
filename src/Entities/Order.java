package Entities;

public class Order {
    private int id; // айди
    private String sourse; //Источник заказа
    private int count; // количество товара
    private String street; // улица
    private String building; // дом
    private int id_batches; //
    private String date; //Дата
    private String number; // Номер телефона клиента
    private String status; // состояние заказа
    private int price;

    public String[] toDoubleArray() { // Для записи в таблицу.
        return new String[]{
                String.valueOf(id),     //преобразуем в строку
                sourse,
                String.valueOf(count),  // если count — число
                street,
                building,
                String.valueOf(id_batches), // если id_batches — число
                date,
                number,
                status,
                String.valueOf(price)
        };
    }


    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public String getStatus() {
        return status;
    }

    public Order(int id, String sourse, int count, String street, String building, int id_batches, String date, String number, String status, int price) {
        this.id = id;
        this.sourse = sourse;
        this.count = count;
        this.street = street;
        this.building = building;
        this.id_batches = id_batches;
        this.date = date;
        this.number = number;
        this.status = status;
        this.price = price;
    }
}