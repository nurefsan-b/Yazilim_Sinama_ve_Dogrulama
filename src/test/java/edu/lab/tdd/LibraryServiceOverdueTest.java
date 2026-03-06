package edu.lab.tdd;

import edu.lab.tdd.model.*;
import edu.lab.tdd.repository.BookRepository;
import edu.lab.tdd.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceOverdueTest{
    @Mock BookRepository bookRepository;
    @Mock NotificationService notificationService;
    @InjectMocks LibraryService libraryService;

    @Test
    @DisplayName("İade edilmemiş kayıtta today parametresi baz alınmalı - 6 günlük ceza")
    void shouldCalculateFineBasedOnTodayWhenNotReturned(){
        Book book=new Book("ISBN-003","Bülbülü Öldürmek",false);
        Member member=new Member("M003", "Fatma Şahin", true);
        LoanRecord record=new LoanRecord(book, member, LocalDate.of(2024,3,1));
        double fine=libraryService.calculateFine(record, LocalDate.of(2024,3,21));
        assertThat(fine).isEqualTo(3.0); 
    }

    @Test
    @DisplayName("Tam 14. günde iade edilirse ceza sıfır")
    void shouldReturnZeroFineWhenReturnedOn14thDay(){
        Book book=new Book("ISBN-003","Bülbülü Öldürmek",false);
        Member member=new Member("M003", "Fatma Şahin", true);
        LoanRecord record=new LoanRecord(book, member, LocalDate.of(2024,3,1)); 
        record.setReturnDate(LocalDate.of(2024,3,15)); 
        double fine=libraryService.calculateFine(record, LocalDate.of(2024,3,21)); 
        assertThat(fine).isEqualTo(0.0);
    }

    @Test
    @DisplayName("15. günde iade edilirse ceza 0.50 TL")
    void shouldCalculateHalfFineWhenReturnedOn15thDay(){
        Book book=new Book("ISBN-003","Bülbülü Öldürmek",false);
        Member member=new Member("M003", "Fatma Şahin", true);
        LoanRecord record=new LoanRecord(book, member, LocalDate.of(2024,3,1)); 
        record.setReturnDate(LocalDate.of(2024,3,16)); 
        double fine=libraryService.calculateFine(record, LocalDate.of(2024,3,21));
        assertThat(fine).isEqualTo(0.5); 
    }

    @Test
    @DisplayName("Negatif süre gönderilirse exception fırlatılmalı")
    void shouldThrowExceptionWhenTodayBeforeBorrowDate(){
        Book book=new Book("ISBN-003","Bülbülü Öldürmek",false);
        Member member=new Member("M003", "Fatma Şahin", true);
        LoanRecord record=new LoanRecord(book, member, LocalDate.of(2024,3,1)); 
        assertThatThrownBy(() -> libraryService.calculateFine(record, LocalDate.of(2024,2,28)))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
