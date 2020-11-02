package com.zivapp.notes.util.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.zivapp.notes.R;

public class DialogDeleteNote extends AppCompatDialogFragment {

    private static final String TAG = "DialogDeleteNote";

    public static AlertDialog.Builder onCreateDialogBuilder(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_note, null);

        builder.setView(view);

        return builder;
    }
}
