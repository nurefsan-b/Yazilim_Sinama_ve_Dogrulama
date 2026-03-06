package edu.lab.tdd.service;
import edu.lab.tdd.repository.BookRepository;
import edu.lab.tdd.exception.*;
import java.time.LocalDate;
import edu.lab.tdd.model.*;


public class LibraryService{
    private final BookRepository bookRepository;
    private final NotificationService notificationService;
    private static final int MAX_ACTIVE_LOANS = 3;

        public LibraryService(BookRepository bookRepository,NotificationService notificationService){
            this.bookRepository=bookRepository;
            this.notificationService=notificationService;
        }

        public LoanRecord borrowBook(Member member,String isbn,LocalDate borrowDate){
            // 1. Üyenin aktif olup olmadığını kontrol et
            if(!member.isActive()){
                throw new InactiveMemberException("Pasif üyeler kitap ödünç alamazlar");
            }

            // 2. Üyenin 3 aktif ödüncü var mı kontrol et
            if(member.getActiveLoanCount() >= MAX_ACTIVE_LOANS){
                throw new LoanLimitExceededException("Üye en fazla 3 aktif ödünç yapabilir");
            }

            // 3. Kitabı veritabanından bul
            Book book = bookRepository.findById(isbn)
                .orElseThrow(()->new BookNotFoundException("Kitap bulunamadı"));

            // 4. Kitabın musait olup olmadığını kontrol et
            if(!book.isAvailable()){
                throw new BookNotAvailableException("Kitap uygun değil");
            }

            // 5. Ödünç kaydı oluştur
            LoanRecord record = new LoanRecord(book,member,borrowDate);

            // 6. Kitabı uygun olmayan durumuna ayarla
            book.setIsAvailable(false);

            // 7. Üyenin ödünç listesine ekle
            member.getLoans().add(record);

            // 8. Bildirim gönder
            notificationService.sendBorrowConfirmation(member,book.getTitle());

            return record;
        }

        public void returnBook(LoanRecord record,LocalDate returnDate){
            // 1. İade tarihini ayarla
            record.setReturnDate(returnDate);

            // 2. Kitabı uygun hale getir
            record.getBook().setIsAvailable(true);

            // 3. Üyenin ödünç listesinden kaldır
            record.getMember().getLoans().remove(record);

            // 4. İade bildirimi gönder
            notificationService.sendReturnConfirmation(record.getMember(), record.getBook().getTitle());

            // 5. Geç iade ise ceza hesapla (ama henüz implemente edilmedi)
            // double fine = calculateFine(record, returnDate);
        }
        public double calculateFine(LoanRecord record,LocalDate today){
            if (today.isBefore(record.getBorrowDate())) {
                throw new IllegalArgumentException("Today cannot be before borrow date");
            }
            LocalDate effectiveReturnDate = record.getReturnDate() != null ? record.getReturnDate() : today;
            LocalDate dueDate = record.getBorrowDate().plusDays(14);
            long overdueDays = effectiveReturnDate.isAfter(dueDate) ? effectiveReturnDate.toEpochDay() - dueDate.toEpochDay() : 0;
            return overdueDays * 0.5; // 0.5 TL per day
        }
    }
