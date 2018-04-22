package com.project.biker.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.biker.R;
import com.project.biker.activity.MainActivity;
import com.project.biker.model.INTFAlertOk;
import com.project.biker.model.INTFConfirmYesNo;
import com.project.biker.tools.SharePref;

/**
 * Created by Rahul Padaliya on 7/8/2017.
 */
public class DialogUtility {

    private static final String TAG = "DialogUtility";
    android.app.AlertDialog dialog = null;

    public static void locationDialog(Context context) {

        final SharePref sharePref = new SharePref(context);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_on_off);
        dialog.setCancelable(false);
        ImageView iv_dialog_close = (ImageView) dialog.findViewById(R.id.iv_dialog_close);
        CheckBox chkDialogOnOff = (CheckBox) dialog.findViewById(R.id.chkDialogOnOff);
        iv_dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        chkDialogOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                   logDebug("CHECKED"+"TRUE");
                    sharePref.setPopupCheck(true);
                } else {
                    logDebug("CHECKED"+"FALSE");
                }
            }
        });
        // Set dialog title
        dialog.show();
    }

    public static ProgressDialog showProgress(final Context context) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage("Please wait...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

            }
        });

        return progressDialog;
    }

    public static void AlertDialogUtility(Context context, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                context.getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    public static void dialogWithPositiveButton(final String msg, Context context) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(false);
        String positiveText = context.getString(R.string.OK);/*getString(android.R.string.yes);*/
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        if (dialog != null)
                            dialog.cancel();
                    }
                });

        android.app.AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public static void dialogQrBarCode(Context context, Bitmap bitmapObject) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_qr_bar_code);
        dialog.setCancelable(false);
        TextView tv_dialog_qr_bar_close = (TextView) dialog.findViewById(R.id.tv_dialog_qr_bar_close);
        ImageView iv_dialog_qr_bar_code = (ImageView) dialog.findViewById(R.id.iv_dialog_qr_bar_code);

        /* if(type.equalsIgnoreCase("barCode")) {
            BarCodeQRCode.generateBarCode(code,iv_dialog_qr_bar_code);
        }else if (type.equalsIgnoreCase("qrCode")){
            BarCodeQRCode.generateQRCode(code,iv_dialog_qr_bar_code);
        }*/

        iv_dialog_qr_bar_code.setImageBitmap(bitmapObject);
        tv_dialog_qr_bar_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        dialog.show();

    }

    public static void invalidCredentialsAlert(final Context context, final String msg) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(false);
        String positiveText = context.getString(R.string.OK);/*getString(android.R.string.yes);*/
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        if (dialog != null) {
                            ((MainActivity) context).logout();
                        }
                        //dialog.cancel();
                    }
                });

        android.app.AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    /**
     * AlertDialog which is override ok click
     * using interface
     * @param context
     * @param message
     * @param intfAlertOk
     * @return
     */
    public static Dialog alertOk(Context context, String message, final INTFAlertOk intfAlertOk) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(false);
        String positiveText = context.getString(R.string.OK);/*getString(android.R.string.yes);*/
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        intfAlertOk.alertOk();
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
        return dialog;
    }

    /**
     * Alert dialog which display yes or no option
     * every call can override YES click using interface
     * @param context
     * @param message
     * @param intfConfirmYesNo
     * @return
     */
    public static Dialog confirmYesNo(Context context, String message, final INTFConfirmYesNo intfConfirmYesNo) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(false);
        String positiveText = context.getString(R.string.YES);
        String negativeText = context.getString(R.string.NO);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intfConfirmYesNo.yesClick();
                    }
                });
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        android.app.AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();

        return dialog;
    }

    /**
     * Method for Log Printing
     *
     * @param msg
     */
    private static void logDebug(String msg) {
        Log.d(TAG, msg);
    }

}
