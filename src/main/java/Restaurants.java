import java.util.ArrayList;

public class Restaurants {
    ArrayList<String> menu = new ArrayList<>();


    public String choseMeal(String langCode, int numberMeal) {
        switch (numberMeal) {
            case 1: return "menu_" + langCode + "_sniadanie.xlsx";
            case 2: return "menu_" + langCode + "_obiad.xlsx";
            case 3: return "menu_" + langCode + "_kolacja.xlsx";
            case 4: return "menu_" + langCode + "_deser.xlsx";
        }
        return null;
    }

    public void showMenu(String adress) {
        System.out.println("=== MENU RESTAURACJI ===");
        ExcelReader reader = new ExcelReader();
        ArrayList<Dish> menu = reader.readExcel(adress);
        for (Dish item : menu) {
            System.out.println(item);
        }

    }


}


