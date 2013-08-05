package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Diese abgeleitete Klasse stellt ein GUI-Element dar, welches eine WeatherDataCollection als Graph ausgibt
 */
public class GraphView extends View
{
	/*Klassenvariablen*/
	private WeatherDataCollection datasets;
    private ResolutionBasedConfiguration res;

    private int temperatureSpan = 0;
    private int minTemperature = 0;
    private int widthPixels = 0;
    private int heightPixels = 0;
    private int pixelsForOneDegree = 50;
    private int pixelsForOneTimeSeq = 50;

    private Context context = null;

    private String graphTitle = "";
	
	/*Klassenkonstanten*/
    private static final String TAG = "GraphView";
    private static final int DRAW_COLOR_TEMPERATURE_MAX = Color.parseColor("#AA0000");
    private static final int DRAW_COLOR_TEMPERATURE_MIN = Color.parseColor("#0000AA");
    private static final int DRAW_COLOR_COORDINATES = Color.parseColor("#000000");
    private static final int DRAW_COLOR_GRID = Color.parseColor("#AAAAAA");
    private static final int DRAW_COLOR_LIGHTGRAY = Color.parseColor("#DDDDDD");
    private static final int DRAW_COLOR_DAYBREAK = Color.parseColor("#00AA00");
    private static final int DRAW_COLOR_ZERODEGREES = Color.parseColor("#00C1FF");
    private static final int DRAW_COLOR_TODAY = Color.parseColor("#FFF172");
    private static final int DRAW_COLOR_AVERAGETEMPERATURE = Color.parseColor("#FF9900");
		
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

    /**
     * Vom Objekt View überschriebene Methode zum Zeichen des Objekts
     * @param canvas Canvaselement der View
     */
	public void onDraw(Canvas canvas){
		canvas = this.drawCoordinateSystem(canvas);
	}

    /**
     * Setzt die WeatherDataCollection fest, die m Graphen angezeigt werden soll
     * @param weatherData WeatherDataCollection - Eine Range von WeatherData in chronologischer Reihenfolge
     */
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

