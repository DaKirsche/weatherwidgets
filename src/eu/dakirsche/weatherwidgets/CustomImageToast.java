package eu.dakirsche.weatherwidgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CustomImageToast extends Toast {
	CustomImageToast(Context context){
		super(context);
	}
	
	public static Toast makeText(Context context, CharSequence text, int duration){
			return Toast.makeText(context, text, duration);
	}
	public static Toast makeText(Context context, int resId, int duration){
		return Toast.makeText(context, resId, duration);
	}
	
	public static Toast makeImageToast(Activity activity, int drawableId, CharSequence text, int duration){
		
		Context context = activity.getApplicationContext();
		
	//	View view = activity.findViewById(android.R.id.content).getRootView();
		LayoutInflater inflater = activity.getLayoutInflater();

		ViewGroup vg = (ViewGroup) activity.findViewById(R.id.toast_layout_root);
		

		View layout = inflater.inflate(R.layout.imagetoast_layout, vg);
		
		TextView txt = (TextView) layout.findViewById(R.id.toastTitle);
		
		/*Icon in das TextView einfügen*/
		Drawable img = context.getResources().getDrawable(drawableId);
		img.setBounds( 0, 0, 20, 20 );
		txt.setCompoundDrawables( img, null, null, null );
		
		txt.setText(text);
		
		//Return the application context
		Toast toast = new Toast(context);
		//Set toast gravity to bottom
		toast.setGravity(Gravity.BOTTOM, 0, 10);
		//Set toast duration
		toast.setDuration(duration);
		//Set the custom layout to Toast
		toast.setView(layout);
		//Display toast
		
		return toast;
	}
	
	public static Toast makeImageToast(Activity activity, int drawableId, int strResId, int duration){
		CharSequence text = activity.getString(strResId);
		return CustomImageToast.makeImageToast(activity, drawableId, text, duration);
	}
	
}
