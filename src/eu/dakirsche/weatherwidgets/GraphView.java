package eu.dakirsche.weatherwidgets;

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
	
	/*Klassenkonstanten*/
		private static final int DRAW_COLOR_TEMPERATURE = Color.parseColor("#AA0000");
		private static final int DRAW_COLOR_RAIN = Color.parseColor("#0000AA");
		//private static final int DRAW_COLOR_WIND = Color.parseColor("#343434");
		private static final int DRAW_COLOR_COORDINATES = Color.parseColor("#000000");
		private static final int DRAW_COLOR_GRID = Color.parseColor("#AAAAAA");
		
	/*Konstruktoren*/
		public GraphView(Context context, AttributeSet attrs, int defStyle, WeatherDataCollection data){
			super(context, attrs, defStyle);
			this.useDataCollection(data);
			this.funcs = new FunctionCollection(context);
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "GraphView erzeugt");
		}
		public GraphView(Context context, AttributeSet attrs, int defStyle){
			super(context, attrs, defStyle);
			this.funcs = new FunctionCollection(context);
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "GraphView erzeugt");
		}
		public GraphView(Context context, AttributeSet attrs){
			super(context, attrs);
			this.funcs = new FunctionCollection(context);
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "GraphView erzeugt");
		}
		public GraphView(Context context){
			super(context);
			this.funcs = new FunctionCollection(context);
            if (FunctionCollection.s_getDebugState())
                Log.d(TAG, "GraphView erzeugt");
		}
	/*Public Deklarationen*/
	public void onDraw(Canvas canvas){
		canvas = this.drawCoordinateSystem(canvas);

		Paint linePaint = new Paint(DRAW_COLOR_RAIN);
		linePaint.setColor(DRAW_COLOR_COORDINATES);
	}
	//Diese Methode empfängt die WeatherDataCollection, die als Graphen dargestellt werden soll
	public void useDataCollection(WeatherDataCollection weatherData){
		this.datasets = weatherData;
        if (FunctionCollection.s_getDebugState())
            Log.d(TAG, "Datensätze empfangen: " + weatherData.getSize() + " Wetterdaten");
	}
	
	/*Private Deklarationen*/
	private Canvas drawCoordinateSystem(Canvas canvas){
		Paint linePaint = new Paint();
		linePaint.setColor(DRAW_COLOR_COORDINATES);
		//DisplayMetrics metrics = this.funcs.getMetrics();
	//	int width = metrics.widthPixels;
	//	int height = metrics.heightPixels;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

		canvas.drawLine(40, height - 40, width - 40, height - 40, linePaint);
		canvas.drawLine(40, 40, 40, height - 40, linePaint);

		return canvas;
	}
	private Canvas drawGrid(Canvas canvas){
		int x,y;
		int[] dims = this.getDimensioning();
		Paint gridPaint = new Paint();
		gridPaint.setColor(DRAW_COLOR_GRID);
		
		DisplayMetrics metrics = this.funcs.getMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		
		for (x = 20; x <= width-10; x += 10){ 
			canvas.drawLine(x, 10, x, height - 10, gridPaint);
		}
		for (y = height - 20; y >= 10; y -= 10){
			canvas.drawLine(10, y, width - 10, y, gridPaint);
		}
		
		return canvas;
	}
	private int[] getDimensioning(){
		int[] dims = new int[2];
		/*
			[0] Pixelverh�ltnis in x-richtung
			[1] Pixelverh�ltnis in y-richtung
		*/
		dims[0] = 15;
		dims[1] = 10;
		
		return dims;
	}
	private Canvas drawTemperatur(Canvas canvas, WeatherDataCollection sequence){
		WeatherData data = sequence.getFirst();
		Point p1 = new Point(10,10);
		Paint temperaturePaint = new Paint();
		temperaturePaint.setColor(DRAW_COLOR_TEMPERATURE);
		Point p2 = this.simulateDrawP2P(p1, data);
		canvas.drawLine(p1.x, p1.y, p2.x, p2.y, temperaturePaint);
		while (sequence.hasNext()){
			data = sequence.getNext();
			p1 = p2;
			p2 = this.simulateDrawP2P(p1, data);
			canvas.drawLine(p1.x, p1.y, p2.x, p2.y, temperaturePaint);
		}
		return canvas;
	}
	private Point simulateDrawP2P(Point start, WeatherData obj){
		Point endPoint = new Point();
		
		return endPoint;
	}
	private Double[] getTemperatureMinMax(){
		Double[] minMax = new Double[2]; //[0]Min [1]Max
		if(this.datasets.getSize() > 0){
			WeatherData data = this.datasets.getFirst();
			minMax[0] = data.getTemperaturMin();
			minMax[1] = data.getTemperatureMax();
			
			while (this.datasets.hasNext()){
				if (data.getTemperatureMax() > minMax[1])
					minMax[1] = data.getTemperatureMax();
				
				if (data.getTemperaturMin() < minMax[0])
					minMax[0] = data.getTemperaturMin();
					
				data = this.datasets.getNext();
			}
		}
		else {}
		
		return minMax;
	}
	private int[] getDateTimeMinMax(){
		int[] dateTimeMinMax = new int[4];
		/*
			[0] MinDate Date
			[1] MinDate MinTime
			[2] MaxDate Date
			[3] MaxDate MaxTime
		*/
		if (this.datasets.getSize() > 0){
			WeatherData data = this.datasets.getFirst();

		}
		
		return dateTimeMinMax;
	}
}
