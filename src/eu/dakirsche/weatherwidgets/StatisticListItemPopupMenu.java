package eu.dakirsche.weatherwidgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Popupmenü für die Auswahl der Statistiktiefe
 */
public class StatisticListItemPopupMenu extends DialogFragment {
    private AlertDialog.Builder builder;
    private CharSequence[] itemlist;
    private DialogInterface.OnClickListener listen;
    private int selectedItemElementId = 0;

    public StatisticListItemPopupMenu(CharSequence[] items, DialogInterface.OnClickListener listener, int itemElement){
        itemlist = items;
        listen = listener;
        selectedItemElementId = itemElement;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());
        builder.setItems(itemlist, listen);
        // Create the AlertDialog object and return it
        return builder.create();
    }
    @Override
    public void show(FragmentManager manager, String tag){
        if (selectedItemElementId > 0)
            super.show(manager, tag);
    }

}