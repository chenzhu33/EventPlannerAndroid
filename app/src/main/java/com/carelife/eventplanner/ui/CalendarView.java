package com.carelife.eventplanner.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;

import com.carelife.eventplanner.dom.Plan;
import com.carelife.eventplanner.utils.TimeUtil;

import java.util.Calendar;
import java.util.Date;

public class CalendarView extends View implements View.OnTouchListener {
    private Date curDate;
    private Date today;
    private Date downDate;
    private int downIndex;
    private Calendar calendar;
    private Surface surface;
    private int[] date = new int[42];
    private int curStartIndex, curEndIndex;

    private Adapter adapter;

    private OnItemClickListener onItemClickListener;

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setAdapter(final Adapter adapter) {
        this.adapter = adapter;
        invalidate();
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                invalidate();
            }
        });
    }

    private void init() {
        curDate = today = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        surface = new Surface();
        surface.density = getResources().getDisplayMetrics().density;
        setBackgroundColor(surface.bgColor);
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        surface.width = getResources().getDisplayMetrics().widthPixels;
        surface.height = getResources().getDisplayMetrics().heightPixels * 2 / 5;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(surface.width,
                MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(surface.height,
                MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        if (changed) {
            surface.init();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(surface.boxPath, surface.borderPaint);
        float weekTextY = surface.monthHeight + surface.weekHeight * 3 / 4f;
        for (int i = 0; i < surface.weekText.length; i++) {
            float weekTextX = i
                    * surface.cellWidth
                    + (surface.cellWidth - surface.weekPaint
                    .measureText(surface.weekText[i])) / 2f;
            canvas.drawText(surface.weekText[i], weekTextX, weekTextY,
                    surface.weekPaint);
        }

        calculateDate();
        drawDownOrPlanBg(canvas);
        int todayIndex = -1;
        calendar.setTime(curDate);
        String curYearAndMonth = calendar.get(Calendar.YEAR) + ""
                + calendar.get(Calendar.MONTH);
        calendar.setTime(today);
        String todayYearAndMonth = calendar.get(Calendar.YEAR) + ""
                + calendar.get(Calendar.MONTH);
        if (curYearAndMonth.equals(todayYearAndMonth)) {
            int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);
            todayIndex = curStartIndex + todayNumber - 1;
        }
        for (int i = 0; i < 42; i++) {
            int color = surface.textColor;
            if (isLastMonth(i)) {
                color = surface.borderColor;
            } else if (isNextMonth(i)) {
                color = surface.borderColor;
            }
            if (todayIndex != -1 && i == todayIndex) {
                color = surface.todayNumberColor;
            }
            drawCellText(canvas, i, date[i] + "", color);
        }
        super.onDraw(canvas);
    }

    private void calculateDate() {
        calendar.setTime(curDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthStart = calendar.get(Calendar.DAY_OF_WEEK);
        if (monthStart == 1) {
            monthStart = 8;
        }
        monthStart -= 1;
        curStartIndex = monthStart;
        date[monthStart] = 1;
        // last month
        if (monthStart > 0) {
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
            for (int i = monthStart - 1; i >= 0; i--) {
                date[i] = dayInmonth;
                dayInmonth--;
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[0]);
        }
        // this month
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < monthDay; i++) {
            date[monthStart + i] = i + 1;
        }
        curEndIndex = monthStart + monthDay;
        for (int i = monthStart + monthDay; i < 42; i++) {
            date[i] = i - (monthStart + monthDay) + 1;
        }
        if (curEndIndex < 42) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, date[41]);
    }

    private void drawCellText(Canvas canvas, int index, String text, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.datePaint.setColor(color);
        float cellY = surface.monthHeight + surface.weekHeight + (y - 1)
                * surface.cellHeight + surface.cellHeight * 7 / 10f;
        float cellX = (surface.cellWidth * (x - 1))
                + (surface.cellWidth - surface.datePaint.measureText(text))
                / 2f;
        canvas.drawText(text, cellX, cellY, surface.datePaint);
    }

    private void drawPlanText(Canvas canvas, int index, String text, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.planPaint.setColor(color);
        float cellY = surface.monthHeight + surface.weekHeight + (y - 1)
                * surface.cellHeight + surface.cellHeight;
        float cellX = (surface.cellWidth * (x - 1))
                + (surface.cellWidth - surface.planPaint.measureText(text))
                / 2f;
        canvas.drawText(text, cellX, cellY, surface.planPaint);
    }

    private void drawCellBg(Canvas canvas, int index, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.cellBgPaint.setColor(color);
        float left = surface.cellWidth * (x - 1) + surface.borderWidth;
        float top = surface.monthHeight + surface.weekHeight + (y - 1)
                * surface.cellHeight + surface.borderWidth;
        canvas.drawRect(left, top, left + surface.cellWidth
                - surface.borderWidth, top + surface.cellHeight
                - surface.borderWidth, surface.cellBgPaint);
    }

    private void drawDownOrPlanBg(Canvas canvas) {
        // down and not up
        if (downDate != null) {
            drawCellBg(canvas, downIndex, surface.cellDownColor);
        }
        if(adapter == null) {
            return;
        }
        int size = adapter.getCount();
        for(int i=0;i<size;i++) {
            Object obj = adapter.getItem(i);
            if(obj instanceof Plan) {
                long startTime = ((Plan) obj).getStartDate();
                String[] dateString = TimeUtil.toSimpleDate(startTime).split("-");
                if(dateString[0].equals(getYear(curDate)) && dateString[1].equals(getMonth(curDate))) {
                    int index;
                    for(index = 0;index<date.length;index++) {
                        if(date[index] == 1) {
                            break;
                        }
                    }
                    for(;index<date.length;index++) {
                        if(date[index] == Integer.parseInt(dateString[2])) {
                            //Find success!
                            drawCellBg(canvas, index, surface.planColor);
                            drawPlanText(canvas, index, ((Plan) obj).title, surface.planTextColor);
                            break;
                        }
                    }
                }

            }
        }
    }

    private boolean isLastMonth(int i) {
        return i < curStartIndex;
    }

    private boolean isNextMonth(int i) {
        return i >= curEndIndex;
    }

    private int getXByIndex(int i) {
        return i % 7 + 1;
    }

    private int getYByIndex(int i) {
        return i / 7 + 1;
    }

    public String getYearAndmonth() {
        calendar.setTime(curDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        return year + "-" + month;
    }

    public String getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year+"";
    }

    public String getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH)+1;
        if(month < 10) {
            return "0"+month;
        }
        return month+"";
    }

    public String clickLeftMonth() {
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, -1);
        curDate = calendar.getTime();
        invalidate();
        return getYearAndmonth();
    }

    public String clickRightMonth() {
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        curDate = calendar.getTime();
        invalidate();
        return getYearAndmonth();
    }

    public void setCalendarData(Date date) {
        calendar.setTime(date);
        invalidate();
    }

    private void setSelectedDateByCoor(float x, float y) {
        if (y > surface.monthHeight + surface.weekHeight) {
            int m = (int) (Math.floor(x / surface.cellWidth) + 1);
            int n = (int) (Math.floor((y - (surface.monthHeight + surface.weekHeight))
                            / surface.cellHeight) + 1);
            downIndex = (n - 1) * 7 + m - 1;
            calendar.setTime(curDate);
            if (isLastMonth(downIndex)) {
                calendar.add(Calendar.MONTH, -1);
            } else if (isNextMonth(downIndex)) {
                calendar.add(Calendar.MONTH, 1);
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
            downDate = calendar.getTime();
        }
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSelectedDateByCoor(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (downDate != null) {
                    onItemClickListener.OnItemClick(downDate);
                    invalidate();
                }

                break;
        }
        return true;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void OnItemClick(Date downDate);
    }

    private class Surface {
        public float density;
        public int width;
        public int height;
        public float monthHeight;
        public float weekHeight;
        public float cellWidth;
        public float cellHeight;
        public float borderWidth;
        public int bgColor = Color.parseColor("#FFFFFF");
        private int textColor = Color.BLACK;
        private int btnColor = Color.parseColor("#666666");
        private int borderColor = Color.parseColor("#CCCCCC");
        public int todayNumberColor = Color.RED;
        public int cellDownColor = Color.parseColor("#CCFFFF");
        public int planColor = Color.GREEN;
        public int planTextColor = Color.GRAY;
        public Paint borderPaint;
        public Paint monthPaint;
        public Paint weekPaint;
        public Paint datePaint;
        public Paint planPaint;
        public Paint monthChangeBtnPaint;
        public Paint cellBgPaint;
        public Path boxPath;
        public String[] weekText = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        public void init() {
            float temp = height / 7f;
            monthHeight = 0;
            weekHeight = (float) ((temp + temp * 0.3f) * 0.7);
            cellHeight = (height - monthHeight - weekHeight) / 6f;
            cellWidth = width / 7f;
            borderPaint = new Paint();
            borderPaint.setColor(borderColor);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderWidth = (float) (0.5 * density);
            borderWidth = borderWidth < 1 ? 1 : borderWidth;
            borderPaint.setStrokeWidth(borderWidth);
            monthPaint = new Paint();
            monthPaint.setColor(textColor);
            monthPaint.setAntiAlias(true);
            float textSize = cellHeight * 0.4f;
            monthPaint.setTextSize(textSize);
            monthPaint.setTypeface(Typeface.DEFAULT_BOLD);
            weekPaint = new Paint();
            weekPaint.setColor(textColor);
            weekPaint.setAntiAlias(true);
            float weekTextSize = weekHeight * 0.6f;
            weekPaint.setTextSize(weekTextSize);
            weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
            datePaint = new Paint();
            datePaint.setColor(textColor);
            datePaint.setAntiAlias(true);
            float cellTextSize = cellHeight * 0.4f;
            datePaint.setTextSize(cellTextSize);
            datePaint.setTypeface(Typeface.DEFAULT_BOLD);
            planPaint = new Paint();
            planPaint.setColor(textColor);
            planPaint.setAntiAlias(true);
            cellTextSize = cellHeight * 0.3f;
            planPaint.setTextSize(cellTextSize);
            planPaint.setTypeface(Typeface.DEFAULT_BOLD);
            boxPath = new Path();
            boxPath.rLineTo(width, 0);
            boxPath.moveTo(0, monthHeight + weekHeight);
            boxPath.rLineTo(width, 0);
            for (int i = 1; i < 6; i++) {
                boxPath.moveTo(0, monthHeight + weekHeight + i * cellHeight);
                boxPath.rLineTo(width, 0);
                boxPath.moveTo(i * cellWidth, monthHeight);
                boxPath.rLineTo(0, height - monthHeight);
            }
            boxPath.moveTo(6 * cellWidth, monthHeight);
            boxPath.rLineTo(0, height - monthHeight);
            monthChangeBtnPaint = new Paint();
            monthChangeBtnPaint.setAntiAlias(true);
            monthChangeBtnPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            monthChangeBtnPaint.setColor(btnColor);
            cellBgPaint = new Paint();
            cellBgPaint.setAntiAlias(true);
            cellBgPaint.setStyle(Paint.Style.FILL);
        }
    }
}
