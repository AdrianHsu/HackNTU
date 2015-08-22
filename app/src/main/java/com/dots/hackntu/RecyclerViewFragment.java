/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.dots.hackntu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;


/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class RecyclerViewFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";

//    protected RecyclerView mRecyclerView;
    public static CardRecyclerView mRecyclerView;
  //    protected CustomAdapter mAdapter;
    public static CardArrayRecyclerViewAdapter mAdapter;
    protected CardRecyclerView.LayoutManager mLayoutManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (CardRecyclerView) rootView.findViewById(R.id.card_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());

        setRecyclerViewLayoutManager();

        mAdapter = new CardArrayRecyclerViewAdapter(getActivity(), TimelineActivity.cards);
        // Set CustomAdapter as the adapter for RecyclerView.
        // END_INCLUDE(initializeRecyclerView)

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Set the empty view
        if (mRecyclerView != null) {
          mRecyclerView.setAdapter(mAdapter);
        }

        return rootView;
    }

//    /**
//     * Set RecyclerView's LayoutManager to the one given.
//     *
//     * @param layoutManagerType Type of layout manager to switch to.
//     */
    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager((getActivity()));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
//      http://stackoverflow.com/questions/27362806/android-swiperefreshlayout-framelayoutgit
        mRecyclerView.addOnScrollListener( new CardRecyclerView.OnScrollListener() {

          @Override
          public void onScrollStateChanged(RecyclerView cardRecyclerView, int i) {}

          @Override
          public void onScrolled(RecyclerView cardRecyclerView, int i, int i2) {
            int topRowVerticalPosition =
              (mRecyclerView == null || mRecyclerView.getChildCount() == 0) ?
                0 : mRecyclerView.getChildAt(0).getTop();
            TimelineActivity.layout.setEnabled(topRowVerticalPosition >= 0);
          }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      // Save currently selected layout manager.
      super.onSaveInstanceState(savedInstanceState);
    }

}
