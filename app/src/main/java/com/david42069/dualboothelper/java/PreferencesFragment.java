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

    private boolean isPreferencesLoaded = false;
    private final Map<String, Integer> actionTitles = new HashMap<>();
    private static final String PREF_FIRST_RUN = "pref_first_run";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment, rootKey);

        // Initialize the action-to-title mapping
        actionTitles.put("switcha", R.string.reboot_a);
        actionTitles.put("switchar", R.string.recovery_a);
        actionTitles.put("switchb", R.string.reboot_b);
        actionTitles.put("switchbr", R.string.recovery_b);
        actionTitles.put("download", R.string.dl_mode);
        actionTitles.put("shutdown", R.string.poweroff);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean isFirstRun = sharedPreferences.getBoolean(PREF_FIRST_RUN, true);

        if (isFirstRun) {
            // Mark the first run as complete
            sharedPreferences.edit().putBoolean(PREF_FIRST_RUN, false).apply();
            isPreferencesLoaded = false;  // Avoid showing prompts on the first run
        } else {
            isPreferencesLoaded = true;  // Set flag to true if not the first run
        }

        // Register preference change listener
        sharedPreferences.registerOnSharedPreferenceChangeListener((prefs, key) -> {
            if (isPreferencesLoaded) {  // Only proceed if not in initial setup
                String action = prefs.getString(key, "");
                if (!action.isEmpty()) {
                    showConfirmationDialog(action);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isPreferencesLoaded = true;  // Ensure this is set when returning to the fragment
    }

    private void showConfirmationDialog(String action) {
        Activity activity = requireActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Get a user-friendly title for the action
        String title = actionTitles.containsKey(action) ? 
                       getString(actionTitles.get(action)) + "?" : 
                       action + "?";

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
            case "shutdown":
                scriptFile = "R.raw.shutdown";
                break;
        }

        Shell.cmd(getResources().openRawResource(getResources().getIdentifier(scriptFile.replace("R.raw.", ""), "raw", getActivity().getPackageName()))).exec();
    }
}
