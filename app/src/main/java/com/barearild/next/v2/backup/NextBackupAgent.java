package com.barearild.next.v2.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

import com.barearild.next.v2.NextOsloApp;


public class NextBackupAgent extends BackupAgentHelper {

    static final String PREFS_BACKUP_KEY = "prefs";

    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, NextOsloApp.USER_PREFERENCES);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}
