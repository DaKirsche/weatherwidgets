package eu.dakirsche.weatherwidgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

/**
 * Popupmenü für die Auswahl einer City bei Mehrfachtreffern im Einstellungsdialog
 */
public class CitySelectPopupMenu extends DialogFragment {
    private AlertDialog.Builder builder;
    private CharSequence[] itemlist;
    private DialogInterface.OnClickListener listen;

    public CitySelectPopupMenu(CharSequence[] items, OnClickListener listener){
        itemlist = items;
        listen = listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Erzeuge Dialog mit, dem Konstruktor übergebenen Datensätzen und OnClickListener-Event mit Hilfe des AlertDialogs
        builder = new AlertDialog.Builder(getActivity());
        builder.setItems(itemlist, listen);
        // Liefert den AlertDialog zurück
        return builder.create();
    }
    @Override
    public void show(FragmentManager manager, String tag){
            super.show(manager, tag);
    }

}