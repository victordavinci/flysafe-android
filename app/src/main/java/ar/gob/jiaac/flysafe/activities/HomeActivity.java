package ar.gob.jiaac.flysafe.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                toggle.syncState();
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                });
            }
            }
        });

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        supportInvalidateOptionsMenu();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                while (getSupportFragmentManager().popBackStackImmediate());
                replaceFragment(MainMenuFragment.newInstance(), false);
                break;
            case R.id.nav_stats:
                while (getSupportFragmentManager().popBackStackImmediate());
                replaceFragment(StatsFragment.newInstance(), false);
                break;
            case R.id.nav_news:
                while (getSupportFragmentManager().popBackStackImmediate());
                replaceFragment(WebViewFragment.newInstance("https://twitter.com/JIAAC_AR"), false);
                break;
            case R.id.nav_notifications:
                while (getSupportFragmentManager().popBackStackImmediate());
                replaceFragment(NotificationsFragment.newInstance(), false);
                break;
            case R.id.nav_map:
                while (getSupportFragmentManager().popBackStackImmediate());
                replaceFragment(ReportsMapFragment.newInstance(), false);
                break;
        }
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (nv != null) {
                    TextView email = nv.getHeaderView(0).findViewById(R.id.textViewUserEmail);
                    email.setText(user.getEmail());
                }
                replaceFragment(MainMenuFragment.newInstance(), false);
            } else {
                finish();
            }
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
