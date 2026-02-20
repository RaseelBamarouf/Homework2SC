public class Book {
private final String title;
private final String author;
private final String isbn;
private final int copies;

public Book (String title , String author , String isbn , int copies){
    this.title=title;
    this.author=author;
    this.isbn=isbn;
    this.copies=copies;
}

public String getTitle (){
return title;
}
public String getAuthor (){
return author;
}
public String getIsbn (){
return isbn;
}
public int getCopies(){
return copies;
}

public String fileFormat(){
    return title + ":" + author + ":" + isbn + ":" + copies;
}
public void printHeader() {
        System.out.printf("%-30s %-20s %-15s %5s%n", "Title", "Author", "ISBN", "Copies");
        System.out.println("---------------------------------------------------------------------------");
    }
public void printFormet() {
    System.out.printf ("%-30s %-20s %-15s %5d%n" , title , author , isbn , copies); 
}

   @Override
public String toString() {
    return title + " | " + author + " | " + isbn + " | " + copies;
}
}

