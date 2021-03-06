package org.nypl.simplified.tests.books

import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import com.io7m.jfunctional.Option
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.nypl.simplified.books.core.FeedLoaderType
import org.nypl.simplified.books.core.FeedWithoutGroups
import java.net.URI
import java.util.concurrent.Executors

abstract class FeedLoaderContract {

  abstract fun createFeedLoader(exec: ListeningExecutorService) : FeedLoaderType

  abstract fun resource(name: String): URI

  private lateinit var exec: ListeningExecutorService

  @Before
  fun setup()
  {
    this.exec = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1))
  }

  @After
  fun tearDown()
  {
    this.exec.shutdown()
  }

  /**
   * An entry with no usable acquisitions should not appear in a feed at all.
   */

  @Test
  fun testFeedWithZeroAcquisitions()
  {
    val loader =
      this.createFeedLoader(this.exec)
    val future =
      loader.fetchURI(resource("feed-no-usable-acquisitions.xml"), Option.none())
    val result =
      future.get()

    Assert.assertTrue(result is FeedWithoutGroups)
    val feed = result as FeedWithoutGroups
    Assert.assertEquals(0, feed.size)
  }
}
