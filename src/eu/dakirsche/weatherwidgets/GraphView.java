package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;

public class GraphView extends View
{
    private static final String TAG = "GraphView";
	/*Klassenvariablen*/
	private WeatherDataCollection datasets;
    private ResolutionBasedConfiguration res;

    private int temperatureSpan = 0;
    private int minTemperature = 0;
   // private int maxTemperature = 0;
    private int widthPixels = 0;
    private int heightPixels = 0;
    private int pixelsForOneDegree = 50;
    private int pixelsForOneTimeSeq = 50;

    private Context context = null;

    private String graphTitle = "";
	
	/*Klassenkonstanten*/
		private static final int DRAW_COLOR_TEMPERATURE_MAX = Color.parseColor("#AA0000");
		private static final int DRAW_COLOR_TEMPERATURE_MIN = Color.parseColor("#0000AA");
		//private static final int DRAW_COLOR_WIND = Color.parseColor("#343434");
		private static final int DRAW_COLOR_COORDINATES = Color.parseColor("#000000");
		private static final int DRAW_COLOR_GRID = Color.parseColor("#AAAAAA");
        private static final int DRAW_COLOR_LIGHTGRAY = Color.parseColor("#DDDDDD");
        private static final int DRAW_COLOR_DAYBREAK = Color.parseColor("#00AA00");
		
