package com.example.pccontrol;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

public class ConfirmDialogFragment extends AppCompatDialogFragment {
    private DialogResult dialogResult;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dialogResult = (DialogResult) context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_dialog, container, false);
        String title = getArguments().getString("title");
        String command = getArguments().getString("command");

        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        Button buttonAccept = (Button) view.findViewById(R.id.buttonAccept);
        Button buttonCancel = (Button) view.findViewById(R.id.buttonCancel);

        textViewTitle.setText(title);
        switch (command) {
            case "Off":
                textViewTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.off_icon_black, 0, 0, 0);
                break;
            case "Sleep":
                textViewTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sleep_icon_black, 0, 0, 0);
                break;
            case "Hibernate":
                textViewTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hibernate_icon_black, 0, 0, 0);
                break;
            case "On":
                textViewTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.on_icon_black, 0, 0, 0);
                break;
        }

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogResult.doCommand(command);
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
