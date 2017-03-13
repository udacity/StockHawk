package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.core.ui.fragment.DaggerCleanFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

import static com.udacity.stockhawk.ui.StockActivity.STOCK_EXTRA;
import static com.udacity.stockhawk.ui.StockActivity.STOCK_HISTORY_EXTRA;

public class StockDetailFragment extends DaggerCleanFragment<StockDetailPresenter, StockDetailView, StockComponent> implements StockDetailView {
   String stockSymbol;
   String stockHistory;
   LineChartView chart;
   @BindView(R.id.chart_view) FrameLayout chartFrame;

   @Inject
   public StockDetailFragment() {
   }

   public static StockDetailFragment newInstance(String symbol, String history) {
      StockDetailFragment stockDetailFragment = new StockDetailFragment();

      Bundle bundle = new Bundle();
      bundle.putString(STOCK_EXTRA, symbol);
      bundle.putString(STOCK_HISTORY_EXTRA, history);

      stockDetailFragment.setArguments(bundle);
      return stockDetailFragment;
   }

   @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      ButterKnife.bind(this, getActivity());
      final Bundle bundle = getArguments();
      stockSymbol = bundle.getString(STOCK_EXTRA);
      stockHistory = bundle.getString(STOCK_HISTORY_EXTRA);
      chart = new LineChartView(getActivity());
      chart.setInteractive(true);
      chart.setZoomEnabled(false);
      chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);


//      barChart.setOnChartGestureListener(this);
      setData(stockHistory);
      chartFrame.addView(chart);
   }

   private void setData(String stockHistory) {
      List<AxisValue> axisValuesX = new ArrayList<>();
      List<PointValue> pointValues = new ArrayList<>();
      List<Float> bidsOverTime = new ArrayList<>();
      List<String> dates = new ArrayList<>();
      Calendar cal = Calendar.getInstance();

      String[] values = stockHistory.split("\n");
      for (String bidPrice : values) {
         String[] splitValue = bidPrice.split(",");
         Long dateInMillis = Long.valueOf(splitValue[0]);
         cal.setTime(new Date(dateInMillis));
         String date = String.format("%d/%d/%d",
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.YEAR));
         dates.add(date);
         bidsOverTime.add(Float.valueOf(splitValue[1]));
      }


      for (int i = 0; i < bidsOverTime.size(); i++) {
         pointValues.add(new PointValue(i, bidsOverTime.get(i)).setLabel(dates.get(i)));
         // Set labels in less places to avoid overlapping
         if (i % (bidsOverTime.size() / 3) == 0) {
            AxisValue axisValueX = new AxisValue(i);
            axisValueX.setLabel(dates.get(i));
            axisValuesX.add(axisValueX);
         }
      }

      Line line = new Line(pointValues);
      line.setShape(ValueShape.CIRCLE);
      line.setCubic(true);
      LineChartData data = new LineChartData(Collections.singletonList(line));


      Axis axisX = new Axis(axisValuesX).setMaxLabelChars(1).setHasLines(true);
      Axis axisY = new Axis().setHasLines(true);
      axisX.setName("Datetime");
      axisY.setName("Bid price");

      data.setAxisXBottom(axisX);
      data.setAxisYLeft(axisY);

      data.setBaseValue(Float.NEGATIVE_INFINITY);
      chart.setLineChartData(data);
   }

   @Nullable @Override
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      return inflater.inflate(R.layout.stock_details, container, false);
   }

   @Override public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      if (stockHistory != null)
         outState.putString(STOCK_HISTORY_EXTRA, stockHistory);
      if (stockSymbol != null)
         outState.putString(STOCK_EXTRA, stockSymbol);
   }

   @Override protected StockComponent buildComponent() {
      return DaggerStockComponent.builder().
         appComponent(getApplicationComponent()).
         stockDetailModule(new StockComponent.StockDetailModule()).
         build();
   }
}

interface StockDetailView {

}
