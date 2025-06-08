package domain.entities;

public class Order {
    private final int id; // айди
    private final String sourse; //Источник заказа
    private final int count; // количество товара
    private final String street; // улица
    private final String building; // дом
    private final int id_batches; //
    private final String date; //Дата
    private final String number; // Номер телефона клиента


    private String status; // состояние заказа
    private final int price;

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



    public Order(int id, String sourse, int count, String street, String building, int id_batches,
                 String date, String number, String status, int price) {
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

    public int getPrice() {return price;}

    public int getCount() {return count;}

    public int getId() {return id;}

    public String getSourse() {return sourse;}

    public String getStreet() {return street;}

    public String getBuilding() {return building;}

    public int getId_batches() {return id_batches;}

    public String getDate() {return date;}

    public String getNumber() {return number;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    // Конструктор без параметров
    public Order() {
        this.id = 0;
        this.sourse = "";
        this.count = 0;
        this.street = "";
        this.building = "";
        this.id_batches = 0;
        this.date = "";
        this.number = "";
        this.status = "";
        this.price = 0;
    }

    public void setSourse(String sourse) {
        // Для совместимости с presentation, не изменяет final поле
    }
    public void setCount(int count) {}
    public void setStreet(String street) {}
    public void setBuilding(String building) {}
    public void setIdBatches(int id_batches) {}
    public void setDate(String date) {}
    public void setNumber(String number) {}
}