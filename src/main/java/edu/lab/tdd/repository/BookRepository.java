package edu.lab.tdd.repository;
import java.util.Optional;
import edu.lab.tdd.model.Book;


public interface BookRepository{
    Optional<Book>findById(String isbn);
    void save(Book book);
}



