package sv.edu.catolica.drinkupgrupo06;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;
import java.util.Date;

public class DayDecorator implements DayViewDecorator{
    private final Date date;
    private final int color;

    public DayDecorator(Date date, int color) {
        this.date = date;
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return day.getDate().equals(calendar.getTime());
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(new ColorDrawable(color));
        view.addSpan(new ForegroundColorSpan(Color.BLACK)); // Cambia el color del texto si es necesario
    }
}
