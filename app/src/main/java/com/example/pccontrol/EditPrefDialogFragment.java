package com.example.pccontrol;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class EditPrefDialogFragment extends AppCompatDialogFragment {
    private DialogResult dialogResult;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dialogResult = (DialogResult) context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_pref_dialog, container, false);

        EditText editTextMacAddress = (EditText) view.findViewById(R.id.editTextMacAddress);
        Button buttonSave = (Button) view.findViewById(R.id.buttonSave);
        Button buttonCancel = (Button) view.findViewById(R.id.buttonCancel);

        String key = getArguments().getString("key");
        String value = getArguments().getString("value");

        editTextMacAddress.setText(value);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogResult.changePreferences(key, editTextMacAddress.getText().toString());
                dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
