package org.wit.myrent.activities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.wit.android.helpers.ContactHelper;
import org.wit.android.helpers.IntentHelper;
import org.wit.myrent.R;
import org.wit.myrent.app.MyRentApp;
import org.wit.myrent.models.Portfolio;
import org.wit.myrent.models.Residence;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.DatePickerDialog;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import static org.wit.android.helpers.ContactHelper.sendEmail;
import static org.wit.android.helpers.IntentHelper.navigateUp;
import static org.wit.android.helpers.IntentHelper.startActivityWithData;

import android.support.design.widget.FloatingActionButton;

import android.widget.Toast;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import static org.wit.android.helpers.CameraHelper.showPhoto;
import android.widget.ImageView;

public class ResidenceFragment extends Fragment implements TextWatcher,
        OnCheckedChangeListener,
        OnClickListener,
        DatePickerDialog.OnDateSetListener,
        View.OnLongClickListener,
        Callback<Residence>
{
    public static   final String  EXTRA_RESIDENCE_ID = "myrent.RESIDENCE_ID";

    private static  final int     REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 0;

    private EditText geolocation;
    private CheckBox rented;
    private Button   dateButton;
    private Button   tenantButton;
    private Button   reportButton;

    private Residence   residence;
    private Portfolio   portfolio;

    private String emailAddress = "";
    private ImageView cameraButton;
    private ImageView photoView;

    MyRentApp app;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Long resId = (Long)getArguments().getSerializable(EXTRA_RESIDENCE_ID);

        app = MyRentApp.getApp();
        portfolio = app.portfolio;
        residence = portfolio.getResidence(resId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,  parent, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_residence, parent, false);

        //ResidenceActivity residenceActivity = (ResidenceActivity)getActivity();
        //residenceActivity.actionBar.setDisplayHomeAsUpEnabled(true);

        addListeners(v);
        updateControls(residence);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        return v;
    }

    private void addListeners(View v)
    {
        geolocation = (EditText) v.findViewById(R.id.geolocation);
        dateButton  = (Button)   v.findViewById(R.id.registration_date);
        rented      = (CheckBox) v.findViewById(R.id.isrented);
        tenantButton = (Button)  v.findViewById(R.id.tenant);
        reportButton = (Button)  v.findViewById(R.id.residence_reportButton);
        cameraButton = (ImageView) v.findViewById(R.id.camera_button);
        photoView = (ImageView) v.findViewById(R.id.myrent_imageView);

        geolocation .addTextChangedListener(this);
        dateButton  .setOnClickListener(this);
        rented      .setOnCheckedChangeListener(this);
        tenantButton.setOnClickListener(this);
        reportButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);

    }

    public void updateControls(Residence residence)
    {
        geolocation.setText(residence.geolocation);
        rented.setChecked(residence.rented);
        dateButton.setText(residence.getDateString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home: navigateUp(getActivity());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        updateResidence(residence);
        super.onPause();
        portfolio.updateResidence(residence);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK)
        {
            return;
        }

        switch (requestCode)
        {
            case REQUEST_CONTACT:
                String name = ContactHelper.getContact(getActivity(), data);
                emailAddress = ContactHelper.getEmail(getActivity(), data);
                tenantButton.setText(name + " : " + emailAddress);
                residence.tenant = name;
                break;
            case REQUEST_PHOTO:
                String filename = data.getStringExtra(ResidenceCameraActivity.EXTRA_PHOTO_FILENAME);
                if (filename != null)
                {
                    residence.photo = filename;
                    showPhoto(getActivity(), residence, photoView );
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {}

    @Override
    public void afterTextChanged(Editable c)
    {
        Log.i(this.getClass().getSimpleName(), "geolocation " + c.toString());
        residence.geolocation = c.toString();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        residence.rented = isChecked;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.registration_date      : Calendar c = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog (getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                dpd.show();
                break;
            case R.id.tenant                 : Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
                if (residence.tenant != null)
                {
                    tenantButton.setText("Tenant: "+residence.tenant);
                }
                break;
            case R.id.residence_reportButton : sendEmail(getActivity(), "emailAddress", getString(R.string.residence_report_subject), residence.getResidenceReport(getActivity()));
                break;

            case R.id.fab:
                startActivityWithData(getActivity(), MapBoxActivity.class, EXTRA_RESIDENCE_ID, residence.id);
                break;
            case R.id.camera_button:
                Intent ic = new Intent(getActivity(), ResidenceCameraActivity.class);
                startActivityForResult(ic, REQUEST_PHOTO);
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
        residence.date = date.getTime();
        dateButton.setText(residence.getDateString());
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onResponse(Response<Residence> response, Retrofit retrofit) {
        Residence returnedResidence = response.body();
        if (returnedResidence != null) {
            Toast.makeText(getActivity(), "Residence updated successfully", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), "Update failed. Residence null returned due to incorrectly configured client", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onFailure(Throwable t) {
        Toast.makeText(getActivity(), "Failed to update residence due to unknown network issue", Toast.LENGTH_SHORT).show();

    }

    public void updateResidence(Residence res) {
        Call<Residence> call = app.residenceService.updateResidence(res);
        call.enqueue(this);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //display thumbnail photo
        showPhoto(getActivity(), residence, photoView);


    }
}