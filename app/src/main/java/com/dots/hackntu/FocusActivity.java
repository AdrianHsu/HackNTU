package com.dots.hackntu;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dots.hackntu.MainApplication;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class FocusActivity extends AppCompatActivity implements View.OnClickListener, MyTimer
  .OnTimeChangeListener, MyTimer.OnSecondChangListener,MyTimer.OnMinChangListener,MyTimer
  .OnHourChangListener {

//  private ProfilePictureView userProfilePictureView;
//  private TextView userNameView;
  public static String userName = "";
  public static Long userId;
  public static String TAG = "Focus";
  static String objectId = "";

  MyTimer timer;
  Button btn_start,btn_stop,btn_reset;
  EditText string;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_focus);
    makeMeRequest();

    // Handle Toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    Log.v(TAG, userName);
    MainApplication.profile = new ProfileDrawerItem().withName(userName).withEmail
    ("adrianhsu1995@gmail" + ".com")
    .withIcon("http://graph.facebook.com/" + userId + "/picture?type=large");

    // Create the AccountHeader
    buildHeader(false, savedInstanceState);
    buildDrawer(toolbar, savedInstanceState);


    timer = (MyTimer) findViewById(R.id.timer);
    timer.setOnTimeChangeListener(this);
    timer.setSecondChangListener(this);
    timer.setMinChangListener(this);
    timer.setHourChangListener(this);
    timer.setModel(Model.Timer);
    timer.setStartTime(1, 30, 30);
    btn_start = (Button) findViewById(R.id.btn_start);
    btn_stop = (Button) findViewById(R.id.btn_stop);
    btn_reset = (Button) findViewById(R.id.btn_reset);
    btn_start.setOnClickListener(this);
    btn_stop.setOnClickListener(this);
    btn_reset.setOnClickListener(this);

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.btn_start:
        timer.start();
        break;
      case R.id.btn_stop:
        timer.stop();
        break;
      case R.id.btn_reset:
        timer.reset();
        break;
    }
  }

  @Override
  public void onTimerStart(long timeStart) {
    Log.e(TAG, "onTimerStart " + timeStart);
    string = (EditText) findViewById(R.id.edit_text);
    putFocusActivity(string.getText().toString());

  }

  public void putFocusActivity(String string) {

    final ParseObject focusActivity = new ParseObject("FocusActivity");
    focusActivity.put("user", ParseUser.getCurrentUser());
    focusActivity.put("focusContent", string);
    focusActivity.put("wasSucceeded", false);
    focusActivity.put("isRunning", true);
    focusActivity.put("stoppedAt", 0);
    focusActivity.put("goalDuration", timer.getTimeStart().getTimeInMillis() - timer.getTimeRemain()
      .getTimeInMillis());
//
//    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//    double longitude = 0;
//    double latitude = 0;
//
//    final LocationListener locationListener = new LocationListener() {
//
//      public void onLocationChanged(Location location) {
//
//      }
//
//      @Override
//      public void onStatusChanged(String provider, int status, Bundle extras) {
//
//      }
//
//      @Override
//      public void onProviderEnabled(String provider) {
//
//      }
//
//      @Override
//      public void onProviderDisabled(String provider) {
//
//      }
//    };
//    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
//    latitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
//    longitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
//    ParseGeoPoint gp = new ParseGeoPoint((int)(latitude * 1e6),(int)(longitude *
//      1e6));
//    focusActivity.put("location", gp);

    focusActivity.saveEventually(new SaveCallback() {
      public void done(ParseException e) {
        if (e == null) {
          // Saved successfully.
          Log.d(TAG, "User update saved!");
          objectId = focusActivity.getObjectId();
          Log.d(TAG, "The object id is: " + objectId);
        } else {
          // The save failed.
          Log.d(TAG, "User update error: " + e);
        }
      }
    });
  }

  @Override
  public void onTimeChange(long timeStart, long timeRemain) {
    Log.e(TAG, "onTimeChange timeStart " + timeStart);
    Log.e(TAG, "onTimeChange timeRemain " + timeRemain);
  }

  @Override
  public void onTimeStop(long timeStart, long timeRemain) {
    Log.e(TAG,"onTimeStop timeRemain "+timeStart);
    Log.e(TAG, "onTimeStop timeRemain " + timeRemain);

    ParseObject focusActivity = ParseObject.createWithoutData("FocusActivity", objectId);
// Set a new value on quantity
    focusActivity.put("wasSucceeded", false);
    focusActivity.put("isRunning", false);
    focusActivity.put("stoppedAt", System.currentTimeMillis());
    focusActivity.saveEventually();
  }
  public static void onTimeDone(Calendar timeStart, Calendar timeRemain) {
    ParseObject focusActivity = ParseObject.createWithoutData("FocusActivity", objectId);
// Set a new value on quantity
    focusActivity.put("wasSucceeded", true);
    focusActivity.put("isRunning", false);
    focusActivity.put("stoppedAt", System.currentTimeMillis());
    focusActivity.saveEventually();
  }

  @Override
  public void onSecondChange(int second) {
    Log.e(TAG, "second change to " + second);
  }

  @Override
  public void onHourChange(int hour) {
    Log.e(TAG, "hour change to " + hour);
  }

  @Override
  public void onMinChange(int minute) {
    Log.e(TAG, "minute change to "+minute);
  }

  private void buildDrawer(Toolbar toolbar, Bundle savedInstanceState) {

    //Create the drawer
    MainApplication.result = new DrawerBuilder()
    .withActivity(this)
    .withToolbar(toolbar)
      .withAccountHeader(MainApplication.headerResult) //set the AccountHeader we created earlier
        // for the header
      .addDrawerItems(
        new PrimaryDrawerItem().withName(R.string.drawer_item_focus).withIcon(FontAwesome.Icon
          .faw_check_circle_o).withIdentifier(0),
        new PrimaryDrawerItem().withName(R.string.drawer_item_timeline).withIcon(FontAwesome.Icon
          .faw_clock_o).withIdentifier(1),
        new PrimaryDrawerItem().withName(R.string.drawer_item_logout).withIcon(FontAwesome.Icon
          .faw_sign_out).withIdentifier(2)
        )
      .addStickyDrawerItems(
        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome
          .Icon.faw_cog).withIdentifier(7)
        )
      .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
          //check if the drawerItem is set.
          //there are different reasons for the drawerItem to be null
          //--> click on the header
          //--> click on the footer
          //those items don't contain a drawerItem

          if (drawerItem != null) {
            Intent intent = null;
            if (drawerItem.getIdentifier() == 0) {
//              intent = new Intent(FocusActivity.this, FocusActivity.class);
            } else if (drawerItem.getIdentifier() == 1) {
              intent = new Intent(FocusActivity.this, TimelineActivity.class);
            } else if (drawerItem.getIdentifier() == 2) {
              ParseUser.logOut();
              intent = new Intent(FocusActivity.this, LoginActivity.class);
            } else if (drawerItem.getIdentifier() == 7) {
//              do nothing
            }
            if (intent != null) {
              FocusActivity.this.startActivity(intent);
            }
          }


          return false;
        }
      })
