package org.nypl.simplified.books.core;

import com.io7m.jnull.NullCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BooksControllerDeleteBookDataTask implements Runnable
{
  private static final Logger LOG;

  static {
    LOG = NullCheck.notNull(
      LoggerFactory.getLogger(BooksControllerDeleteBookDataTask.class));
  }

  private final BookDatabaseType     book_database;
  private final BookID               book_id;
  private final BooksStatusCacheType books_status;
  private final boolean              needs_auth;

  BooksControllerDeleteBookDataTask(
    final BooksStatusCacheType in_books_status,
    final BookDatabaseType in_book_database,
    final BookID in_book_id,
    final boolean in_needs_auth)
  {
    this.books_status = NullCheck.notNull(in_books_status);
    this.book_database = NullCheck.notNull(in_book_database);
    this.book_id = NullCheck.notNull(in_book_id);
    this.needs_auth = in_needs_auth;
  }

  @Override public void run()
  {
    try {
      final BookDatabaseEntryType e =
        this.book_database.databaseOpenEntryForWriting(this.book_id);
      e.entryDeleteBookData();

      final BookDatabaseEntrySnapshot snap = e.entryGetSnapshot();
      final BookStatusType status = BookStatus.fromSnapshot(this.book_id, snap);
      this.books_status.booksStatusUpdate(status);

      // destroy entry, this is needed after deletion has been broadcasted, so the book doesn't stay in loans,
      // especially needed for collection without auth requirement where no syn is happening
      if (!this.needs_auth) {
        e.entryDestroy();
      }
    } catch (final Throwable e) {
      BooksControllerDeleteBookDataTask.LOG.error(
        "[{}]: could not destroy book data: ", this.book_id.getShortID(), e);
    }
  }
}
