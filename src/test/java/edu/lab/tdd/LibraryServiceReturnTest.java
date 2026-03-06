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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceReturnTest{
    @Mock BookRepository bookRepository;
    @Mock NotificationService notificationService;
    @InjectMocks LibraryService libraryService;

    private Book book;
    private Member member;
    private LoanRecord record;

    @BeforeEach
    void setUp(){
        book = new Book("ISBN-002", "Suç ve Ceza", false);
        member=new Member("M002", "Mehmet Demir", true);
        record=new LoanRecord(book,member,LocalDate.of(2024,3,1));
        member.getLoans().add(record);
    }

    @Test  
    @DisplayName("İade edilince kitap tekrar uygun hale gelmeli")
    void shouldMarkBookAsAvailableAfterReturn(){
        libraryService.returnBook(record, LocalDate.of(2024,3,10));

        assertThat(book.isAvailable()).isTrue();
        assertThat(record.getReturnDate()).isEqualTo(LocalDate.of(2024,3,10));
    }

    @Test
    @DisplayName("İade sonrası üyenin ödünç listesinden kaldırılmalı")
    void shouldRemoveLoanFromMemberAfterReturn(){
        libraryService.returnBook(record, LocalDate.of(2024,3,10));

        assertThat(member.getLoans()).doesNotContain(record);
    }

    @Test
    @DisplayName("İade sonrası bildirim gönderilmeli")
    void shouldSendReturnConfirmationNotification(){
        libraryService.returnBook(record, LocalDate.of(2024,3,10));

        verify(notificationService).sendReturnConfirmation(member, book.getTitle());
    }
}
