public class Table {


    private int idTable;
    private boolean isOccupied;

    public Table(int idTable, boolean isOccupied) {
        this.idTable = idTable;
        this.isOccupied = isOccupied;
    }

    public int getIdTable() {
        return idTable;
    }


    public void setIdTable(int idTable) {
        this.idTable = idTable;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public void reserve() {
        if (!isOccupied) {
            isOccupied = true;
            System.out.println(" Stolik nr " + idTable + " został zarezerwowany.");
        } else {
            System.out.println(" Stolik nr " + idTable + " jest już zajęty.");
        }
    }

    // Zwolnienie stolika
    public void free() {
        if (isOccupied) {
            isOccupied = false;
            System.out.println("️ Stolik nr " + idTable + " został zwolniony.");
        } else {
            System.out.println("️ Stolik nr " + idTable + " już jest wolny.");
        }
    }


}
