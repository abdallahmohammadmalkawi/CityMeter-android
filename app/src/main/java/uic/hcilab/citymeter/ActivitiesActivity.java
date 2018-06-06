package uic.hcilab.citymeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ActivitiesActivity extends TabHost {

    @Override
    public int getContentViewId() {
        return R.layout.activity_activities;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_activities;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("My Activities");
    }
}
