package com.example.krrishdholakia.martaview9;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.R.id.list;

public class MainActivity extends AppCompatActivity {

    private TextView mStateText;
    private TextView mOffsetText;
    private BottomSheetBehavior mBottomSheetBehavior;
    private GoogleMap map;
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mOffsetText = (TextView) findViewById(R.id.offsetText);
        mStateText = (TextView) findViewById(R.id.stateText);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setAutoMeasureEnabled(true);
        ApplicationAdapter adapter = new ApplicationAdapter(this, listApplications(this));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        FrameLayout parentThatHasBottomSheetBehavior = (FrameLayout) recyclerView.getParent().getParent();
        mBottomSheetBehavior = BottomSheetBehavior.from(parentThatHasBottomSheetBehavior);
        if (mBottomSheetBehavior != null) {
            setStateText(mBottomSheetBehavior.getState());
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    setStateText(newState);
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    setOffsetText(slideOffset);
                }
            });
        }

        View peekButton = findViewById(R.id.peek_me);
        peekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Let's peek it, programmatically
                View peakView = findViewById(R.id.drag_me);
                mBottomSheetBehavior.setPeekHeight(peakView.getHeight());
                peakView.requestLayout();
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    private void setOffsetText(float slideOffset) {
        mOffsetText.setText(getString(R.string.offset, slideOffset));
    }

    private void setStateText(int newState) {
        mStateText.setText(getStateAsString(newState));
    }

    public static int getStateAsString(int newState) {
        switch (newState) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                return R.string.collapsed;
            case BottomSheetBehavior.STATE_DRAGGING:
                return R.string.dragging;
            case BottomSheetBehavior.STATE_EXPANDED:
                return R.string.expanded;
            case BottomSheetBehavior.STATE_HIDDEN:
                return R.string.hidden;
            case BottomSheetBehavior.STATE_SETTLING:
                return R.string.settling;
        }
        return R.string.undefined;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bottom_sheet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static List<ApplicationInfo> listApplications(Context context) {
        int flags = PackageManager.GET_META_DATA;
        List<ApplicationInfo> installedApps = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
        for (ApplicationInfo appInfo : applications) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                installedApps.add(appInfo);
            }
        }
        return installedApps;
    }
}