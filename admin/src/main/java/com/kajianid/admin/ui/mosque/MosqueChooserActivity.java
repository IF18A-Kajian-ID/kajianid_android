package com.kajianid.admin.ui.mosque;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kajianid.admin.R;
import com.kajianid.admin.databinding.ActivityMosqueChooserBinding;

import java.util.Objects;

public class MosqueChooserActivity extends AppCompatActivity {

    public static final String EXTRA_MOSQUE_RESULT = "extra_mosque_result";
    public static final String EXTRA_IS_USTADZ_ONLY = "extra_is_ustadz_only";
    public static final int REQUEST_MOSQUE = 100;
    private MosqueChooserAdapter mosqueChooserAdapter;
    private MosqueChooserViewModel mosqueChooserViewModel;
    private ActivityMosqueChooserBinding binding;
    private boolean isUstadzOnly = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityMosqueChooserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setFinishOnTouchOutside(false);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.choose_mosque);

        mosqueChooserAdapter = new MosqueChooserAdapter();
        mosqueChooserAdapter.notifyDataSetChanged();

        binding.rvMosqueList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMosqueList.setAdapter(mosqueChooserAdapter);

        binding.progressMessage.setVisibility(View.VISIBLE);

        binding.btnBack.setOnClickListener(ignored -> {
            finish();
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isUstadzOnly = bundle.getBoolean(EXTRA_IS_USTADZ_ONLY, true);

            mosqueChooserViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory())
                    .get(MosqueChooserViewModel.class);
            mosqueChooserViewModel.getMosques().observe(this, mosques -> {
                if (mosques != null) {
                    binding.progressMessage.setVisibility(View.GONE);
                    mosqueChooserAdapter.setMosques(mosques);
                }
            });
            mosqueChooserViewModel.setMosqueAsync(
                    this,
                    mosqueChooserAdapter,
                    "",
                    isUstadzOnly
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mosque_chooser, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.search_mosque));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.progressMessage.setVisibility(View.VISIBLE);
                mosqueChooserViewModel.setMosqueAsync(
                        MosqueChooserActivity.this,
                        mosqueChooserAdapter,
                        query,
                        isUstadzOnly);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /* no-op */
                return true;
            }
        });

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}