	/*Konstruktoren*/
		public GraphView(Context context, AttributeSet attrs, int defStyle, WeatherDataCollection data){
			super(context, attrs, defStyle);
			this.useDataCollection(data);
            this.context = context;
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "GraphView erzeugt");
		}
		public GraphView(Context context, AttributeSet attrs, int defStyle){
			super(context, attrs, defStyle);
            this.context = context;
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "GraphView erzeugt");
		}
		public GraphView(Context context, AttributeSet attrs){
			super(context, attrs);
            this.context = context;
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "GraphView erzeugt");
		}
		public GraphView(Context context){
			super(context);
            this.context = context;
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "GraphView erzeugt");
		}
	/*Public Deklarationen*/
	public void onDraw(Canvas canvas){
		canvas = this.drawCoordinateSystem(canvas);
	}
	//Diese Methode empfängt die WeatherDataCollection, die als Graphen dargestellt werden soll
	public void useDataCollection(WeatherDataCollection weatherData){
		this.datasets = weatherData;
        this.res = new ResolutionBasedConfiguration(this.context);

        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Datensätze empfangen: " + weatherData.getSize() + " Wetterdaten");

        this.temperatureSpan = weatherData.getTemperatureSpan() + 4;
        // Jeweils inc 2
        this.minTemperature = weatherData.getMinTemp() - 2;
       // this.maxTemperature = weatherData.getMaxTemp() + 2;

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //this.widthPixels = getMeasuredWidth();
       // this.heightPixels = getMeasuredHeight();
        this.heightPixels = metrics.heightPixels;
        this.heightPixels -= this.res.exclude_height;
        this.widthPixels = metrics.widthPixels;

        this.pixelsForOneDegree = Integer.parseInt(""+Math.round((this.heightPixels - (this.res.padding_top + this.res.padding_bottom)) / this.temperatureSpan));
        this.pixelsForOneTimeSeq = Integer.parseInt(""+Math.round((this.widthPixels - (this.res.padding_top + this.res.padding_bottom)) / (this.datasets.getSize() - 1)));
	}
	
	/*Private Deklarationen*/
	private Canvas drawCoordinateSystem(Canvas canvas){

        Paint linePaint = new Paint();
        linePaint.setColor(DRAW_COLOR_COORDINATES);

        Paint minLinePaint = new Paint();
        minLinePaint.setColor(DRAW_COLOR_TEMPERATURE_MIN);

        Paint maxLinePaint = new Paint();
        maxLinePaint.setColor(DRAW_COLOR_TEMPERATURE_MAX);

        Paint minFillPaint = new Paint();
        minFillPaint.setColor(DRAW_COLOR_TEMPERATURE_MIN);

        Paint maxFillPaint = new Paint();
        maxFillPaint.setColor(DRAW_COLOR_TEMPERATURE_MAX);

        Paint gridLinePaint = new Paint();
        gridLinePaint.setColor(DRAW_COLOR_GRID);

        Paint lightGridLinePaint = new Paint();
        lightGridLinePaint.setColor(DRAW_COLOR_LIGHTGRAY);

        Paint dayBreakLinePaint = new Paint();
        dayBreakLinePaint.setColor(DRAW_COLOR_DAYBREAK);

        /* Paints konfigurieren */

        maxLinePaint.setStyle(Paint.Style.STROKE);
        minLinePaint.setStyle(Paint.Style.STROKE);
        maxLinePaint.setStrokeWidth(this.res.draw_line_width);
        minLinePaint.setStrokeWidth(this.res.draw_line_width);

        /* Gefüllt 80% Alpha */
        maxFillPaint.setStyle(Paint.Style.FILL);
        minFillPaint.setStyle(Paint.Style.FILL);
        maxFillPaint.setAlpha(50);
        minFillPaint.setAlpha(50);

        int max = this.datasets.getSize();
        PointF[] maxPoints = new PointF[max];
        PointF[] minPoints = new PointF[max];
        Path maxPath = new Path();
        Path minPath = new Path();

		//DisplayMetrics metrics = this.funcs.getMetrics();
	//	int width = metrics.widthPixels;
	//	int height = metrics.heightPixels;
        int width = this.widthPixels;
        int height = this.heightPixels;


        //Punkte im Zentrum des KOORD Systems
        int maxPosX = this.res.padding_left - this.pixelsForOneTimeSeq;
        int minPosX = this.res.padding_left - this.pixelsForOneTimeSeq;
        int maxPosY = height - this.res.padding_bottom;
        int minPosY = height - this.res.padding_bottom;
        int nx1 = 0;
        int ny1 = 0;
        int nx2 = 0;
        int ny2 = 0;


        int posY = height - this.res.padding_bottom;
        int i = 0;
        int posX = this.res.padding_left - this.pixelsForOneTimeSeq;

            /* NULLPUNKTE */
        int zeroMaxX = maxPosX + this.pixelsForOneTimeSeq;
        int zeroMaxY = maxPosY;
        int zeroMinX = minPosX + this.pixelsForOneTimeSeq;
        int zeroMinY = minPosY;


        for (i = 0; i < max; i++){
            //Für jeden Datensatz Temperaturen einzeichnen

            /*Temperatur zeichnen*/
            WeatherData data = this.datasets.getItemAtPos(i);
            int t1 = data.getTemperaturMinInt() - this.minTemperature;
            int t2 = data.getTemperatureMaxInt() - this.minTemperature;

            //Neuen Positionspunkte berechnen
            ny1 =  (height - this.res.padding_bottom) - (this.pixelsForOneDegree * t2);
            nx1 = maxPosX + this.pixelsForOneTimeSeq;

            ny2 =  (height - this.res.padding_bottom) - (this.pixelsForOneDegree * t1);
            nx2 = minPosX + this.pixelsForOneTimeSeq;

            canvas.drawCircle(nx1, ny1, this.res.draw_point_radius, maxLinePaint);
            canvas.drawCircle(nx2, ny2, this.res.draw_point_radius, minLinePaint);

            maxPoints[i] = new PointF(nx1, ny1);
            minPoints[i] = new PointF(nx2, ny2);
               // canvas.drawLine(maxPosX, maxPosY, nx1, ny1, maxLinePaint);
               // canvas.drawLine(minPosX, minPosY, nx2, ny2, minLinePaint);
            minPosX = nx2;
            maxPosX = nx1;
            minPosY = ny2;
            maxPosY = ny1;
        }

        /* Pfad zusammensetzen */
        maxPath.moveTo(maxPoints[0].x, maxPoints[0].y);
        minPath.moveTo(minPoints[0].x, minPoints[0].y);
        for (i = 1; i < max; i++){
            maxPath.lineTo(maxPoints[i].x, maxPoints[i].y);
            minPath.lineTo(minPoints[i].x, minPoints[i].y);
        }

        /* Linien des Polygons zeichnen */
        canvas.drawPath(maxPath, maxLinePaint);
        canvas.drawPath(minPath, minLinePaint);

        /* Beschriftungen einfügen */
        maxLinePaint.setStrokeWidth(1);
        minLinePaint.setStrokeWidth(1);

        canvas.drawText(this.context.getString(R.string.temp_max), maxPosX + 5, maxPosY - 15, maxLinePaint);
        canvas.drawText(this.context.getString(R.string.temp_min), minPosX + 5, minPosY + 15, minLinePaint);


        /* Polygone schliessen */
        for (i = (max - 1); i >= 0; i--){
            maxPath.lineTo(minPoints[i].x, minPoints[i].y);
           // maxPath.lineTo(nx1, (height - this.res.padding_bottom));
        }

        maxPath.lineTo(this.res.padding_left, (height - this.res.padding_bottom));

        /* Min Path ist Border untere Achse*/
        minPath.lineTo(nx2, (height - this.res.padding_bottom));
        minPath.lineTo(this.res.padding_left, (height - this.res.padding_bottom));

        maxPath.lineTo(zeroMaxX, zeroMaxY);
        minPath.lineTo(zeroMinX, zeroMinY);
        /* Gefülltes Polygon */
        canvas.drawPath(maxPath, maxFillPaint);
        canvas.drawPath(minPath, minFillPaint);

        /* Basislinien zeichnen */
        canvas.drawLine(this.res.padding_left, this.res.padding_top - 10, this.res.padding_left, height - this.res.padding_bottom, linePaint);   //Y-Achse
        canvas.drawLine(this.res.padding_left, height - this.res.padding_bottom, width - this.res.padding_right + 20, height - this.res.padding_bottom, linePaint);    //X-Achse

        /* Pfeile zeichenn */
        canvas.drawLine(this.res.padding_left, this.res.padding_top - 10, this.res.padding_left-5, this.res.padding_top-5, linePaint);       //Y-Achse
        canvas.drawLine(this.res.padding_left, this.res.padding_top - 10, this.res.padding_left+5, this.res.padding_top-5, linePaint);       //Y-Achse

        canvas.drawLine(width - this.res.padding_right + 20, height - this.res.padding_bottom, width-this.res.padding_right+15, height - this.res.padding_bottom+5, linePaint);       //X-Achse
        canvas.drawLine(width - this.res.padding_right + 20, height - this.res.padding_bottom, width-this.res.padding_right+15, height - this.res.padding_bottom-5, linePaint);       //X-Achse
        i = 0;
        /* Koordinatensystem für Temperaturen zeichnen*/
        while (i < this.temperatureSpan){
            posY -= this.pixelsForOneDegree;
            canvas.drawLine(this.res.padding_left, posY, width - this.res.padding_right+10, posY, gridLinePaint);
            canvas.drawLine(this.res.padding_left-10, posY, this.res.padding_left+10, posY, linePaint);
            canvas.drawText(""+(this.minTemperature + i + 1), this.res.temperatures_padding, posY, linePaint);
            i++;
        }

        /* Koordinatensystem für Dati zeichnen*/
        i = 0;
        int n = 0;
        while (i < max){
            posX += this.pixelsForOneTimeSeq;
            if (i%4 == 1) {       //11Uhr
                n++;
                posY = (n % 2 == 1 ? this.res.date_padding_even : this.res.date_padding_odd);
                canvas.drawText(this.datasets.getItemAtPos(i).getDateStr(), posX - 10, height - this.res.padding_bottom + posY, linePaint);
                canvas.drawLine(posX, height - this.res.padding_bottom, posX, this.res.padding_top, gridLinePaint);
                canvas.drawLine(posX, height - this.res.padding_bottom + 5, posX, height - this.res.padding_bottom - 10, linePaint);
            }
            else {
                if (i % 4 == 3)             //23Uhr
                    canvas.drawLine(posX, height - this.res.padding_bottom, posX, this.res.padding_top, dayBreakLinePaint);
                else if (i > 0)    //5Uhr und 17Uhr
                    canvas.drawLine(posX, height - this.res.padding_bottom, posX, this.res.padding_top, lightGridLinePaint);

                canvas.drawLine(posX, height - this.res.padding_bottom+10, posX, height - this.res.padding_bottom+10, linePaint);
            }
            i++;
        }


        /* Achsen beschriften */
        canvas.drawText(this.context.getString(R.string.caption_degree), this.res.padding_left - 20, this.res.padding_top, linePaint);
        canvas.drawText(this.context.getString(R.string.caption_Time), width - this.res.padding_right, height - this.res.padding_bottom + 20, linePaint);

        linePaint.setTextSize(18);
        canvas.drawText(this.graphTitle,  this.res.padding_left, Math.round(this.res.padding_top/2), linePaint);


		return canvas;
	}
    public void setGraphTitle(String title){
             this.graphTitle = title;
    }
}
