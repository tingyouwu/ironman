package com.wty.app.bluetoothcar;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.kw.app.commonlib.utils.AppLogUtil;
import com.kw.app.commonlib.utils.PreferenceUtil;
import com.kw.app.ormlib.OrmModuleManager;
import com.wty.app.bluetoothcar.data.BloodSugarDALEx;
import com.wty.app.bluetoothcar.mvp.contract.IMainContract;
import com.wty.app.bluetoothcar.mvp.presenter.MainPresenter;
import com.wty.app.bluetoothcar.view.LineChartMarkView;
import com.wty.lib.widget.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 血糖数据表页面
 **/
public class NewMainActivity extends BaseActivity<MainPresenter>  implements IMainContract.IMainView {

    @Bind(R.id.linechart)
    LineChart mlineChart;
    @Bind(R.id.tv_desc)
    TextView mTvDesc;

    private XAxis xAxis;                //X轴
    private YAxis leftYAxis;            //左侧Y轴
    private YAxis rightYaxis;           //右侧Y轴
    private Legend legend;              //图例
    private LimitLine limitLineLow,limitLineHigh;        //限制线
    private List<BloodSugarDALEx> data = new ArrayList<>();

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, NewMainActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public MainPresenter getPresenter() {
        return new MainPresenter();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_main_new;
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        getDefaultNavigation().setTitle("血糖数据表");
        getDefaultNavigation().getLeftButton().setButton("采集", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserUploadDataActivity.startActivity(NewMainActivity.this);
            }
        });
        getDefaultNavigation().getRightButton().setButton("切换账号", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.startActivity(NewMainActivity.this,true);
                finish();
            }
        });
        OrmModuleManager.getInstance().setCurrentDBName(PreferenceUtil.getInstance().getLastAccount());
        initChart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.refreshData();
    }

    /**
     * 初始化图表
     */
    private void initChart(){
        /***图表设置***/
        mlineChart.setBackgroundColor(Color.WHITE);
        //是否展示网格线
        mlineChart.setDrawGridBackground(false);
        //是否显示边界
        mlineChart.setDrawBorders(false);
        //是否可以拖动
        mlineChart.setDragEnabled(false);
        //是否有触摸事件
        mlineChart.setTouchEnabled(true);
        //设置XY轴动画效果
        mlineChart.animateY(2500);
        mlineChart.animateX(1500);
        mlineChart.setNoDataText("暂无数据");

        Description description = new Description();
        description.setText("单位(mmol/L)");
        mlineChart.setDescription(description);

        /***XY轴的设置***/
        xAxis = mlineChart.getXAxis();
        leftYAxis = mlineChart.getAxisLeft();
        rightYaxis = mlineChart.getAxisRight();
        xAxis.setDrawGridLines(false);
        leftYAxis.setDrawGridLines(true);
        rightYaxis.setEnabled(false);
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);

        //保证Y轴从0开始，不然会上移一点
        leftYAxis.setAxisMinimum(0f);
        rightYaxis.setAxisMinimum(0f);
        rightYaxis.setEnabled(false);

        /***折线图例 标签 设置***/
        legend = mlineChart.getLegend();
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);
        /** 设置警戒值*/

        limitLineLow = new LimitLine((float) 3.9,"血糖正常最低值");
        limitLineLow.setLineWidth(1f);
        limitLineLow.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLineLow.setLineColor(Color.RED);
        limitLineLow.setTextColor(getResources().getColor(R.color.colorRedPrimary));
        limitLineLow.setTextSize(10f);
        leftYAxis.addLimitLine(limitLineLow);

        limitLineHigh = new LimitLine((float)6.1,"血糖正常最高值");
        limitLineHigh.setLineWidth(1f);
        limitLineHigh.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        limitLineHigh.setLineColor(Color.RED);
        limitLineHigh.setTextColor(getResources().getColor(R.color.colorRedPrimary));
        limitLineHigh.setTextSize(10f);
        leftYAxis.addLimitLine(limitLineHigh);
    }


    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     * @param mode
     */
    private void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        //设置折线图填充
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }


    /**
     * 设置 可以显示X Y 轴自定义值的 MarkerView
     */
    public void setMarkerView(List<BloodSugarDALEx> data) {
        LineChartMarkView mv = new LineChartMarkView(this, data);
        mv.setChartView(mlineChart);
        mlineChart.setMarker(mv);
        mlineChart.invalidate();
    }

    @Override
    public void refreshChart(List<BloodSugarDALEx> list) {
        AppLogUtil.d("获取数据成功");
        data.clear();
        data.addAll(list);
        if(data.size() == 0){
            mTvDesc.setVisibility(View.GONE);
            return;
        }

        ArrayList<Entry> yVals = new ArrayList<>();
        for(int index = 0;index<list.size();index++){
            BloodSugarDALEx item = list.get(list.size()-1-index);
            yVals.add(new Entry(index,item.getLevel()));
        }

        String desc;
        mTvDesc.setVisibility(View.VISIBLE);
        if(list.size()==1){
            //只有一条数据
            desc = String.format("数据采集时间\n%s",list.get(0).getDate());
        }else{
            desc = String.format("数据采集时间\n从 %s 至 %s",list.get(list.size()-1).getDate(),list.get(0).getDate());
        }
        mTvDesc.setText(desc);

        LineDataSet set = new LineDataSet(yVals,"您的血糖测量数据");
        initLineDataSet(set,getResources().getColor(R.color.app_main_yellow),LineDataSet.Mode.LINEAR);
        LineData data = new LineData(set);
        xAxis.setLabelCount(list.size());
        mlineChart.setData(data);
        setMarkerView(list);
    }
}