    /**
     * Methode, in der die Daten der verwedeten Sequenz zu einem Graphen verarbeitet werden.
     * Der Graph wird dynamisch erzeugt und passt seine Breiten und Höhen dynamisch an die Gegebenheiten an, die von der Sequenz bereitgestellt werden
     * @param canvas CanvasElement der View
     * @return canvasElement der View mit Zeichenung
     */
	private Canvas drawCoordinateSystem(Canvas canvas){

        /* Paint OBJEKTE für den Graphen */
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

        Paint todayBgPaint = new Paint();
        todayBgPaint.setColor(DRAW_COLOR_TODAY);

        Paint zeroDegreeLinePaint = new Paint();
        zeroDegreeLinePaint.setColor(DRAW_COLOR_ZERODEGREES);

        Paint averageLine = new Paint();
        averageLine.setColor(DRAW_COLOR_AVERAGETEMPERATURE);

        /* Paints konfigurieren */
        maxLinePaint.setStyle(Paint.Style.STROKE);
        minLinePaint.setStyle(Paint.Style.STROKE);
        maxLinePaint.setStrokeWidth(this.res.draw_line_width);
        minLinePaint.setStrokeWidth(this.res.draw_line_width);
        zeroDegreeLinePaint.setStrokeWidth(this.res.draw_line_width + 1);
        averageLine.setStrokeWidth(2);
        averageLine.setStyle(Paint.Style.STROKE);
        //averageLine.setPathEffect(new DashPathEffect(new float[] {10,20}, 10));

        /* Gefüllt 80% Alpha */
        maxFillPaint.setStyle(Paint.Style.FILL);
        minFillPaint.setStyle(Paint.Style.FILL);
        todayBgPaint.setStyle(Paint.Style.FILL);
        maxFillPaint.setAlpha(50);
        minFillPaint.setAlpha(50);
        todayBgPaint.setAlpha(50);

        /* Variablen */
        int max = this.datasets.getSize();
        int allTemperatures = 0;
        PointF[] maxPoints = new PointF[max];
        PointF[] minPoints = new PointF[max];
        int[] todayPoints = new int[5];
        Path maxPath = new Path();
        Path minPath = new Path();
        Path todayPath = new Path();

        int width = this.widthPixels;
        int tx = 0;
        int height = this.heightPixels;


        //Punkte im Zentrum des KOORD Systems (0|0)
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

        /* NULLPUNKTE merken */
        int zeroMaxX = maxPosX + this.pixelsForOneTimeSeq;
        int zeroMaxY = maxPosY;
        int zeroMinX = minPosX + this.pixelsForOneTimeSeq;
        int zeroMinY = minPosY;

        /* Heute als String bestimmen */
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.");
        String todayStr = df.format(d);

        /************************************************************
         * Sammeln der Daten der Punkte und Linien der einzelnen WeatherData
         ************************************************************/
        for (i = 0; i < max; i++){
            //Für jeden Datensatz Temperaturen einzeichnen

            /*Temperatur zeichnen*/
            WeatherData data = this.datasets.getItemAtPos(i);
            int t1 = data.getTemperaturMinInt() - this.minTemperature;
            int t2 = data.getTemperatureMaxInt() - this.minTemperature;
            /* Für die Durchschnittsberechnung alle Temperaturen hinzufügen */
            allTemperatures += (t1 + t2);

            //Neuen Positionspunkte berechnen
            ny1 =  (height - this.res.padding_bottom) - (this.pixelsForOneDegree * t2);
            nx1 = maxPosX + this.pixelsForOneTimeSeq;

            ny2 =  (height - this.res.padding_bottom) - (this.pixelsForOneDegree * t1);
            nx2 = minPosX + this.pixelsForOneTimeSeq;


            maxPoints[i] = new PointF(nx1, ny1);
            minPoints[i] = new PointF(nx2, ny2);

            /* Aktuellen Tag hervorheben */
            if (todayStr.equals(data.getDateStr())){
                //Heute
                if (tx == 0){
                    if (i > 0){
                        todayPoints[tx] = maxPosX;
                        tx++;
                    }
                    else {
                        /* Wenn heute der erste angezeigte Tag ist existieren nur 3 Frames */
                        todayPoints[tx] = nx1;
                        tx++;
                    }
                }
                if (tx < 5){
                    todayPoints[tx] = nx1;
                    tx++;
                }

            }

            minPosX = nx2;
            maxPosX = nx1;
            minPosY = ny2;
            maxPosY = ny1;
        }
        /************************************************************
         * Punkte der WeatherData-Temperaturen zu einem Pfad zusammensetzen
         ************************************************************/
        maxPath.moveTo(maxPoints[0].x, maxPoints[0].y);
        minPath.moveTo(minPoints[0].x, minPoints[0].y);
        for (i = 1; i < max; i++){
            maxPath.lineTo(maxPoints[i].x, maxPoints[i].y);
            minPath.lineTo(minPoints[i].x, minPoints[i].y);
        }
        /************************************************************
         * Heutigen Tag farblich erkennbar machen
         ************************************************************/
        for (i = 4; i >= 0; i--){
            if (todayPath.isEmpty()){
                todayPath.moveTo(todayPoints[i], this.res.padding_top);
            }
            else todayPath.lineTo(todayPoints[i], this.res.padding_top);
        }
        for (i = 0; i <= 4; i++){
            todayPath.lineTo(todayPoints[i], height - this.res.padding_bottom);
        }

        todayPath.lineTo(todayPoints[4],this.res.padding_top);

        canvas.drawPath(todayPath, todayBgPaint);

        /************************************************************
         * Durchschnittstemperatur einzeichnen
         ************************************************************/
        float avTemperature = allTemperatures / (max * 2);
        float avTemperatureLinePos = (height - this.res.padding_bottom) - (avTemperature * pixelsForOneDegree);
        canvas.drawLine(this.res.padding_left, avTemperatureLinePos, width - this.res.padding_right+10, avTemperatureLinePos, averageLine);

        /************************************************************
         * Den Pfad nachzeichnen als Linie
         ************************************************************/
        canvas.drawPath(maxPath, maxLinePaint);
        canvas.drawPath(minPath, minLinePaint);

        /************************************************************
         * Linienbeschriftung einfügen
         ************************************************************/
        maxLinePaint.setStrokeWidth(1);
        minLinePaint.setStrokeWidth(1);

        averageLine.setStrokeWidth(1);
        canvas.drawText((avTemperature + this.minTemperature)+"", width - this.res.padding_right + 10, avTemperatureLinePos, averageLine);

        canvas.drawText(this.context.getString(R.string.temp_max), maxPosX + 5, maxPosY - 15, maxLinePaint);
        canvas.drawText(this.context.getString(R.string.temp_min), minPosX + 5, minPosY + 15, minLinePaint);


        /************************************************************
         * Pfade wieder zum Startpunkt schliessen, um den Graph zu füllen
         * Unterer Rand des MAxPath ist oberer Rand des MinPath
         * Unterer Rand des MinPath ist die X-Achse
         ************************************************************/
        for (i = (max - 1); i >= 0; i--){
            maxPath.lineTo(minPoints[i].x, minPoints[i].y);
        }

        maxPath.lineTo(this.res.padding_left, (height - this.res.padding_bottom));

        minPath.lineTo(nx2, (height - this.res.padding_bottom));
        minPath.lineTo(this.res.padding_left, (height - this.res.padding_bottom));

        /************************************************************
         * Beide Path zum jeweiligen Startpunkt hin schliessen und mit Alpha gefüllt zeichnen
         ************************************************************/
        maxPath.lineTo(zeroMaxX, zeroMaxY);
        minPath.lineTo(zeroMinX, zeroMinY);

        canvas.drawPath(maxPath, maxFillPaint);
        canvas.drawPath(minPath, minFillPaint);

        /************************************************************
         * Koordinatensystem zeichnen
         ************************************************************/
        canvas.drawLine(this.res.padding_left, this.res.padding_top - 10, this.res.padding_left, height - this.res.padding_bottom, linePaint);   //Y-Achse
        canvas.drawLine(this.res.padding_left, height - this.res.padding_bottom, width - this.res.padding_right + 20, height - this.res.padding_bottom, linePaint);    //X-Achse

        /************************************************************
         * Pfeile an die Achsen zeichnen
         ************************************************************/
        canvas.drawLine(this.res.padding_left, this.res.padding_top - 10, this.res.padding_left-5, this.res.padding_top-5, linePaint);       //Y-Achse
        canvas.drawLine(this.res.padding_left, this.res.padding_top - 10, this.res.padding_left+5, this.res.padding_top-5, linePaint);       //Y-Achse

        canvas.drawLine(width - this.res.padding_right + 20, height - this.res.padding_bottom, width-this.res.padding_right+15, height - this.res.padding_bottom+5, linePaint);       //X-Achse
        canvas.drawLine(width - this.res.padding_right + 20, height - this.res.padding_bottom, width-this.res.padding_right+15, height - this.res.padding_bottom-5, linePaint);       //X-Achse

        /************************************************************
         * Y-Achse zeichnen inkl. Hilfslinien
         ************************************************************/
        i = 0;
        while (i < this.temperatureSpan){
            posY -= this.pixelsForOneDegree;
            int currentTemperature = (this.minTemperature + i + 1);
            if (currentTemperature == 0 && i > 0)      // 0 Grad farblich Kennzeichnen
                canvas.drawLine(this.res.padding_left, posY, width - this.res.padding_right+10, posY, zeroDegreeLinePaint);
            else
                canvas.drawLine(this.res.padding_left, posY, width - this.res.padding_right+10, posY, gridLinePaint);
            canvas.drawLine(this.res.padding_left-10, posY, this.res.padding_left+10, posY, linePaint);
            canvas.drawText(""+currentTemperature, this.res.temperatures_padding, posY, linePaint);
            i++;
        }


        /************************************************************
         * X-Achse zeichnen inkl. Hilfslinien
         ************************************************************/
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
        /************************************************************
         * Punkte an den Wendepunkten des Graphen zeichnen
         ************************************************************/
        maxLinePaint.setStyle(Paint.Style.FILL);
        minLinePaint.setStyle(Paint.Style.FILL);
        for (i=0; i < max; i++){
            canvas.drawCircle(maxPoints[i].x, maxPoints[i].y, this.res.draw_point_radius, maxLinePaint);
            canvas.drawCircle(minPoints[i].x, minPoints[i].y, this.res.draw_point_radius, minLinePaint);
        }
        /************************************************************
         * Achsen beschriften
         ************************************************************/
        canvas.drawText(this.context.getString(R.string.caption_degree), this.res.padding_left - 20, this.res.padding_top, linePaint);
        canvas.drawText(this.context.getString(R.string.caption_Time), width - this.res.padding_right, height - this.res.padding_bottom + 20, linePaint);
        /************************************************************
         * Graphenbeschriftung einfügen am obere Rand des Graphen
         ************************************************************/
        linePaint.setTextSize(18);
        canvas.drawText(this.graphTitle,  this.res.padding_left, Math.round(this.res.padding_top/2), linePaint);

		return canvas;
	}

    /**
     * Beschriftung des Graphen festlegen
     * @param title String der GraphTitel
     */
    public void setGraphTitle(String title){
             this.graphTitle = title;
    }
}
