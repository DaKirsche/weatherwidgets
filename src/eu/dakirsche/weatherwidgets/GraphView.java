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
	private FunctionCollection funcs;

    private int temperatureSpan = 0;
    private int minTemperature = 0;
    private int maxTemperature = 0;
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
			this.funcs = new FunctionCollection(context);
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "GraphView erzeugt");
		}
		public GraphView(Context context, AttributeSet attrs, int defStyle){
			super(context, attrs, defStyle);
            this.context = context;
			this.funcs = new FunctionCollection(context);
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "GraphView erzeugt");
		}
		public GraphView(Context context, AttributeSet attrs){
			super(context, attrs);
            this.context = context;
			this.funcs = new FunctionCollection(context);
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "GraphView erzeugt");
		}
		public GraphView(Context context){
			super(context);
            this.context = context;
			this.funcs = new FunctionCollection(context);
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

        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Datensätze empfangen: " + weatherData.getSize() + " Wetterdaten");

        this.temperatureSpan = weatherData.getTemperatureSpan() + 4;
        // Jeweils inc 2
        this.minTemperature = weatherData.getMinTemp() - 2;
        this.maxTemperature = weatherData.getMaxTemp() + 2;

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //this.widthPixels = getMeasuredWidth();
       // this.heightPixels = getMeasuredHeight();
        this.heightPixels = metrics.heightPixels;
        this.heightPixels -= 120;
        this.widthPixels = metrics.widthPixels;

        this.pixelsForOneDegree = Integer.parseInt(""+Math.round((this.heightPixels - 220) / this.temperatureSpan));
        this.pixelsForOneTimeSeq = Integer.parseInt(""+Math.round((this.widthPixels - 220) / (this.datasets.getSize() - 1)));
	}
	
	/*Private Deklarationen*/
	private Canvas drawCoordinateSystem(Canvas canvas){

        Paint linePaint = new Paint();
        linePaint.setColor(DRAW_COLOR_COORDINATES);

        Paint minLinePaint = new Paint();
        minLinePaint.setColor(DRAW_COLOR_TEMPERATURE_MIN);

        Paint maxLinePaint = new Paint();
        maxLinePaint.setColor(DRAW_COLOR_TEMPERATURE_MAX);

        Paint gridLinePaint = new Paint();
        gridLinePaint.setColor(DRAW_COLOR_GRID);

        Paint lightGridLinePaint = new Paint();
        lightGridLinePaint.setColor(DRAW_COLOR_LIGHTGRAY);

        Paint dayBreakLinePaint = new Paint();
        dayBreakLinePaint.setColor(DRAW_COLOR_DAYBREAK);

		//DisplayMetrics metrics = this.funcs.getMetrics();
	//	int width = metrics.widthPixels;
	//	int height = metrics.heightPixels;
        int width = this.widthPixels;
        int height = this.heightPixels;

        /* Basislinien zeichnen */
		canvas.drawLine(100, height - 100, width - 100, height - 100, linePaint);
		canvas.drawLine(100, 100, 100, height - 100, linePaint);

        /* Pfeile zeichenn */
        canvas.drawLine(100, 100, 95, 105, linePaint);
        canvas.drawLine(100, 100, 105, 105, linePaint);
        canvas.drawLine(width - 100, height - 100, width-105, height - 105, linePaint);
        canvas.drawLine(width - 100, height - 100, width-105, height - 95, linePaint);

        /* Koordinatensystem für Temperaturen zeichnen*/
        int posY = height - 100;
        int i = 0;
        while (i < this.temperatureSpan){
            posY -= this.pixelsForOneDegree;
            canvas.drawLine(100, posY, width - 100, posY, gridLinePaint);
            canvas.drawLine(90, posY, 110, posY, linePaint);
            canvas.drawText(""+(this.minTemperature + i), 60, posY, linePaint);
            i++;
        }

        /* Koordinatensystem für Dati zeichnen*/
        int posX = 110 - this.pixelsForOneTimeSeq;
        i = 0;
        int n = 0;
        int max = this.datasets.getSize();
        while (i < max){
            posX += this.pixelsForOneTimeSeq;
            if (i%4 == 1) {
                n++;
                posY = (n % 2 == 1 ? 40 : 60);
                canvas.drawText(this.datasets.getItemAtPos(i).getDateStr(), posX - 10, height - posY, linePaint);
                canvas.drawLine(posX, height - 100, posX, 100, gridLinePaint);
                canvas.drawLine(posX, height - 80, posX, height - 110, linePaint);
            }
            else {
                if (i % 4 == 3)
                    canvas.drawLine(posX, height - 60, posX, 60, dayBreakLinePaint);
                else
                    canvas.drawLine(posX, height - 100, posX, 100, lightGridLinePaint);

                canvas.drawLine(posX, height - 90, posX, height - 110, linePaint);
            }
            i++;
        }

        //Punkte im Zentrum des KOORD Systems
        int maxPosX = 110 - this.pixelsForOneTimeSeq;
        int minPosX = 110 - this.pixelsForOneTimeSeq;
        int maxPosY = height - 100;
        int minPosY = height - 100;
        int nx1 = 0;
        int ny1 = 0;
        int nx2 = 0;
        int ny2 = 0;

        for (i = 0; i < max; i++){
            //Für jeden Datensatz Temperaturen einzeichnen

            /*Temperatur zeichnen*/
            WeatherData data = this.datasets.getItemAtPos(i);
            int t1 = data.getTemperaturMinInt() - this.minTemperature;
            int t2 = data.getTemperatureMaxInt() - this.minTemperature;

            //Neuen Positionspunkte berechnen
            ny1 =  (height - 100) - (this.pixelsForOneDegree * t2);
            nx1 = maxPosX + this.pixelsForOneTimeSeq;

            ny2 =  (height - 100) - (this.pixelsForOneDegree * t1);
            nx2 = minPosX + this.pixelsForOneTimeSeq;

            canvas.drawCircle(nx1, ny1, 2, maxLinePaint);
            canvas.drawCircle(nx2, ny2, 2, minLinePaint);

            if (i > 0){
                canvas.drawLine(maxPosX, maxPosY, nx1, ny1, maxLinePaint);
                canvas.drawLine(minPosX, minPosY, nx2, ny2, minLinePaint);
            }
            minPosX = nx2;
            maxPosX = nx1;
            minPosY = ny2;
            maxPosY = ny1;
        }

        /* Beschriftungen einfügen */

        canvas.drawText(this.context.getString(R.string.temp_max), maxPosX + 10, maxPosY - 15, maxLinePaint);
        canvas.drawText(this.context.getString(R.string.temp_min), minPosX + 10, minPosY + 15, minLinePaint);

        /* Achsen beschriften */
        canvas.drawText(this.context.getString(R.string.caption_degree), 70, 100, linePaint);
        canvas.drawText(this.context.getString(R.string.caption_Time), width - 100, height - 80, linePaint);

        linePaint.setTextSize(18);
        canvas.drawText(this.graphTitle,  100, 50, linePaint);


		return canvas;
	}
    public void setGraphTitle(String title){
             this.graphTitle = title;
    }
}
