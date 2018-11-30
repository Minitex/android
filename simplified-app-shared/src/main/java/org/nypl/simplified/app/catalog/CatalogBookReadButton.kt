package org.nypl.simplified.app.catalog

import android.app.Activity
import android.content.res.Resources
import android.support.v4.content.ContextCompat
import com.io7m.jfunctional.Some
import com.io7m.junreachable.UnreachableCodeException
import org.nypl.simplified.app.R
import org.nypl.simplified.app.R.string.catalog_accessibility_book_download
import org.nypl.simplified.app.R.string.catalog_accessibility_book_listen
import org.nypl.simplified.app.R.string.catalog_accessibility_book_read
import org.nypl.simplified.app.R.string.catalog_book_download
import org.nypl.simplified.app.R.string.catalog_book_listen
import org.nypl.simplified.app.R.string.catalog_book_read
import org.nypl.simplified.app.Simplified
import org.nypl.simplified.app.ThemeMatcher
import org.nypl.simplified.books.core.BookDatabaseEntryFormatSnapshot
import org.nypl.simplified.books.core.BookDatabaseEntryFormatSnapshot.BookDatabaseEntryFormatSnapshotAudioBook
import org.nypl.simplified.books.core.BookDatabaseEntryFormatSnapshot.BookDatabaseEntryFormatSnapshotEPUB
import org.nypl.simplified.books.core.BookDatabaseEntryFormatSnapshot.BookDatabaseEntryFormatSnapshotPDF
import org.nypl.simplified.books.core.BookDatabaseEntrySnapshot
import org.nypl.simplified.books.core.BookID
import org.nypl.simplified.books.core.BooksType
import org.nypl.simplified.books.core.DeviceActivationListenerType
import org.nypl.simplified.books.core.FeedEntryOPDS

/**
 * A button that opens a given book for reading.
 */

class CatalogBookReadButton(
  activity: Activity,
  bookID: BookID,
  feedEntry: FeedEntryOPDS,
  books: BooksType) : CatalogLeftPaddedButton(activity), CatalogBookButtonType {

  init {
    this.textView.textSize = 12.0f
    this.setBackgroundResource(R.drawable.simplified_button)

    val resID = ThemeMatcher.color(Simplified.getCurrentAccount().mainColor)
    val mainColor = ContextCompat.getColor(this.context, resID)
    this.textView.setTextColor(mainColor)

    val readable = bookIsReadable(books, bookID)
    if (readable != null) {
      setButtonForRead(activity, bookID, feedEntry, readable, activity.resources)
    } else {
      setButtonForDownload(bookID, feedEntry, books, activity.resources)
    }
  }

  private fun bookIsReadable(
    books: BooksType,
    bookID: BookID): BookDatabaseEntrySnapshot? {

    val snapshotOpt =
      books.bookGetDatabase().databaseGetEntrySnapshot(bookID)

    if (snapshotOpt is Some<BookDatabaseEntrySnapshot>) {
      val snapshot = snapshotOpt.get()
      if (snapshot.isDownloaded) {
        return snapshot
      }
    }

    return null
  }

  private fun setButtonForDownload(
    bookID: BookID,
    feedEntry: FeedEntryOPDS,
    books: BooksType,
    resources: Resources) {

    this.textView.text = resources.getString(catalog_book_download)
    this.textView.contentDescription = resources.getString(catalog_accessibility_book_download)

    this.setOnClickListener { view ->
      val listener = object : DeviceActivationListenerType {
        override fun onDeviceActivationFailure(message: String) {}
        override fun onDeviceActivationSuccess() {}
      }
      books.accountActivateDeviceAndFulFillBook(bookID, feedEntry.feedEntry.licensor, listener)
    }
  }

  private fun setButtonForRead(
    activity: Activity,
    bookID: BookID,
    feedEntry: FeedEntryOPDS,
    snapshot: BookDatabaseEntrySnapshot,
    resources: Resources) {

    this.textView.text = resources.getString(catalog_book_read)
    this.textView.contentDescription = resources.getString(catalog_accessibility_book_read)
    this.setOnClickListener(CatalogBookRead(activity, bookID, feedEntry))

    val formatOpt = snapshot.findPreferredFormat()
    if (formatOpt is Some<BookDatabaseEntryFormatSnapshot>) {
      val format = formatOpt.get()
      return when (format) {
        is BookDatabaseEntryFormatSnapshotEPUB -> Unit
        is BookDatabaseEntryFormatSnapshotAudioBook -> {
          this.textView.text = resources.getString(catalog_book_listen)
          this.textView.contentDescription = resources.getString(catalog_accessibility_book_listen)
        }
        is BookDatabaseEntryFormatSnapshotPDF -> Unit
      }
    } else {
      throw UnreachableCodeException()
    }
  }
}