.withAnimateDrawerItems(true)
.withSavedInstance(savedInstanceState)
.withSelectedItem(0)
.build();

}

public static void makeMeRequest() {
  GraphRequest request = GraphRequest.newMeRequest(
    AccessToken.getCurrentAccessToken(),
    new GraphRequest.GraphJSONObjectCallback() {
      @Override
      public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

        if (jsonObject != null) {
          JSONObject userProfile = new JSONObject();
          try {

            userId =  jsonObject.getLong("id");
            userName = jsonObject.getString("name");
            userProfile.put("facebookId", jsonObject.getLong("id"));
            userProfile.put("name", jsonObject.getString("name"));
              // userProfile.put("gender", jsonObject.getString("gender"));
              // userProfile.put("email", jsonObject.getString("email"));

              // Save the user profile info in a user property
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.put("profile", userProfile);
              // Link installationId with currentUser
            currentUser.put("installationId",
              ParseInstallation.getCurrentInstallation().get("installationId"));
              //store facebookId exclusively
            JSONObject profile = currentUser.getJSONObject("profile");
            try {
              currentUser.put("facebookId", profile.get("facebookId"));
            } catch (JSONException e) {
              e.printStackTrace();
            }
            currentUser.saveInBackground();

              // Show user info
            updateViewsWithProfileInfo();
          } catch (JSONException e) {
            Log.d(TAG,
              "Error parsing returned user data. " + e);
          }
        } else if (graphResponse.getError() != null) {
          switch (graphResponse.getError().getCategory()) {
            case LOGIN_RECOVERABLE:
            Log.d(TAG,
              "Authentication error: " + graphResponse.getError());
            break;

            case TRANSIENT:
            Log.d(TAG,
              "Transient error. Try again. " + graphResponse.getError());
            break;

            case OTHER:
            Log.d(TAG,
              "Some other error: " + graphResponse.getError());
            break;
          }
        }
      }
    });

