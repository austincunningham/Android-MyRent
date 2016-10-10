package org.wit.myrent.activities;

/**
 * Created by ictskills on 10/10/16.
 */
 import android.os.Bundle;
 import android.support.v4.view.PagerAdapter;
 import android.support.v4.view.ViewPager;
 import android.support.v7.app.AppCompatActivity;
 import android.support.v4.view.ViewPager;

 import org.wit.myrent.R;
 import org.wit.myrent.app.MyRentApp;
 import org.wit.myrent.models.Portfolio;
 import org.wit.myrent.models.Residence;

 import android.support.v4.app.Fragment;
 import android.support.v4.app.FragmentManager;
 import android.support.v4.app.FragmentStatePagerAdapter;



 import java.util.ArrayList;

public class ResidencePagerActivity extends AppCompatActivity
{
    private ViewPager viewPager;
    private ArrayList<Residence> residences;
    private Portfolio portfolio;
    private PagerAdapter pagerAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        viewPager = new ViewPager(this);
        viewPager.setId(R.id.viewPager);
        setContentView(viewPager);

        setResidenceList();
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), residences);
        viewPager.setAdapter(pagerAdapter);

    }

    private void setResidenceList()
    {
        MyRentApp app = (MyRentApp) getApplication();
        portfolio = app.portfolio;
        residences = portfolio.residences;
    }

    class PagerAdapter extends FragmentStatePagerAdapter
    {
        private ArrayList<Residence>  residences;

        public PagerAdapter(FragmentManager fm, ArrayList<Residence> residences)
        {
            super(fm);
            this.residences = residences;
        }

        @Override
        public int getCount()
        {
            return residences.size();
        }

        @Override
        public Fragment getItem(int pos)
        {
            Residence residence = residences.get(pos);
            Bundle args = new Bundle();
            args.putSerializable(ResidenceFragment.EXTRA_RESIDENCE_ID, residence.id);
            ResidenceFragment fragment = new ResidenceFragment();
            fragment.setArguments(args);
            return fragment;
        }
    }


}