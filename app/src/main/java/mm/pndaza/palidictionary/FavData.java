package mm.pndaza.palidictionary;

public class FavData {
    public static Boolean isCheckboxShow;
    private int id;
    private String item;
    private String book;
    private boolean isSelected;

    public FavData(int id, String item,String book) {
        this.id = id;
        this.item = item;
        this.book = book;
    }

    public int getId() { return  id;}

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getBook() { return book; }

    public void setBook(String book) {this.book = book; }

    public Boolean getIsCheckboxShow() { return isCheckboxShow; }

    public void setIsCheckboxShow(Boolean flag) { isCheckboxShow = flag; }

    public boolean isSelected() { return isSelected; }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof FavData)) return false;
        FavData o = (FavData) obj;
        return o.id == this.id;
    }

}
