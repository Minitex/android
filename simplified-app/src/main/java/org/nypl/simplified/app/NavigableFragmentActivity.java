package org.nypl.simplified.app;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.BackStackEntry;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * <p>
 * The type of activities that contain a navigable stack of fragments.
 * </p>
 * <p>
 * The action bar home button is set to display the title of the current
 * fragment on the stack, and to display a caret to indicate that backwards
 * navigation is possible when the fragment stack is non-empty.
 * </p>
 */

@SuppressWarnings("synthetic-access") abstract class NavigableFragmentActivity extends
  PartActivity
{
  private static void displayOrHideUpCaretAsNecessary(
    final Resources rr,
    final FragmentManager fm,
    final ActionBar bar)
  {
    final int count = fm.getBackStackEntryCount();
    final boolean stack_nonempty = count > 0;
    if (stack_nonempty) {
      final BackStackEntry e = fm.getBackStackEntryAt(count - 1);
      bar.setTitle(e.getName());
    } else {
      bar.setTitle(rr.getString(R.string.app_name));
    }

    bar.setDisplayHomeAsUpEnabled(stack_nonempty);
  }

  /**
   * Create an initial fragment that will be displayed in the content area.
   *
   * @param container
   *          The ID of the container area
   * @return A new initial fragment
   */

  protected abstract NavigableFragment newInitialFragment(
    int container);

  @Override protected void onCreate(
    final @Nullable Bundle state)
  {
    super.onCreate(state);

    final Resources rr = NullCheck.notNull(this.getResources());
    final FragmentManager fm = this.getFragmentManager();
    final ActionBar bar = this.getActionBar();
    bar.setDisplayShowTitleEnabled(true);

    fm.addOnBackStackChangedListener(new OnBackStackChangedListener() {
      @Override public void onBackStackChanged()
      {
        NavigableFragmentActivity
          .displayOrHideUpCaretAsNecessary(rr, fm, bar);
      }
    });

    final Fragment f = fm.findFragmentById(R.id.content_area);
    if (f == null) {
      final NavigableFragment fn = this.newInitialFragment(R.id.content_area);
      final FragmentTransaction ft = fm.beginTransaction();
      ft.add(R.id.content_area, fn);
      ft.commit();
    }

    NavigableFragmentActivity.displayOrHideUpCaretAsNecessary(rr, fm, bar);
  }
}
