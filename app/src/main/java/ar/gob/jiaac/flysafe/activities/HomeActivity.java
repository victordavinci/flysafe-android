package ar.gob.jiaac.flysafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import ar.gob.jiaac.flysafe.R;
import ar.gob.jiaac.flysafe.fragments.HelpFragment;
import ar.gob.jiaac.flysafe.fragments.HomeFragment;
import ar.gob.jiaac.flysafe.fragments.MainMenuFragment;
import ar.gob.jiaac.flysafe.fragments.NewReportFragment;
import ar.gob.jiaac.flysafe.fragments.NotificationsFragment;
import ar.gob.jiaac.flysafe.fragments.ReportsMapFragment;
import ar.gob.jiaac.flysafe.fragments.StatsFragment;
import ar.gob.jiaac.flysafe.fragments.WebViewFragment;

public class HomeActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {
    private static final int RC_SIGN_IN = 15123;
    private DrawerLayout drawerLayout;
    private NavigationView nv;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (savedInstanceState == null) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build(), new AuthUI.IdpConfig.EmailBuilder().build());
            startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        nv = findViewById(R.id.nav_view);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nv.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        actionBar.setDisplayHomeAsUpEnabled(true);
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });
                    } else {
                        actionBar.setDisplayHomeAsUpEnabled(false);
                        toggle.syncState();
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                drawerLayout.openDrawer(GravityCompat.START);
                            }
                        });
                    }
                }
            }
        });

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        supportInvalidateOptionsMenu();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    while (fragmentManager.popBackStackImmediate()) ;
                    replaceFragment(MainMenuFragment.newInstance(), false);
                    break;
                case R.id.nav_stats:
                    while (fragmentManager.popBackStackImmediate()) ;
                    replaceFragment(StatsFragment.newInstance(), false);
                    break;
                case R.id.nav_news:
                    while (fragmentManager.popBackStackImmediate()) ;
                    replaceFragment(WebViewFragment.newInstance("https://twitter.com/JIAAC_AR"), false);
                    break;
                case R.id.nav_notifications:
                    while (fragmentManager.popBackStackImmediate()) ;
                    replaceFragment(NotificationsFragment.newInstance(), false);
                    break;
                case R.id.nav_map:
                    while (fragmentManager.popBackStackImmediate()) ;
                    replaceFragment(ReportsMapFragment.newInstance(), false);
                    break;
            }
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && !user.isEmailVerified()) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Se ha enviado un email a su casilla de correo", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error al enviar correo de verificación, intente de nuevo", Toast.LENGTH_LONG).show();
                            }
                            FirebaseAuth.getInstance().signOut();
                            finish();
                        }
                    });
                } else {
                    if (nv != null) {
                        TextView email = nv.getHeaderView(0).findViewById(R.id.textViewUserEmail);
                        if (user != null) {
                            email.setText(user.getEmail());
                        }
                    }
                    replaceFragment(MainMenuFragment.newInstance(), false);
                }
            } else {
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.exit_item:
                finish();
                break;
            case R.id.help_item:
                replaceFragment(new HelpFragment(), true);
                break;
            case R.id.logout_item:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void replaceFragment(Fragment f) {
        replaceFragment(f, true);
    }

    private void replaceFragment(Fragment f, boolean backStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, f);
        if (backStack) {
            ft.addToBackStack(f.getClass().getName());
        }
        ft.commit();
        if (backStack) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onNewReport() {
        replaceFragment(NewReportFragment.newInstance());
    }
}
