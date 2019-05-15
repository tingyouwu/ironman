package com.wty.app.bluetoothcar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.wty.app.bluetoothcar.R;
import com.wty.app.bluetoothcar.data.BloodSugarDALEx;

import java.util.List;

public class LineChartMarkView extends MarkerView {

	private TextView tvDate;
	private TextView tvValue;
	private List<BloodSugarDALEx> data;

	public LineChartMarkView(Context context, List<BloodSugarDALEx> data) {
		super(context, R.layout.layout_markview);
		this.data = data;

		tvDate = findViewById(R.id.tv_date);
		tvValue = findViewById(R.id.tv_value);
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void refreshContent(Entry e, Highlight highlight) {
		//展示自定义X轴值 后的X轴内容
		BloodSugarDALEx item = data.get(data.size() - 1 - (int)e.getX());
		tvDate.setText("日期：" + item.getDate());
		tvValue.setText("血糖值：" + e.getY() + "mmol/L");
		super.refreshContent(e, highlight);
	}

	@Override
	public MPPointF getOffset() {
		return new MPPointF(-(getWidth() / 2), -getHeight());
	}
}