
public class Dish {
    String nameDish;
    int priceDish;
    String compositionOfDish;
    int number;
    int id;
    boolean isReady;
    boolean isServed;


    public Dish(int number, int id, String nameDish, String compositionOfDish, int priceDish, boolean isReady) {
        this.id = id;
        this.nameDish = nameDish;
        this.priceDish = priceDish;
        this.compositionOfDish = compositionOfDish;
        this.number = number;
        this.isReady = isReady;
    }

    public void setServed(boolean served) {
        isServed = served;
    }

    public boolean isServed() {
        return isServed;
    }

    public String getNameDish() {
        return nameDish;
    }

    public int getPriceDish() {
        return priceDish;
    }

    public String getCompositionOfDish() {
        return compositionOfDish;
    }

    public int getNumber() {
        return number;
    }

    public int getId() {
        return id;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    @Override
    public String toString() {
        return id + ": " + "Name of the dish: " + nameDish +
                " components: " + compositionOfDish + " " + priceDish + "pln" +
                " [" + (isReady ? "Gotowe" : "W trakcie") + "]";
    }
}