request.executeAsync();
}

private static void updateViewsWithProfileInfo() {
  ParseUser currentUser = ParseUser.getCurrentUser();
  if (currentUser.has("profile")) {
    JSONObject userProfile = currentUser.getJSONObject("profile");
    try {

      if (userProfile.has("facebookId")) {
//          userProfilePictureView.setProfileId(userProfile.getString("facebookId"));
        userId = userProfile.getLong("id");
      } else {
          // Show the default, blank user profile picture
//          userProfilePictureView.setProfileId(null);
      }
      if (userProfile.has("name")) {
//          userNameView.setText(userProfile.getString("name"));
        userName = userProfile.getString("name");
      } else {
//          userNameView.setText("");
        userName = "";
      }
        // if (userProfile.has("gender")) {
        //   userGenderView.setText(userProfile.getString("gender"));
        // } else {
        //   userGenderView.setText("");
        // }
        // if (userProfile.has("email")) {
        //   userEmailView.setText(userProfile.getString("email"));
        // } else {
        //   userEmailView.setText("");
        // }
    } catch (JSONException e) {
      Log.d(TAG, "Error parsing saved user data.");
    }
  }
}

  /**
   * small helper method to reuse the logic to build the AccountHeader
   * this will be used to replace the header of the drawer with a compact/normal header
   *
   * @param compact
   * @param savedInstanceState
   */
  private void buildHeader(boolean compact, Bundle savedInstanceState) {
    // Create the AccountHeader
    MainApplication.headerResult = new AccountHeaderBuilder()
    .withActivity(this)
    .withHeaderBackground(R.drawable.header)
    .withCompactStyle(compact)
    .addProfiles(
      MainApplication.profile
      )
    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
      @Override
      public boolean onProfileChanged(View view, IProfile profile, boolean current) {

          //false if you have not consumed the event and it should close the drawer
        return false;
      }
    })
    .withSavedInstance(savedInstanceState)
    .build();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    //add the values which need to be saved from the drawer to the bundle
    outState = MainApplication.result.saveInstanceState(outState);
    //add the values which need to be saved from the accountHeader to the bundle
    outState = MainApplication.headerResult.saveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onResume() {
    super.onResume();

    ParseUser currentUser = ParseUser.getCurrentUser();
    if (currentUser != null) {
      // Check if the user is currently logged
      // and show any cached content
      updateViewsWithProfileInfo();
    } else {
      // If the user is not logged in, go to the
      // activity showing the login view.
      Intent intent = new Intent(FocusActivity.this, LoginActivity.class);
      FocusActivity.this.startActivity(intent);
    }
  }

  @Override
  public void onBackPressed() {
    //handle the back press :D close the drawer first and if the drawer is closed close the activity
    if (MainApplication.result != null && MainApplication.result.isDrawerOpen()) {
      MainApplication.result.closeDrawer();
    } else {
//      super.onBackPressed();
      Intent a = new Intent(Intent.ACTION_MAIN);
      a.addCategory(Intent.CATEGORY_HOME);
      a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(a);
    }
  }
}