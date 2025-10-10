package com.example.flashcard;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView textViewAppName = findViewById(R.id.textViewAppName);
        TextView textViewVerion = findViewById(R.id.textViewVersion);
        TextView textViewGroup = findViewById(R.id.textViewGroup);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent MenuActivity = new Intent(this, MenuActivity.class);
            startActivity(MenuActivity);
        });

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = packageInfo.versionName;
            textViewVerion.setText(getString(R.string.version_label, version));
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        textViewAppName.setText(R.string.app_name);
        textViewGroup.setText(R.string.group);
    }
}