package com.example.applibrary;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.applibrary.data.remote.dto.TicketDtos;
import com.example.applibrary.data.repository.ApiResult;
import com.example.applibrary.databinding.ActivityMainBinding;
import com.example.applibrary.ui.ticket.TicketInfoDialog;
import com.example.applibrary.util.QrScanUrlHelper;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), ignored -> {});

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);
        if (navHost == null) return;
        NavController nav = navHost.getNavController();

        binding.getRoot().post(() -> {
            if (savedInstanceState != null) return;
            LibraryApplication app = (LibraryApplication) getApplication();
            if (app.getAppContainer().getAuthRepository().isLoggedIn()) {
                requestNotificationPermissionIfNeeded();
                nav.navigate(R.id.mainFragment);
            }
        });

        handleTicketIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleTicketIntent(intent);
    }

    private void handleTicketIntent(Intent intent) {
        if (intent == null || intent.getData() == null) return;
        Uri uri = intent.getData();
        String token = QrScanUrlHelper.extractToken(uri);
        if (token == null || token.isBlank()) return;

        View anchor = findViewById(R.id.nav_host);
        if (anchor == null) {
            anchor = findViewById(android.R.id.content);
        }

        View snackAnchor = anchor;
        new Thread(() -> {
            ApiResult<TicketDtos.QrCardView> result = ((LibraryApplication) getApplication())
                    .getAppContainer().getTicketRepository().resolveToken(token);
            runOnUiThread(() -> {
                if (result instanceof ApiResult.Success) {
                    TicketDtos.QrCardView view =
                            ((ApiResult.Success<TicketDtos.QrCardView>) result).getData();
                    TicketInfoDialog.newInstance(
                            view.fullName,
                            view.cardNumber,
                            view.status,
                            view.validUntil
                    ).show(getSupportFragmentManager(), "ticket_scan");
                } else if (result instanceof ApiResult.Error) {
                    Snackbar.make(snackAnchor,
                            ((ApiResult.Error<TicketDtos.QrCardView>) result).getMessage(),
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }).start();
        intent.setData(null);
    }

    public void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
    }
}
