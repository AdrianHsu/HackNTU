package com.dots.hackntu;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

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
import com.parse.ParseUser;


public class TimelineActivity extends ActionBarActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_timeline);
    // Handle Toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    MainApplication.profile = new ProfileDrawerItem().withName(FocusActivity.userName).withEmail
      ("adrianhsu1995@gmail" + ".com")
      .withIcon("http://graph.facebook.com/"+ FocusActivity.userId +"/picture?type=large");

    // Create the AccountHeader
    buildHeader(false, savedInstanceState);
    buildDrawer(toolbar, savedInstanceState);
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
              intent = new Intent(TimelineActivity.this, FocusActivity.class);
            } else if (drawerItem.getIdentifier() == 1) {
//              intent = new Intent(TimelineActivity.this, TimelineActivity.class);
            } else if (drawerItem.getIdentifier() == 2) {
//              intent = new Intent(TimelineActivity.this, LoginActivity.class);
              ParseUser.logOut();
              intent = new Intent(TimelineActivity.this, LoginActivity.class);
            } else if (drawerItem.getIdentifier() == 7) {

            }
            if (intent != null) {
              TimelineActivity.this.startActivity(intent);
            }
          }


          return false;
        }
      })
      .withAnimateDrawerItems(true)
      .withSavedInstance(savedInstanceState)
      .withSelectedItem(1)
      .build();
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
