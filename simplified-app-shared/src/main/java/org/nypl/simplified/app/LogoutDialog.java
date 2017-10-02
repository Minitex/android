package org.nypl.simplified.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

import org.nypl.simplified.books.core.AccountLogoutListenerType;
import org.nypl.simplified.books.core.BooksType;
import org.nypl.simplified.books.core.LogUtilities;
import org.slf4j.Logger;

/**
 * A reusable logout dialog.
 */

@SuppressWarnings("synthetic-access") public final class LogoutDialog
  extends DialogFragment implements AccountLogoutListenerType
{
  private static final Logger LOG;

  static {
    LOG = LogUtilities.getLog(DialogFragment.class);
  }

  private @Nullable Runnable on_confirm;

  /**
   * Construct a new dialog.
   */

  public LogoutDialog()
  {
    // Fragments must have no-arg constructors.
  }

  /**
   * @return A new dialog
   */

  public static LogoutDialog newDialog()
  {
    return new LogoutDialog();
  }

  @Override public void onAccountLogoutFailure(
    final OptionType<Throwable> error,
    final String message)
  {
    final String s =
      NullCheck.notNull(String.format("logout failed: %s", message));
    LogUtilities.errorWithOptionalException(LogoutDialog.LOG, s, error);
  }

  @Override public void onAccountLogoutSuccess()
  {
    // Nothing


    final SimplifiedCatalogAppServicesType app =
      Simplified.getCatalogAppServices();

    final BooksType books = app.getBooks();

    books.destroyBookStatusCache();

  }

  @Override
  public void onAccountLogoutFailureServerError(final int code) {
    final String s =
      NullCheck.notNull(String.format("logout failed: %s", code));
    LogUtilities.errorWithOptionalException(LogoutDialog.LOG, s, null);
  }

  @Override public void onResume()
  {
    super.onResume();

    final Resources rr = NullCheck.notNull(this.getResources());
    final int h = (int) rr.getDimension(R.dimen.logout_dialog_height);
    final int w = (int) rr.getDimension(R.dimen.logout_dialog_width);

    final Dialog dialog = NullCheck.notNull(this.getDialog());
    final Window window = NullCheck.notNull(dialog.getWindow());
    window.setLayout(w, h);
    window.setGravity(Gravity.CENTER);
  }

  @Override public void onCreate(
    final @Nullable Bundle state)
  {
    super.onCreate(state);
    this.setStyle(DialogFragment.STYLE_NORMAL, R.style.SimplifiedLoginDialog);
  }

  @Override public View onCreateView(
    final @Nullable LayoutInflater inflater_mn,
    final @Nullable ViewGroup container,
    final @Nullable Bundle state)
  {
    final LayoutInflater inflater = NullCheck.notNull(inflater_mn);

    final ViewGroup layout = NullCheck.notNull(
      (ViewGroup) inflater.inflate(
        R.layout.logout_confirm, container, false));

    final Button in_logout_button =
      NullCheck.notNull((Button) layout.findViewById(R.id.logout_confirm));

    in_logout_button.setOnClickListener(
      new OnClickListener()
      {
        @Override public void onClick(
          final @Nullable View v)
        {
          final Runnable r = LogoutDialog.this.on_confirm;
          LogoutDialog.LOG.debug("runnable: {}", r);
          if (r != null) {
            r.run();
          }
          LogoutDialog.this.dismiss();
        }
      });

    final Button in_logout_cancel_button =
      NullCheck.notNull((Button) layout.findViewById(R.id.logout_cancel));

    in_logout_cancel_button.setOnClickListener(
      new OnClickListener()
      {
        @Override public void onClick(
          final @Nullable View v)
        {
          LogoutDialog.this.dismiss();
        }
      });

    final Dialog d = this.getDialog();
    if (d != null) {
      d.setCanceledOnTouchOutside(true);
    }
    return layout;
  }

  /**
   * Set the confirmation listener.
   *
   * @param r The listener
   */

  public void setOnConfirmListener(
    final Runnable r)
  {
    this.on_confirm = NullCheck.notNull(r);
  }
}
