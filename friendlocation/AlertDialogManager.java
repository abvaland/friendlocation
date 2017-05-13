package com.example.ajay.friendlocation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
public class AlertDialogManager {


    private static ProgressDialog progressDialog;

    /***********************************************************************************************
     * Function to display simple Alert Dialog
     *
     * @param context - application context
     **********************************************************************************************/
    public static void noInternetDialog(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert Message");

        // Setting Dialog Message
        alertDialog.setMessage(context.getResources().getString(R.string.wrn_no_internet));

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }//end of showAlertDialog

    /***********************************************************************************************
     * Function to display simple Alert Dialog
     *
     * @param context - application context
     * @param message - alert message
     **********************************************************************************************/
    public static void showAlertDialog(Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert Message");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }//end of showAlertDialog

    public static void showWaitingDialog(Context context)
    {
        progressDialog= ProgressDialog.show(
                context,null,"Please Wait...");
        //waitingDialogProgress.show();
    }

    //release progress dialog
    public static void releaseDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }//end of releaseDialog()
    /**********************************************************************************************************
     * Function to display simple Alert Dialog with click event
     *
     * @param context
     * @param message
     * @param clickListener
     */
    public static void showAlertDialog(Context context, String message, final ClickListener clickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert Message");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                clickListener.okClick();
            }
        });

        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }//end of showAlertDialog

    /**********************************************************************************************************
     * Function to display simple Alert Dialog with click event and yes no button
     *
     * @param context
     * @param message
     * @param isOk
     * @param isNo
     * @param isCancel
     * @param clickListener
     ***********************************************************************************************************/

    public static void showAlertDialogOkNo(Context context, String message, boolean isOk, boolean isNo, boolean isCancel, final ClickListener clickListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("Quit");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        if (isOk) {
            alertDialog.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    clickListener.okClick();
                }
            });
        }

        if (isNo) {
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    clickListener.noClick();
                }
            });
        }
        if (isCancel) {
            alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    clickListener.cancelClick();
                }
            });
        }

        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }//end of showAlertDialog

    public static abstract class ClickListener {
        public void okClick() {
        }

        public void noClick() {
        }

        public void cancelClick() {

        }
    }
}