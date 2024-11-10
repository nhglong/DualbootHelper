package com.david42069.dualboothelper;

import android.content.Intent;
import android.os.Bundle;
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
import dev.oneuiproject.oneui.preference.HorizontalRadioPreference;
import dev.oneuiproject.oneui.preference.TipsCardPreference;
import dev.oneuiproject.oneui.preference.internal.PreferenceRelatedCard;
import dev.oneuiproject.oneui.utils.PreferenceUtils;
import dev.oneuiproject.oneui.widget.Toast;
import android.app.Activity;
import com.topjohnwu.superuser.Shell;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private static final String STATUS_FILE_PATH = "/data/data/com.david42069.dualboothelper/files/status.txt";
    private static final String SLOT_A_FILE_PATH = "/data/data/com.david42069.dualboothelper/files/slota.txt";
    private static final String SLOT_B_FILE_PATH = "/data/data/com.david42069.dualboothelper/files/slotb.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Shell.getShell(shell -> {});
        cp(R.raw.parted, "parted");
		cp(R.raw.jq, "jq");
        ToolbarLayout toolbarLayout = findViewById(R.id.home);
        updateStatusCardView();
        updateSlotCardView(R.id.slota_txt, SLOT_A_FILE_PATH);
        updateSlotCardView(R.id.slotb_txt, SLOT_B_FILE_PATH);
        slotb.setText(slotbstring);

        // Dynamically add the PreferencesFragment
        if (savedInstanceState == null) {
            PreferencesFragment preferencesFragment = new PreferencesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.pref_container, preferencesFragment);
            transaction.commit();
        }
    }

    private void cp(int resourceId, String fileName) {
        try (InputStream in = getResources().openRawResource(resourceId);
             OutputStream out = new FileOutputStream(new File(getFilesDir(), fileName))) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            Log.e("FileCopyError", "Error copying file " + fileName, e);
        }
    }
    
    private void updateStatusCardView() {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(STATUS_FILE_PATH)))) {
            StringBuilder statusText = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.replace("##NOT_INSTALLED##", getString(R.string.not_installed))
                           .replace("##INSTALLED_V5##", getString(R.string.installed_v5))
                           .replace("##INSTALLED_V4##", getString(R.string.installed_v4))
                           .replace("##UNAVAILABLE##", getString(R.string.unavailable))
                           .replace("##SUPER_PARTITION##", getString(R.string.super_partition))
                           .replace("##NORMAL_NAMING##", getString(R.string.normal_naming))
                           .replace("##CAPS_NAMING##", getString(R.string.caps_naming))
                           .replace("##UFS_SDA##", getString(R.string.ufs_sda))
                           .replace("##EMMC_SDC##", getString(R.string.emmc_sdc))
                           .replace("##EMMC_MMCBLK0##", getString(R.string.emmc_mmcblk0));

                statusText.append(line).append("\n");
            }

            String textToDisplay = statusText.toString().trim().isEmpty() ? 
                                   getString(R.string.sudo_access) : statusText.toString();

            CardView statusCardView = findViewById(R.id.status);
            statusCardView.setSummaryText(textToDisplay);

        } catch (IOException e) {
            Log.e("MainActivity", "Error reading status.txt", e);
        }
    }

    private void updateSlotCardView(int cardViewId, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
            StringBuilder slotText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                slotText.append(line).append("\n");
            }

            String textToDisplay = slotText.toString().trim().isEmpty() ? 
                                   getString(R.string.unavailable) : slotText.toString();

            CardView slotCardView = findViewById(cardViewId);
            slotCardView.setSummaryText(textToDisplay);

        } catch (IOException e) {
            Log.e("MainActivity", "Error reading " + filePath, e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_app_info) {
            ActivityUtils.startPopOverActivity(this,
                    new Intent(this, AboutActivity.class),
                    null,
                    ActivityUtils.POP_OVER_POSITION_RIGHT | ActivityUtils.POP_OVER_POSITION_TOP);
            return true;
        }
        return false;
    }
}