package com.kajianid.ustadz.ui.kajian;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.kajianid.ustadz.R;
import com.kajianid.ustadz.data.Kajian;
import com.kajianid.ustadz.data.Mosque;
import com.kajianid.ustadz.databinding.ActivityAddUpdateKajianBinding;
import com.kajianid.ustadz.prefs.CredentialPreference;
import com.kajianid.ustadz.ui.mosque.MosqueChooserActivity;
import com.kajianid.ustadz.utils.StringHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class AddUpdateKajianActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 111;
    public static final int RESULT_SAVE = 130;
    public static final int RESULT_UPDATE = 260;
    public static final String EXTRA_PARCEL_KAJIAN = "extra_parcel_kajian";

    private final Calendar calendar = Calendar.getInstance();
    private ActivityAddUpdateKajianBinding binding;

    private boolean isEditMode = false;
    private final Kajian kajian = new Kajian();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUpdateKajianBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressBar.setVisibility(View.GONE);
        binding.errorMessage.setVisibility(View.GONE);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.add_kajian));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.update_kajian));

            Kajian params = (Kajian) bundle.getParcelable(EXTRA_PARCEL_KAJIAN);
            isEditMode = true;

            kajian.setId(params.getId());
            kajian.setTitle(params.getTitle());
            kajian.setDate(params.getDate());
            kajian.setAddress(params.getAddress());
            kajian.setPlace(params.getPlace());
            kajian.setDescription(params.getDescription());
            kajian.setYtLink(params.getYtLink());
            kajian.setImgResource(params.getImgResource());
            kajian.setMosqueId(params.getMosqueId());
            kajian.setMosqueName(params.getMosqueName());

            switch (kajian.getPlace()) {
                case "Di Tempat":
                    binding.spKajianCategory.setSelection(0);
                    binding.rlKajianImage.setVisibility(View.VISIBLE);
                    binding.tilKajianAddress.setVisibility(View.VISIBLE);
                    binding.tilKajianYtLink.setVisibility(View.GONE);
                    if (kajian.getImgResource() != null) {
                        Glide.with(this)
                                .load(kajian.getImgResource())
                                .into(binding.imgKajian);
                    }
                    binding.edtKajianAddress.setText(kajian.getAddress());
                    break;
                case "Video":
                    binding.spKajianCategory.setSelection(1);
                    binding.rlKajianImage.setVisibility(View.GONE);
                    binding.tilKajianAddress.setVisibility(View.GONE);
                    binding.tilKajianYtLink.setVisibility(View.VISIBLE);
                    binding.edtKajianYtLink.setText(kajian.getYtLink());
                    break;
                case "Live Streaming":
                    break;
            }

            binding.edtKajianTitle.setText(kajian.getTitle());
            binding.tvMosqueId.setText(kajian.getId());
            binding.tvMosqueName.setText(kajian.getMosqueName());
            binding.edtKajianDescription.setText(kajian.getDescription());

            try {
                Date dateParse = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(kajian.getDate());
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateParse);
                String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateParse);

                binding.edtKajianDateDue.setText(date);
                binding.edtKajianTimeDue.setText(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            binding.spKajianCategory.setEnabled(false);
            binding.btnChooseKajianImage.setEnabled(false);
        }

        binding.btnChooseMosque.setOnClickListener(it -> {
            Intent i = new Intent(this, MosqueChooserActivity.class);
            i.putExtra(MosqueChooserActivity.EXTRA_IS_USTADZ_ONLY, true);
            startActivityForResult(i, MosqueChooserActivity.REQUEST_MOSQUE);
        });

        binding.btnChooseKajianImage.setOnClickListener(it -> {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                startActivity(i);
            } else {
                Toast.makeText(
                        this,
                        getString(R.string.file_permission_denied),
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        binding.btnRetryError.setOnClickListener(it -> {
            try {
                postData();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        binding.btnCancelError.setOnClickListener(it -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.errorMessage.setVisibility(View.GONE);
        });

        binding.spKajianCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    binding.rlKajianImage.setVisibility(View.VISIBLE);
                    binding.tilKajianAddress.setVisibility(View.VISIBLE);
                    binding.tilKajianYtLink.setVisibility(View.GONE);
                } else {
                    binding.rlKajianImage.setVisibility(View.GONE);
                    binding.tilKajianAddress.setVisibility(View.GONE);
                    binding.tilKajianYtLink.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no-op
            }
        });

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
            binding.edtKajianDateDue.setText(sdf.format(calendar.getTime()));
        };

        binding.edtKajianDateDue.setOnClickListener(it -> {
            DatePickerDialog dpd = new DatePickerDialog(
                    this,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dpd.setTitle(getString(R.string.choose_date_due));
            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // minimal hari ini
            dpd.show();
        });

        binding.edtKajianTimeDue.setOnClickListener(it -> {
            Calendar mCurrentTime = Calendar.getInstance();
            int hourNow = mCurrentTime.get(Calendar.HOUR_OF_DAY);
            int minuteNow = mCurrentTime.get(Calendar.MINUTE);

            TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
                Calendar timeCalendarSet = Calendar.getInstance();
                timeCalendarSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
                timeCalendarSet.set(Calendar.MINUTE, minute);
                String timeSet = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(timeCalendarSet);
                binding.edtKajianTimeDue.setText(timeSet);
            };

            TimePickerDialog tpd = new TimePickerDialog(
                    this,
                    timeSetListener,
                    hourNow,
                    minuteNow,
                    true
            );

            tpd.setTitle(getString(R.string.choose_time_due));
            tpd.show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) super.onBackPressed();
        else if (item.getItemId() == R.id.menuSave) {
            try {
                postData();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case MosqueChooserActivity.REQUEST_MOSQUE:
                if (data != null) {
                    Mosque mosque = (Mosque) data.getParcelableExtra(MosqueChooserActivity.EXTRA_MOSQUE_RESULT);

                    binding.tvMosqueId.setText(mosque.getId());
                    binding.tvMosqueName.setText(mosque.getMosqueName());
                }
                break;
            case RESULT_OK:
                if (requestCode == RESULT_LOAD_IMAGE && data != null) {
                    Uri selectedImage = data.getData();
                    Glide.with(this)
                            .load(selectedImage)
                            .into(binding.imgKajian);
                    String path = selectedImage.getPath().substring(5);
                    binding.tvKajianImagePath.setText(path);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void postData() throws FileNotFoundException {
        if (isEditMode) {
            // save
            String selectedItem = binding.spKajianCategory.getSelectedItem().toString();
            String[] categories = getResources().getStringArray(R.array.kajian_category_entries);

            String kajianTitle = null;
            String kajianAddress = null;
            String kajianYtLink = null;
            String kajianDescription = null;
            String kajianDateDue = null;
            String kajianTimeDue = null;

            if (selectedItem.equals(categories[0])) {
                if (StringHelper.isNullOrEmpty(binding.edtKajianAddress.getText().toString())) {
                    Toast.makeText(this, getString(R.string.kajian_address_empty), Toast.LENGTH_SHORT).show();
                    binding.edtKajianAddress.requestFocus();
                    return;
                }
            } else {
                if (StringHelper.isNullOrEmpty(binding.edtKajianYtLink.getText().toString())) {
                    Toast.makeText(this, getString(R.string.kajian_yt_link_empty), Toast.LENGTH_SHORT).show();
                    binding.edtKajianAddress.requestFocus();
                    return;
                }
            }
            if (StringHelper.isNullOrEmpty(binding.edtKajianTitle.getText().toString())) {
                Toast.makeText(this, getString(R.string.kajian_title_empty), Toast.LENGTH_SHORT).show();
                binding.edtKajianTitle.requestFocus();
                return;
            }
            if (binding.tvMosqueId.getText().toString().equals(getString(R.string.example_id))) {
                Toast.makeText(this, getString(R.string.kajian_mosque_empty), Toast.LENGTH_SHORT).show();
                binding.btnChooseMosque.requestFocus();
                return;
            }
            if (StringHelper.isNullOrEmpty(binding.edtKajianDescription.getText().toString())) {
                Toast.makeText(this, getString(R.string.kajian_desc_empty), Toast.LENGTH_SHORT).show();
                binding.edtKajianTitle.requestFocus();
                return;
            }
            if (StringHelper.isNullOrEmpty(binding.edtKajianDateDue.getText().toString()) ||
                    StringHelper.isNullOrEmpty(binding.edtKajianTimeDue.getText().toString())) {
                Toast.makeText(this, getString(R.string.kajian_due_empty), Toast.LENGTH_SHORT).show();
                binding.svAddUpdateKajian.fullScroll(ScrollView.FOCUS_DOWN);
                return;
            }

            binding.progressMessage.setVisibility(View.VISIBLE);
            binding.errorMessage.setVisibility(View.GONE);

            String kajianCategory;
            if (selectedItem.equals(categories[0])) kajianCategory = "Di Tempat";
            else if (selectedItem.equals(categories[1])) kajianCategory = "Video";
            else if (selectedItem.equals(categories[2])) kajianCategory = "Live Streaming";
            else throw new IllegalArgumentException("Invalid Kajian Category!");

            kajianTitle = binding.edtKajianTitle.getText().toString();

            if (selectedItem.equals(categories[0])) kajianAddress = binding.edtKajianAddress.getText().toString();
            else kajianYtLink = binding.edtKajianYtLink.getText().toString();

            int mosqueId = Integer.parseInt(binding.tvMosqueId.getText().toString());
            kajianDescription = binding.edtKajianDescription.getText().toString();

            kajianDateDue = binding.edtKajianDateDue.getText().toString();
            kajianTimeDue = binding.edtKajianTimeDue.getText().toString();
            String kajianDateTimeDue = kajianDateDue + " " + kajianTimeDue;

            String api = getString(R.string.server) + "api/kajian";
            AsyncHttpClient client = new AsyncHttpClient();
            CredentialPreference credential = new CredentialPreference(this);

            RequestParams params = new RequestParams();
            params.put("title", kajianTitle);
            params.put("ustadz_id", credential.getCredential().getUsername());
            params.put("mosque_id", mosqueId);

            if (selectedItem.equals(categories[0])) {
                params.put("address", kajianAddress);
                if (binding.tvKajianImagePath.getText().toString().equals(getString(R.string.example_path))) {
                    File file = new File(binding.tvKajianImagePath.getText().toString());
                    if (file.isFile())
                        params.put("file", file);
                    else
                        Toast.makeText(this, "Not found: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
            } else params.put("yt_url", kajianYtLink);
            params.put("place", kajianCategory);
            params.put("desc", kajianDescription);
            params.put("due", kajianDateTimeDue);
            client.setTimeout(60000);
            client.post(api, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    binding.progressMessage.setVisibility(View.GONE);
                    binding.errorMessage.setVisibility(View.GONE);
                    Toast.makeText(AddUpdateKajianActivity.this, getString(R.string.add_kajian_success), Toast.LENGTH_SHORT).show();

                    AddUpdateKajianActivity.this.setResult(RESULT_SAVE) ;
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    binding.progressMessage.setVisibility(View.GONE);
                    binding.errorMessage.setVisibility(View.VISIBLE);
                    String errorMsg = "Oops! An error occurred.\n" +
                            "[" + statusCode + "]\n" +
                            Arrays.toString(responseBody);
                    binding.tvErrorMessage.setText(errorMsg);
                }
            });
        } else {
            String selectedItem = binding.spKajianCategory.getSelectedItem().toString();
            String[] categories = getResources().getStringArray(R.array.kajian_category_entries);

            String kajianTitle = null;
            String kajianAddress = null;
            String kajianYtLink = null;
            String kajianDescription = null;
            String kajianDateDue = null;
            String kajianTimeDue = null;

            if (selectedItem.equals(categories[0])) {
                if (StringHelper.isNullOrEmpty(binding.edtKajianAddress.getText().toString())) {
                    Toast.makeText(this, getString(R.string.kajian_address_empty), Toast.LENGTH_SHORT).show();
                    binding.edtKajianAddress.requestFocus();
                    return;
                }
            } else {
                if (StringHelper.isNullOrEmpty(binding.edtKajianYtLink.getText().toString())) {
                    Toast.makeText(this, getString(R.string.kajian_yt_link_empty), Toast.LENGTH_SHORT).show();
                    binding.edtKajianAddress.requestFocus();
                    return;
                }
            }
            if (StringHelper.isNullOrEmpty(binding.edtKajianTitle.getText().toString())) {
                Toast.makeText(this, getString(R.string.kajian_title_empty), Toast.LENGTH_SHORT).show();
                binding.edtKajianTitle.requestFocus();
                return;
            }
            if (binding.tvMosqueId.getText().toString().equals(getString(R.string.example_id))) {
                Toast.makeText(this, getString(R.string.kajian_mosque_empty), Toast.LENGTH_SHORT).show();
                binding.btnChooseMosque.requestFocus();
                return;
            }
            if (StringHelper.isNullOrEmpty(binding.edtKajianDescription.getText().toString())) {
                Toast.makeText(this, getString(R.string.kajian_desc_empty), Toast.LENGTH_SHORT).show();
                binding.edtKajianTitle.requestFocus();
                return;
            }
            if (StringHelper.isNullOrEmpty(binding.edtKajianDateDue.getText().toString()) ||
                    StringHelper.isNullOrEmpty(binding.edtKajianTimeDue.getText().toString())) {
                Toast.makeText(this, getString(R.string.kajian_due_empty), Toast.LENGTH_SHORT).show();
                binding.svAddUpdateKajian.fullScroll(ScrollView.FOCUS_DOWN);
                return;
            }

            binding.progressMessage.setVisibility(View.VISIBLE);
            binding.errorMessage.setVisibility(View.GONE);

            String kajianCategory;
            if (selectedItem.equals(categories[0])) kajianCategory = "Di Tempat";
            else if (selectedItem.equals(categories[1])) kajianCategory = "Video";
            else if (selectedItem.equals(categories[2])) kajianCategory = "Live Streaming";
            else throw new IllegalArgumentException("Invalid Kajian Category!");

            kajianTitle = binding.edtKajianTitle.getText().toString();

            if (selectedItem.equals(categories[0])) kajianAddress = binding.edtKajianAddress.getText().toString();
            else kajianYtLink = binding.edtKajianYtLink.getText().toString();

            int mosqueId = Integer.parseInt(binding.tvMosqueId.getText().toString());
            kajianDescription = binding.edtKajianDescription.getText().toString();

            kajianDateDue = binding.edtKajianDateDue.getText().toString();
            kajianTimeDue = binding.edtKajianTimeDue.getText().toString();
            String kajianDateTimeDue = kajianDateDue + " " + kajianTimeDue;

            String api = getString(R.string.server) + "api/kajian/" + kajian.getId();
            AsyncHttpClient client = new AsyncHttpClient();
            CredentialPreference credential = new CredentialPreference(this);

            RequestParams params = new RequestParams();
            params.put("title", kajianTitle);
            params.put("ustadz_id", credential.getCredential().getUsername());
            params.put("mosque_id", mosqueId);

            if (selectedItem.equals(categories[0])) {
                params.put("address", kajianAddress);
                if (binding.tvKajianImagePath.getText().toString().equals(getString(R.string.example_path))) {
                    File file = new File(binding.tvKajianImagePath.getText().toString());
                    if (file.isFile())
                        params.put("file", file);
                    else
                        Toast.makeText(this, "Not found: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
            } else params.put("yt_url", kajianYtLink);
            params.put("place", kajianCategory);
            params.put("desc", kajianDescription);
            params.put("due", kajianDateTimeDue);
            client.setTimeout(60000);
            client.put(api, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    binding.progressMessage.setVisibility(View.GONE);
                    binding.errorMessage.setVisibility(View.GONE);
                    Toast.makeText(AddUpdateKajianActivity.this, getString(R.string.add_kajian_success), Toast.LENGTH_SHORT).show();

                    AddUpdateKajianActivity.this.setResult(RESULT_UPDATE) ;
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    binding.progressMessage.setVisibility(View.GONE);
                    binding.errorMessage.setVisibility(View.VISIBLE);
                    String errorMsg = "Oops! An error occurred.\n" +
                            "[" + statusCode + "]\n" +
                            Arrays.toString(responseBody);
                    binding.tvErrorMessage.setText(errorMsg);
                }
            });
        }
    }
}