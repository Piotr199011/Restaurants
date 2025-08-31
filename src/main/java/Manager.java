public class Manager {

    private String data;
    private String name;
    private int price;

    private int quantity;
    public Manager(String data, String name, int price,int quantity) {
        this.data = data;
        this.name = name;
        this.price = price;
        this.quantity=quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
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
