public class Manager {

    private int data;
    private String name;
    private int price;


    public Manager(int data, String name, int price) {
        this.data = data;
        this.name = name;
        this.price = price;
    }
    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // Opcjonalnie dla debugowania
    @Override
    public String toString() {
        return "Manager{" +
                "data=" + data +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
