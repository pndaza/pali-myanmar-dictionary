package mm.pndaza.palidictionary;

public class Book {

    public  static String getBookName(String book,Boolean isUnicode){
        String book_name = "";

        if (isUnicode) {
            if (book.equals( "1"))
                book_name = "တိပိ";
            else if (book.equals( "2"))
                book_name = "ဟုတ်စိန်";
            else if (book.equals( "3"))
                book_name = "ဓာတွတ္ထ";
            else if (book.equals( "4"))
                book_name = "ဓာတု";

        } else {
            if (book.equals( "1"))
                book_name = "တိပိ";
            else if (book.equals( "2"))
                book_name = "ဟုတ္စိန္";
            else if (book.equals( "3"))
                book_name = "ဓာတြတၳ";
            else if (book.equals( "4"))
                book_name = "ဓာတု";
        }


        return book_name;
    }
}
