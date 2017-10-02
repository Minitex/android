package org.nypl.simplified.app.reader;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import org.nypl.simplified.app.R;
import org.nypl.simplified.app.Simplified;
import org.nypl.simplified.app.SimplifiedReaderAppServicesType;

/**
 * The reader settings dialog, allowing the selection of colors and fonts.
 */

public final class ReaderSettingsDialog extends DialogFragment
{
  /**
   * Construct a dialog.
   */

  public ReaderSettingsDialog()
  {

  }

  @Override public void onCreate(
    final @Nullable Bundle state)
  {
    super.onCreate(state);
    this.setStyle(DialogFragment.STYLE_NORMAL, R.style.SimplifiedBookDialog);
  }

  @Override public View onCreateView(
    final @Nullable LayoutInflater inflater_mn,
    final @Nullable ViewGroup container,
    final @Nullable Bundle state)
  {
    final LayoutInflater inflater = NullCheck.notNull(inflater_mn);
    final LinearLayout layout = NullCheck.notNull(
      (LinearLayout) inflater.inflate(
        R.layout.reader_settings, container, false));

    final TextView in_view_font_serif = NullCheck.notNull(
      (TextView) layout.findViewById(
        R.id.reader_settings_font_serif));
    final TextView in_view_font_sans = NullCheck.notNull(
      (TextView) layout.findViewById(
        R.id.reader_settings_font_sans));
    final TextView in_view_font_open_dyslexic = NullCheck.notNull(
      (TextView) layout.findViewById(
        R.id.reader_settings_font_open_dyslexic));

    final TextView in_view_black_on_white = NullCheck.notNull(
      (TextView) layout.findViewById(
        R.id.reader_settings_black_on_white));
    final TextView in_view_white_on_black = NullCheck.notNull(
      (TextView) layout.findViewById(
        R.id.reader_settings_white_on_black));
    final TextView in_view_black_on_beige = NullCheck.notNull(
      (TextView) layout.findViewById(
        R.id.reader_settings_black_on_beige));

    final TextView in_view_text_smaller = NullCheck.notNull(
      (TextView) layout.findViewById(
        R.id.reader_settings_text_smaller));
    final TextView in_view_text_larger = NullCheck.notNull(
      (TextView) layout.findViewById(
        R.id.reader_settings_text_larger));

    final SeekBar in_view_brightness = NullCheck.notNull(
      (SeekBar) layout.findViewById(
        R.id.reader_settings_brightness));

    final Button in_view_close_button = NullCheck.notNull((Button) layout.findViewById(R.id.reader_settings_close));

    /**
     * Configure the settings buttons.
     */

    final SimplifiedReaderAppServicesType rs =
      Simplified.getReaderAppServices();
    final ReaderSettingsType settings = rs.getSettings();

    in_view_font_serif.setOnClickListener(
      new OnClickListener()
      {
        @Override public void onClick(final View v)
        {
          settings.setFontFamily(ReaderFontSelection.READER_FONT_SERIF);
        }
      });
    in_view_font_sans.setOnClickListener(
      new OnClickListener()
      {
        @Override public void onClick(final View v)
        {
          settings.setFontFamily(ReaderFontSelection.READER_FONT_SANS_SERIF);
        }
      });
    in_view_font_open_dyslexic.setOnClickListener(
      new OnClickListener()
      {
        @Override public void onClick(final View v)
        {
          settings.setFontFamily(ReaderFontSelection.READER_FONT_OPEN_DYSLEXIC);
        }
      });

    final Typeface od = Typeface.createFromAsset(
      this.getActivity().getAssets(), "OpenDyslexic3-Regular.ttf");
    in_view_font_open_dyslexic.setTypeface(od);

    in_view_black_on_white.setBackgroundColor(
      ReaderColorScheme.SCHEME_BLACK_ON_WHITE.getBackgroundColor());
    in_view_black_on_white.setTextColor(
      ReaderColorScheme.SCHEME_BLACK_ON_WHITE.getForegroundColor());
    in_view_black_on_white.setOnClickListener(
      new OnClickListener()
      {
        @Override public void onClick(
          final @Nullable View v)
        {
          settings.setColorScheme(ReaderColorScheme.SCHEME_BLACK_ON_WHITE);
        }
      });

    in_view_white_on_black.setBackgroundColor(
      ReaderColorScheme.SCHEME_WHITE_ON_BLACK.getBackgroundColor());
    in_view_white_on_black.setTextColor(
      ReaderColorScheme.SCHEME_WHITE_ON_BLACK.getForegroundColor());
    in_view_white_on_black.setOnClickListener(
      new OnClickListener()
      {
        @Override public void onClick(
          final @Nullable View v)
        {
          settings.setColorScheme(ReaderColorScheme.SCHEME_WHITE_ON_BLACK);
        }
      });

    in_view_black_on_beige.setBackgroundColor(
      ReaderColorScheme.SCHEME_BLACK_ON_BEIGE.getBackgroundColor());
    in_view_black_on_beige.setTextColor(
      ReaderColorScheme.SCHEME_BLACK_ON_BEIGE.getForegroundColor());
    in_view_black_on_beige.setOnClickListener(
      new OnClickListener()
      {
        @Override public void onClick(
          final @Nullable View v)
        {
          settings.setColorScheme(ReaderColorScheme.SCHEME_BLACK_ON_BEIGE);
        }
      });

    in_view_text_larger.setOnClickListener(
      new OnClickListener()
      {
        @Override public void onClick(
          final @Nullable View v)
        {
          if (settings.getFontScale() < 250) {
            settings.setFontScale(settings.getFontScale() + 25.0f);
          }
        }
      });

    in_view_text_smaller.setOnClickListener(
      new OnClickListener()
      {
        @Override public void onClick(
          final @Nullable View v)
        {
          if (settings.getFontScale() > 75) {
            settings.setFontScale(settings.getFontScale() - 25.0f);
          }
        }
      });

    in_view_close_button.setOnClickListener(
      new OnClickListener() {
        @Override
        public void onClick(
          final @Nullable View v) {
            ReaderSettingsDialog.this.dismiss();
        }
      }
    );
    /**
     * Configure brightness controller.
     */

    final int brightness = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("reader_brightness", 50);
    in_view_brightness.setProgress(brightness);
    in_view_brightness.setOnSeekBarChangeListener(
      new OnSeekBarChangeListener()
      {
        @Override public void onProgressChanged(
          final @Nullable SeekBar bar,
          final int progress,
          final boolean from_user)
        {
          final float back_light_value = (float) progress / 100;

          final WindowManager.LayoutParams layout_params = getActivity().getWindow().getAttributes();
          layout_params.screenBrightness = back_light_value;
          getActivity().getWindow().setAttributes(layout_params);

          getActivity().getPreferences(Context.MODE_PRIVATE).edit().putInt("reader_brightness", progress).apply();

        }

        @Override public void onStartTrackingTouch(
          final @Nullable SeekBar bar)
        {
          // Nothing
        }

        @Override public void onStopTrackingTouch(
          final @Nullable SeekBar bar)
        {
          // Nothing
        }
      });

    final Dialog d = this.getDialog();
    if (d != null) {
      d.setCanceledOnTouchOutside(true);
    }

    return layout;
  }

  @Override public void onResume()
  {
    super.onResume();

    final SimplifiedReaderAppServicesType rs =
      Simplified.getReaderAppServices();

    final Window window = this.getDialog().getWindow();
    window.setLayout(
      (int) rs.screenDPToPixels(300),
      android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
    window.setGravity(Gravity.CENTER);
  }
}
