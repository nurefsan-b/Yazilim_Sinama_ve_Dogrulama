package edu.lab.tdd;

import edu.lab.tdd.model.*;
import edu.lab.tdd.repository.BookRepository;
import edu.lab.tdd.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

import static org.mockito.Mockito.*;

import edu.lab.tdd.exception.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceBorrowTest{
    @Mock BookRepository bookRepository;
    @Mock NotificationService notificationService;
    @InjectMocks LibraryService libraryService;

    private Book availableBook;
    private Book unavailableBook;
    private Member activeMemb;
    private Member inactiveMemb;

    @BeforeEach
    void setUp(){
        availableBook=new Book("ISBN-001","Sefiller",true);
        unavailableBook=new Book("ISBN-002","Notre-Dame",false);
        activeMemb=new Member("M001","Ayşe Kaya",true);
        inactiveMemb=new Member("M002","İbrahim Yıldız",false);
    }

    @Test
    @DisplayName("Uygun kitap aktif üyeye ödünç verildiğinde LoanRecord dönmeli")
    void shouldReturnLoanRecordWhenBookIsAvailable(){
        when(bookRepository.findById("ISBN-001"))
        .thenReturn(Optional.of(availableBook));

        LoanRecord record=libraryService.borrowBook(
            activeMemb,"ISBN-001",LocalDate.of(2024,3,1));

            assertThat(record).isNotNull();
            assertThat(record.getBook().getIsbn()).isEqualTo("ISBN-001");
            assertThat(record.getMember().getMemberId()).isEqualTo("M001");
            assertThat(record.getBorrowDate()).isEqualTo(LocalDate.of(2024,3,1));
    }

    @Test
    @DisplayName("Kitap musait değilse BookNotAvailableException fırlatılmalı")
    void shouldThrowBookNotAvailableExceptionWhenBookIsNotAvailable(){
        when(bookRepository.findById("ISBN-002"))
        .thenReturn(Optional.of(unavailableBook));

        assertThatThrownBy(()->libraryService.borrowBook(activeMemb,"ISBN-002",LocalDate.of(2024,3,1)))
        .isInstanceOf(BookNotAvailableException.class);
    }

    @Test
    @DisplayName("Pasif üye ödünç alamazsa InactiveMemberException fırlatılmalı")
    void shouldThrowInactiveMemberExceptionWhenMemberIsInactive(){
        assertThatThrownBy(()->libraryService.borrowBook(inactiveMemb,"ISBN-001",LocalDate.of(2024,3,1)))
        .isInstanceOf(InactiveMemberException.class);
    }

    @Test
    @DisplayName("Varolmayan kitap ödünç alınamaz - BookNotFoundException fırlatılmalı")
    void shouldThrowBookNotFoundExceptionWhenBookDoesNotExist(){
        when(bookRepository.findById("ISBN-999"))
        .thenReturn(Optional.empty());

        assertThatThrownBy(()->libraryService.borrowBook(activeMemb,"ISBN-999",LocalDate.of(2024,3,1)))
        .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("Ödünç sonrası kitap uygun olmayan durum olmalı")
    void shouldSetBookAsUnavailableAfterBorrow(){
        when(bookRepository.findById("ISBN-001"))
        .thenReturn(Optional.of(availableBook));

        libraryService.borrowBook(activeMemb,"ISBN-001",LocalDate.of(2024,3,1));

        assertThat(availableBook.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Ödünç sonrası bildirim gönderilmeli")
    void shouldSendBorrowConfirmationNotification(){
        when(bookRepository.findById("ISBN-001"))
        .thenReturn(Optional.of(availableBook));

        libraryService.borrowBook(activeMemb,"ISBN-001",LocalDate.of(2024,3,1));

        verify(notificationService,times(1)).sendBorrowConfirmation(activeMemb,"Sefiller");
    }

    @Test
    @DisplayName("Üyenin 3 ödüncü varsa LoanLimitExceededException fırlatılmalı")
    void shouldThrowLoanLimitExceededExceptionWhenMemberHas3ActiveLoans(){
        activeMemb.getLoans().add(new LoanRecord(new Book("ISBN-101","Kitap1",true),activeMemb,LocalDate.of(2024,1,1)));
        activeMemb.getLoans().add(new LoanRecord(new Book("ISBN-102","Kitap2",true),activeMemb,LocalDate.of(2024,1,5)));
        activeMemb.getLoans().add(new LoanRecord(new Book("ISBN-103","Kitap3",true),activeMemb,LocalDate.of(2024,2,1)));

        assertThatThrownBy(()->libraryService.borrowBook(activeMemb,"ISBN-001",LocalDate.of(2024,3,1)))
        .isInstanceOf(LoanLimitExceededException.class);
    }
}
