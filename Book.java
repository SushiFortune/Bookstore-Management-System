import java.io.Serializable;

/**
 * Book class creates Book objects with title, author, isbn, genre, price and year attributes
 * @author raniam
 * @version 1.0
 *
 */
public class Book implements Serializable {
    private String title, author, isbn, genre;
    private double price;
    private int year;

    public Book(){
        title = "";
        author = "";
        price = 0.0;
        isbn = "";
        genre = "";
        year = 0;
    }
    public Book(String t, String a, double p, String i, String g, int y){
        title = t;
        author = a;
        price = p;
        isbn = i;
        genre = g;
        year = y;
    }

    //getters
    /**
     * getTitle() returns title of book record
     * @return title of book record
     */
    public String getTitle(){
        return title;
    }

    /**
     * getAuthor() returns authors names of book record
     * @return authors names of book record
     */
    public  String getAuthor(){
        return author;
    }

    /**
     * getPrice() returns price of book record
     * @return price of book record
     */
    public double getPrice(){
        return price;
    }

    /**
     * getIsbn() returns ISBN value of book record of stops between starting and destination stations
     * @return ISBN value of book record
     */
    public String getIsbn(){
        return isbn;
    }

    /**
     * getGenre() returns genre of book record
     * @return genre of book record
     */
    public String getGenre(){
        return genre;
    }

    /**
     * getYear() returns year of book record
     * @return year of book record
     */
    public int getYear(){
        return year;
    }

    //setters
    /**
     * setTitle() sets the title of book record to the parameter value
     *@param t passed String for title
     */
    public void setTitle(String t){
        title = t;
    }

    /**
     *setAuthor() sets the author of book record to the parameter value
     *@param a passed String for author
     */
    public void setAuthor(String a){
        author = a;
    }

    /**
     *setPrice() sets the price of book record to the parameter value
     *@param p passed double value for price
     */
    public void setPrice(double p){
        price = p;
    }

    /**
     *setIsbn() sets the isbn of book record to the parameter value
     *@param i passed String for isbn
     */
    public void setIsbn(String i){
        isbn = i;
    }

    /**
     *setGenre() sets the genre of book record to the parameter value
     *@param g passed String for genre
     */
    public void setGenre(String g){
        genre = g;
    }

    /**
     *setYear() sets the year of book record to the parameter value
     *@param y passed int value for year
     */
    public void setYear(int y){
        year = y;
    }

    /**
     * toString() returns a string describing attributes of a book object
     * @return string
     */
    @Override
    public String toString(){
        return title + ", " + author + ", " + price + ", " + isbn + ", " + genre + ", " + year;
    }
    @Override
    /**
     * equals() returns a boolean value, true if two book objects  have the same attributes; and false if they either don't have the same attributes, or the passed object is not from the same class or is null
     * @param obj Abstract object can be an object from any class
     * @return true or false
     * */
    public boolean equals(Object obj){
        if(obj == null || obj.getClass() != this.getClass()){
            return false;
        }
        Book newBook = (Book) obj;
        return(title.equals(newBook.title) && author.equals(newBook.author) && (price - newBook.price) < 0.00000001 && isbn.equals(newBook.isbn) && genre.equals(newBook.genre) && year == newBook.year);
    }

}



