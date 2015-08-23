package com.dots.hackntu;


import android.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.baoyz.widget.PullRefreshLayout;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

public class TimelineActivity extends ActionBarActivity {

  public static PullRefreshLayout layout;
  boolean init = false;

  public static ArrayList<Card> cards = new ArrayList<Card>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_timeline);
    // Handle Toolbar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    MainApplication.profile = new ProfileDrawerItem().withName(FocusActivity.userName).withEmail
      ("adrianhsu1995@gmail" + ".com")
      .withIcon("http://graph.facebook.com/" + FocusActivity.userId + "/picture?type=large");


    if (savedInstanceState == null) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      RecyclerViewFragment fragment = new RecyclerViewFragment();
      transaction.replace(R.id.sample_content_fragment, fragment);
      transaction.commit();
    }

    layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        layout.postDelayed(new Runnable() {
          @Override
          public void run() {
//              updateAdapter(cards);
            layout.setRefreshing(false);
            RecyclerViewFragment.mAdapter.notifyDataSetChanged();
          }
        }, 2000);
      }
    });
    layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);

    if (init == false) {
      initCard();
      init = true;
    }

    // Create the AccountHeader
    buildHeader(false, savedInstanceState);
    buildDrawer(toolbar, savedInstanceState);
  }

  public void initCard() {
    for (int i = 0; i < 60; i++) {
      //Create a Card
      Card card = new Card(this);
      card.setTitle("this is card #" + i);
      //Create a CardHeader
      CardHeader header = new CardHeader(this);
      header.setTitle("Adrian's card");
      header.setButtonExpandVisible(true);

      //Add Header to card
      card.addCardHeader(header);

      //This provides a simple (and useless) expand area
      CardExpand expand = new CardExpand(this);
      //Set inner title in Expand Area
      expand.setTitle("Expand Area test");
      card.addCardExpand(expand);
      card.setExpanded(false);

      CardThumbnail thumb = new CardThumbnail(this);
//      thumb.setDrawableResource(R.drawable.adrian);
      card.addCardThumbnail(thumb);

      card.setClickable(true);
      //Add ClickListener
      card.setOnClickListener(new Card.OnCardClickListener() {

        @Override
        public void onClick(Card card, View view) {
//            Toast.makeText(MainActivity.this, "Click Listener card=" + card.getTitle(), Toast
//              .LENGTH_SHORT)
//              .show();
        }
      });
      cards.add(card);
    }
  }

  /**
   * Update the adapter
   */
  public void updateAdapter(ArrayList<Card> cards) {
    if (cards != null) {
//        RecyclerViewFragment.mAdapter.addAll(cards);
    }
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
        new PrimaryDrawerItem().withName(R.string.drawer_item_map).withIcon(FontAwesome.Icon
          .faw_map_marker).withIdentifier(2),
        new PrimaryDrawerItem().withName(R.string.drawer_item_logout).withIcon(FontAwesome.Icon
          .faw_sign_out).withIdentifier(3)
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
              intent = new Intent(TimelineActivity.this, MainActivity.class);
            } else if (drawerItem.getIdentifier() == 3) {
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
