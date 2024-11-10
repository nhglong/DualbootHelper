package com.david42069.dualboothelper;

import dev.oneuiproject.oneui.preference.HorizontalRadioPreference;
import dev.oneuiproject.oneui.preference.TipsCardPreference;
import dev.oneuiproject.oneui.preference.internal.PreferenceRelatedCard;
import dev.oneuiproject.oneui.utils.PreferenceUtils;
import dev.oneuiproject.oneui.widget.Toast;
import android.app.Activity;
import android.content.SharedPreferences;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.DropDownPreference;
import androidx.preference.PreferenceManager;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import com.topjohnwu.superuser.Shell;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import dev.oneuiproject.oneui.layout.ToolbarLayout;
import dev.oneuiproject.oneui.utils.ActivityUtils;
import androidx.annotation.NonNull;
import androidx.appcompat.util.SeslMisc;
import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeslSwitchPreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;
import android.content.SharedPreferences;
import android.widget.LinearLayout;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import android.view.View;
import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import android.graphics.Color;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import android.os.CountDownTimer;
import android.net.Uri;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.LayoutInflater;
import androidx.annotation.StringRes;
import dev.oneuiproject.oneui.widget.ui.widget.CardView;



public class PreferencesFragment extends PreferenceFragmentCompat {

    private boolean isPreferencesLoaded = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment, rootKey); // Link to your preferences XML file

        // Initialize the flag when preferences are loaded
        isPreferencesLoaded = true;

        // Set up actions based on preference changes
        PreferenceManager.getDefaultSharedPreferences(requireContext())
                .registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
                    if (isPreferencesLoaded) {  // Check flag to avoid initial triggering
                        if ("slot_a_actions".equals(key) || "slot_b_actions".equals(key) || "misc_actions".equals(key)) {
                            String action = sharedPreferences.getString(key, "");
                            showConfirmationDialog(action);
                        }
                    }
                });
    }

    // Ensure that the flag is reset when the fragment is detached
    @Override
    public void onDetach() {
        super.onDetach();
        isPreferencesLoaded = false;
    }

    private void showConfirmationDialog(String action) {
        Activity activity = requireActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String title = getString(R.string.dialog_confirm_title);
        String message = getString(R.string.dialog_confirm);
        String positiveButton = getString(R.string.dialog_yes);
        String negativeButton = getString(R.string.dialog_no);

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, (dialog, which) -> {
                    showLoadingDialog();
                    executeAction(action);
                })
                .setNegativeButton(negativeButton, null);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showLoadingDialog() {
        Activity activity = requireActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    
    private void executeAction(String action) {
        String scriptFile = "";

        // Assign corresponding script based on action
        switch (action) {
            case "switcha":
                scriptFile = "R.raw.switcha";
                break;
            case "switchar":
                scriptFile = "R.raw.switchar";
                break;
            case "switchb":
                scriptFile = "R.raw.switchb";
                break;
            case "switchbr":
                scriptFile = "R.raw.switchbr";
                break;
            case "download":
                scriptFile = "R.raw.download";
                break;
            case "shuwdown":
                scriptFile = "R.raw.shutdown";
                break;
        }

        // Execute the shell command based on the selected action
        Shell.cmd(getResources().openRawResource(getResources().getIdentifier(scriptFile.replace("R.raw.", ""), "raw", getActivity().getPackageName()))).exec();
    }

}
