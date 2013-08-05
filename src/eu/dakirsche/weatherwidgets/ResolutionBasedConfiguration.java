package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Diese Klasse stellt für die GaphView Komponente je nach DPI Auflösung verschiedene Werte für die Borders, etc bereit,
 * da eine auflösungsbasierte Bereitstellung von Integer- und Float-Variablen nicht oder nur unzureichend möglich ist
 */
public class ResolutionBasedConfiguration {
    /*Wertproperties*/
    public static int padding_bottom;
    public static int padding_top;
    public static int padding_left;
    public static int padding_right;
    public static int exclude_height;
    public static int temperatures_padding;
    public static int date_padding_even;
    public static int date_padding_odd;
    public static int draw_line_width;
    public static int draw_point_radius;


    public ResolutionBasedConfiguration(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int dpi = metrics.densityDpi;
        if (dpi == DisplayMetrics.DENSITY_XHIGH || dpi == DisplayMetrics.DENSITY_XXHIGH)
            this.initializeForXHDPI();
        else if (dpi == DisplayMetrics.DENSITY_HIGH)
            this.initializeForHDPI();
        else if (dpi == DisplayMetrics.DENSITY_MEDIUM)
            this.initializeForMDPI();
        else if (dpi == DisplayMetrics.DENSITY_LOW)
            this.initializeForLDPI();
        else this.initializeForXHDPI();

    }

    private void initializeForXHDPI(){
        this.padding_bottom = 100;
        this.padding_top = 100;
        this.padding_left = 100;
        this.padding_right = 100;
        this.exclude_height = 120;
        this.temperatures_padding = 60;
        this.date_padding_even = 17;
        this.date_padding_odd = 30;
        this.draw_line_width = 2;
        this.draw_point_radius = 3;
    }
    private void initializeForHDPI(){
        this.padding_bottom = 60;
        this.padding_top = 50;
        this.padding_left = 50;
        this.padding_right = 50;
        this.exclude_height = 100;
        this.temperatures_padding = 10;
        this.date_padding_even = 17;
        this.date_padding_odd = 30;
        this.draw_line_width = 1;
        this.draw_point_radius = 3;
    }
    private void initializeForMDPI(){
        this.padding_bottom = 50;
        this.padding_top = 50;
        this.padding_left = 50;
        this.padding_right = 50;
        this.exclude_height = 80;
        this.temperatures_padding = 20;
        this.date_padding_even = 17;
        this.date_padding_odd = 30;
        this.draw_line_width = 1;
        this.draw_point_radius = 2;
    }
    private void initializeForLDPI(){
        this.padding_bottom = 50;
        this.padding_top = 50;
        this.padding_left = 50;
        this.padding_right = 50;
        this.exclude_height = 90;
        this.temperatures_padding = 20;
        this.date_padding_even = 17;
        this.date_padding_odd = 30;
        this.draw_line_width = 1;
        this.draw_point_radius = 2;
    }
}
