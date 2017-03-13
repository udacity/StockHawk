package com.udacity.stockhawk.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.udacity.stockhawk.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddStockDialog extends DialogFragment {

   @SuppressWarnings("WeakerAccess")
   @BindView(R.id.dialog_stock)
   EditText stock;
   private OnStockDialogClick onStockDialogClick;

   public AddStockDialog(OnStockDialogClick onStockDialogClick) {
      this.onStockDialogClick = onStockDialogClick;
   }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {

      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

      LayoutInflater inflater = LayoutInflater.from(getActivity());
      @SuppressLint("InflateParams") View custom = inflater.inflate(R.layout.add_stock_dialog, null);

      ButterKnife.bind(this, custom);

      stock.setOnEditorActionListener((v, actionId, event) -> {
         addStock();
         return true;
      });
      builder.setView(custom);

      builder.setMessage(getString(R.string.dialog_title));
      builder.setPositiveButton(getString(R.string.dialog_add),
         (dialog, id) -> addStock());
      builder.setNegativeButton(getString(R.string.dialog_cancel), null);

      Dialog dialog = builder.create();

      Window window = dialog.getWindow();
      if (window != null) {
         window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
      }

      return dialog;
   }

   private void addStock() {
      onStockDialogClick.onStockDialogClick(stock.getText().toString());
      dismissAllowingStateLoss();
   }

   interface OnStockDialogClick {
      void onStockDialogClick(String symbol);
   }
}