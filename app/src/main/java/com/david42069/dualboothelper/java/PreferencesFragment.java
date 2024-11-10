package com.david42069.dualboothelper;

import dev.oneuiproject.oneui.preference.HorizontalRadioPreference;
import dev.oneuiproject.oneui.preference.TipsCardPreference;
import dev.oneuiproject.oneui.preference.internal.PreferenceRelatedCard;
import dev.oneuiproject.oneui.utils.PreferenceUtils;
import dev.oneuiproject.oneui.widget.Toast;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.DropDownPreference;
import androidx.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.util.Log;
import com.topjohnwu.superuser.Shell;
import java.util.HashMap;
import java.util.Map;



public class PreferencesFragment extends PreferenceFragmentCompat {

    private static final String PREF_FIRST_RUN = "pref_first_run";
    private final Map<String, Integer> actionTitles = new HashMap<>();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment, rootKey);

        // Initialize the map with action-title mappings
        actionTitles.put("switcha", R.string.reboot_a);
        actionTitles.put("switchar", R.string.recovery_a);
        actionTitles.put("switchb", R.string.reboot_b);
        actionTitles.put("switchbr", R.string.recovery_b);
        actionTitles.put("download", R.string.dl_mode);
        actionTitles.put("shutdown", R.string.poweroff);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean isFirstRun = sharedPreferences.getBoolean(PREF_FIRST_RUN, true);

        if (isFirstRun) {
            // Set first-run flag to false, so future launches are not treated as first runs
            sharedPreferences.edit().putBoolean(PREF_FIRST_RUN, false).apply();
        }

        // Register preference change listener
        sharedPreferences.registerOnSharedPreferenceChangeListener((prefs, key) -> {
            String action = prefs.getString(key, "");
            if (!action.isEmpty() && (isFirstRun && !action.equals("default"))) {
                // Only show the confirmation dialog if the selected action differs from default value
                showConfirmationDialog(action);
            }
        });
    }

    private void showConfirmationDialog(String action) {
        Activity activity = requireActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Retrieve user-friendly title from the map
        String title = actionTitles.containsKey(action) ? 
                       getString(actionTitles.get(action)) + "?" : 
                       action + "?";  // Fallback to action if not found in map

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
