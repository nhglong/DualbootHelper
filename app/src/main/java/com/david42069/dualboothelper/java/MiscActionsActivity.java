package com.david42069.dualboothelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import dev.oneuiproject.oneui.layout.ToolbarLayout;
import dev.oneuiproject.oneui.utils.ActivityUtils;

public class MiscActionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_misc);  // Create a layout file for this
        // Initialize any UI components or actions specific to Slot A
        ToolbarLayout toolbarLayout = findViewById(R.id.misc_act);
        toolbarLayout.setNavigationButtonAsBack();
    }
}
