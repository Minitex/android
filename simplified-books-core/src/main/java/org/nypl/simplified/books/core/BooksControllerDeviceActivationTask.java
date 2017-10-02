package org.nypl.simplified.books.core;

import com.io7m.jfunctional.None;
import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jfunctional.OptionVisitorType;
import com.io7m.jfunctional.Some;
import com.io7m.jfunctional.Unit;

import org.nypl.drm.core.AdobeAdeptActivationReceiverType;
import org.nypl.drm.core.AdobeAdeptConnectorType;
import org.nypl.drm.core.AdobeAdeptExecutorType;
import org.nypl.drm.core.AdobeAdeptProcedureType;
import org.nypl.drm.core.AdobeDeviceID;
import org.nypl.drm.core.AdobeUserID;
import org.nypl.drm.core.AdobeVendorID;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Runnable that JUST activates the device with Adobe (used on startup, and as part of logging in)
 */

public class BooksControllerDeviceActivationTask implements Runnable,
  AdobeAdeptActivationReceiverType {

  private final OptionType<AdobeAdeptExecutorType> adobe_drm;
  private final AccountCredentials credentials;
  private final AccountsDatabaseType accounts_database;
  private final DeviceActivationListenerType device_activation_listener;

  private static final Logger LOG;

  static {
    LOG = LogUtilities.getLog(BooksControllerDeviceActivationTask.class);
  }

  BooksControllerDeviceActivationTask(
    final OptionType<AdobeAdeptExecutorType> in_adobe_drm,
    final AccountCredentials in_credentials,
    final AccountsDatabaseType in_accounts_database,
    final BookDatabaseType book_database,
    final DeviceActivationListenerType in_device_activation_listener) {
    this.adobe_drm = in_adobe_drm;
    this.credentials = in_credentials;
    this.accounts_database = in_accounts_database;
    this.device_activation_listener = in_device_activation_listener;
  }

  @Override
  public void run() {
    if (this.adobe_drm.isSome()) {
      final Some<AdobeAdeptExecutorType> some =
        (Some<AdobeAdeptExecutorType>) this.adobe_drm;
      final AdobeAdeptExecutorType adobe_exec = some.get();

      final OptionType<AccountAdobeToken> adobe_token = this.credentials.getAdobeToken();
      final OptionType<AdobeVendorID> vendor_opt = this.credentials.getAdobeVendor();

      vendor_opt.accept(
        new OptionVisitorType<AdobeVendorID, Unit>() {
          @Override
          public Unit none(final None<AdobeVendorID> n) {
            BooksControllerDeviceActivationTask.this.onActivationError(
              "No Adobe vendor ID provided");
            return Unit.unit();
          }

          @Override
          public Unit some(final Some<AdobeVendorID> s) {
            adobe_exec.execute(
              new AdobeAdeptProcedureType() {
                @Override
                public void executeWith(final AdobeAdeptConnectorType c) {
//                  c.discardDeviceActivations();

                  final String token = ((Some<AccountAdobeToken>) adobe_token).get().toString().replace("\n", "");

                  final String username = token.substring(0, token.lastIndexOf("|"));
                  final String password = token.substring(token.lastIndexOf("|") + 1);

                  c.activateDevice(
                    BooksControllerDeviceActivationTask.this,
                    ((Some<AdobeVendorID>) vendor_opt).get(),
                    username,
                    password);

                  new DeviceManagerPostTask(BooksControllerDeviceActivationTask.this.credentials).run();

                }
              });
            return Unit.unit();
          }
        });
    }
  }

  @Override
  public void onActivation(
    final int index,
    final AdobeVendorID authority,
    final AdobeDeviceID device_id,
    final String user_name,
    final AdobeUserID user_id,
    final String expires) {
    BooksControllerDeviceActivationTask.LOG.debug(
      "Activation [{}]: authority: {}", Integer.valueOf(index), authority);
    BooksControllerDeviceActivationTask.LOG.debug(
      "Activation [{}]: device_id: {}", Integer.valueOf(index), device_id);
    BooksControllerDeviceActivationTask.LOG.debug(
      "Activation [{}]: user_name: {}", Integer.valueOf(index), user_name);
    BooksControllerDeviceActivationTask.LOG.debug(
      "Activation [{}]: user_id: {}", Integer.valueOf(index), user_id);
    BooksControllerDeviceActivationTask.LOG.debug(
      "Activation [{}]: expires: {}", Integer.valueOf(index), expires);

    BooksControllerDeviceActivationTask.this.credentials.setAdobeUserID(Option.some(user_id));
    BooksControllerDeviceActivationTask.this.credentials.setAdobeDeviceID(Option.some(device_id));

    this.device_activation_listener.onDeviceActivationSuccess();

    try {
      this.accounts_database.accountSetCredentials(this.credentials);
    } catch (final IOException e) {
      BooksControllerDeviceActivationTask.LOG.error("could not save credentials: ", e);
    }

  }

  @Override
  public void onActivationsCount(final int count) {
    /**
     * Device activation succeeded.
     */
    BooksControllerDeviceActivationTask.LOG.debug(
      "Activation  count: {}", count);

  }

  @Override
  public void onActivationError(final String error) {
    BooksControllerDeviceActivationTask.LOG.debug("Failed to activate device: {}", error);
    this.device_activation_listener.onDeviceActivationFailure(error);
//    try {
//      this.accounts_database.accountRemoveCredentials();
//    } catch (IOException exception) {
//      BooksControllerDeviceActivationTask.LOG.debug("Failed to clear account credentials");
//    }
  }
}
