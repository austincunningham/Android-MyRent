package org.wit.myrent.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.text.Editable;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;

import org.wit.myrent.R;
import org.wit.myrent.app.MyRentApp;
import org.wit.myrent.models.Portfolio;
import org.wit.myrent.models.Residence;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.DatePickerDialog;
import android.view.View;
import android.view.View.OnClickListener;

import static org.wit.android.helpers.IntentHelper.navigateUp;

public class ResidenceActivity extends AppCompatActivity implements TextWatcher, OnCheckedChangeListener, View.OnClickListener, DatePickerDialog.OnDateSetListener
{

    private EditText geolocation;
    private Residence residence;
    private CheckBox rented;
    private Button dateButton;
    private Portfolio portfolio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_residence);

        geolocation = (EditText) findViewById(R.id.geolocation);
        dateButton = (Button) findViewById(R.id.registration_date);
        rented = (CheckBox) findViewById(R.id.isrented);

        residence = new Residence();
        //dateButton.setEnabled(false);
        dateButton.setOnClickListener(this);

        rented.setOnCheckedChangeListener(this);
        geolocation.addTextChangedListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MyRentApp app = (MyRentApp) getApplication();
        portfolio = app.portfolio;
        Long resId = (Long) getIntent().getExtras().getSerializable("RESIDENCE_ID");
        residence = portfolio.getResidence(resId);
        if (residence != null)  {
            updateControls(residence);
        }

    }

    public void updateControls(Residence residence)
    {
        geolocation.setText(residence.geolocation);
        rented.setOnCheckedChangeListener(this);
        dateButton.setText(residence.getDateString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.myrent, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
//        int id = item.getItemId();
//        if(id == R.id.action_settings){
//            return true;
//        }
        switch (item.getItemId()){
            case android.R.id.home: navigateUp(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.i(this.getClass().getSimpleName(), "character Input");
        residence.setGeolocation(editable.toString());
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Log.i(this.getClass().getSimpleName(), "rented Checked");
        residence.rented = isChecked;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.registration_date      : Calendar c = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog (this, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                dpd.show();
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
    public void onPause()
    {
        super.onPause();
        portfolio.saveResidences();
    }
}
