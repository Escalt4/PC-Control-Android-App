package com.example.pccontrol;

public interface DialogResult {
    void doCommand(String command);

    void changePreferences(String key, String value);
}
