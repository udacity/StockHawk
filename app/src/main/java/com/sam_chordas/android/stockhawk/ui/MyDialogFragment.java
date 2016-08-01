package com.sam_chordas.android.stockhawk.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.service.StockIntentService;

/**
 * Created by Abhishek on 01-08-2016.
 */
public class MyDialogFragment extends DialogFragment {
    private final String LOG_TAG = getClass().getSimpleName();
    private String mInputSymbol;
    private Intent mServiceIntent;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.symbol_search)
                .content(R.string.content_test)
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
                .positiveText(R.string.positive_text)
                .negativeText(R.string.negative_text)
                .alwaysCallInputCallback()
                .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                        if (input.toString().contains(" ")) {
                            Log.v(LOG_TAG, "This part executed");
                            dialog.setContent("WhiteSpace not allowed");
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        } else {
                            dialog.setContent(R.string.content_test);
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                            mInputSymbol = input.toString();

                        }
                    }
                })
                .inputRangeRes(1, 20, R.color.material_red_700)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Cursor c = getActivity().getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                                new String[]{QuoteColumns.SYMBOL}, QuoteColumns.SYMBOL + "= ?",
                                new String[]{mInputSymbol}, null);
                        if ((c != null ? c.getCount() : 0) != 0) {
                            Toast toast =
                                    Toast.makeText(getActivity(), "This stock is already saved!",
                                            Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
                            toast.show();
                            return;
                        } else {
                            // Add the stock to DB
                            mServiceIntent = new Intent(getActivity(), StockIntentService.class);
                            mServiceIntent.putExtra("tag", "add");
                            mServiceIntent.putExtra("symbol", mInputSymbol);
                            getActivity().startService(mServiceIntent);
                        }
                        if (c != null) {
                            c.close();
                        }
                    }
                })
                .build();

        return dialog;
    }
}
