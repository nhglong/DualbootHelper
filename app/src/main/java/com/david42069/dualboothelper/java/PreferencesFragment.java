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



public class PreferencesFragment extends PreferenceFragmentCompat {

    private boolean isPreferencesLoaded = false;
    private static final String PREF_FIRST_RUN = "pref_first_run";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment, rootKey);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Check if it's the first run after clearing data
        boolean isFirstRun = sharedPreferences.getBoolean(PREF_FIRST_RUN, true);

        if (isFirstRun) {
            // Set first-run flag to false, so next launches are not treated as first runs
            sharedPreferences.edit().putBoolean(PREF_FIRST_RUN, false).apply();
            Log.d("PreferencesFragment", "First run detected. Skipping dialogs.");
        } else {
            isPreferencesLoaded = true;  // Enable listener if not the first run
        }

        // Set up actions based on preference changes
        sharedPreferences.registerOnSharedPreferenceChangeListener((prefs, key) -> {
            if (isPreferencesLoaded) { 
                Log.d("PreferencesFragment", "Preference changed: " + key);
                if ("slot_a_actions".equals(key)  "slot_b_actions".equals(key)  "misc_actions".equals(key)) {
                    String action = prefs.getString(key, "");
                    Log.d("PreferencesFragment", "Action selected: " + action); 
                    if (!action.isEmpty()) {
                        showConfirmationDialog(action);
                    }
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

        // Use the action as the title, with "?" appended to it
        String title = action + "?";
        String message = getString(R.string.dialog_confirm);
        String positiveButton = getString(R.string.dialog_yes);
        String negativeButton = getString(R.string.dialog_no);

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, (dialog, which) -> {
                    showLoadingDialog();
                    executeAction(action);  // Pass the action to execute based on selection
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
