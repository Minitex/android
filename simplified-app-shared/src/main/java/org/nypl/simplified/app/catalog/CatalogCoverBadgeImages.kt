package org.nypl.simplified.app.catalog

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.io7m.jfunctional.Some
import org.nypl.simplified.app.R
import org.nypl.simplified.app.ScreenSizeControllerType
import org.nypl.simplified.books.core.BookFormats
import org.nypl.simplified.books.core.BookFormats.BookFormatDefinition.BOOK_FORMAT_AUDIO
import org.nypl.simplified.books.core.BookFormats.BookFormatDefinition.BOOK_FORMAT_EPUB
import org.nypl.simplified.books.core.FeedEntryOPDS
import org.nypl.simplified.books.covers.BookCoverBadge
import org.nypl.simplified.books.covers.BookCoverBadgeLookupType

/**
 * The images used to add badges to book covers.
 */

class CatalogCoverBadgeImages private constructor(
  private val screenSize: ScreenSizeControllerType,
  private val backgroundColorRGBA: Int?,
  private val audioBookIcon: Bitmap) : BookCoverBadgeLookupType {

  override fun badgeForEntry(entry: FeedEntryOPDS): BookCoverBadge? {
    val formatOpt = entry.probableFormat
    return if (formatOpt is Some<BookFormats.BookFormatDefinition>) {
      val format = formatOpt.get()
      when (format) {
        null,
        BOOK_FORMAT_EPUB -> null
        BOOK_FORMAT_AUDIO ->
          BookCoverBadge(
            bitmap = this.audioBookIcon,
            width = this.screenSize.screenDPToPixels(24).toInt(),
            height = this.screenSize.screenDPToPixels(24).toInt(),
            backgroundColorRGBA = this.backgroundColorRGBA)
      }
    } else {
      null
    }
  }

  companion object {

    /**
     * Create a new set of badge images.
     */

    fun create(
      resources: Resources,
      backgroundColorRGBA: Int?,
      screenSize: ScreenSizeControllerType): BookCoverBadgeLookupType {
      val audioBookIcon = BitmapFactory.decodeResource(resources, R.drawable.audiobook_icon)
      return CatalogCoverBadgeImages(
        audioBookIcon = audioBookIcon,
        backgroundColorRGBA = backgroundColorRGBA,
        screenSize = screenSize)
    }
  }
}
