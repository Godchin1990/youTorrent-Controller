package com.lgallardo.youtorrentcontroller;

import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.robotium.solo.Solo;

import java.util.ArrayList;

/**
 * Created by lgallard on 07/07/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {


    private MainActivity mActivity;
    private RecyclerView mLeftDrawer;
    private Solo mSolo;
    private ArrayList<ListView> mListViews;
    int mLeftDrawerIndex;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        // Set off the	touch mode in the device (to avoid ignoring key	events)
        setActivityInitialTouchMode(false);

        mActivity = getActivity();
        mLeftDrawer = (RecyclerView) mActivity.findViewById(R.id.RecyclerView);


        //	Initiate	the	instance	of	Solo
        mSolo = new Solo(getInstrumentation(), getActivity());


        // Get All ListViews
        mListViews = mSolo.getCurrentViews(ListView.class);

        // Define index for left_drawer list view
        mLeftDrawerIndex = 0;

        // Get drawer list view index
        for (int i = 0; i < mListViews.size(); i++) {
            if (mListViews.get(i).getId() == R.id.RecyclerView) {
                mLeftDrawerIndex = i;
                break;
            }
        }


    }

    // Test the All list
    public void testAllListClicked() throws Exception {

        // Click All
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(2, mLeftDrawerIndex);
        mSolo.sleep(1000);

        assertEquals("All torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[0],
                mActivity.getSupportActionBar().getTitle());

    }

    // Test the Download list
    public void testDownloadListClicked() {

        // Click Download
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(3, mLeftDrawerIndex);
        mSolo.sleep(1000);

        assertEquals("Download torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[1],
                mActivity.getSupportActionBar().getTitle());

    }

    // Test the Completed list
    public void testCompletedListClicked() {

        // Click Completed
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(4, mLeftDrawerIndex);

        mSolo.sleep(1000);

        assertEquals("Completed torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[2],
                mActivity.getSupportActionBar().getTitle());

    }


    // Test the Paused list
    public void testPausedListClicked() {

        // Click Paused
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(5, mLeftDrawerIndex);
        mSolo.sleep(1000);

        assertEquals("Completed torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[3],
                mActivity.getSupportActionBar().getTitle());

    }


    // Test the Active list
    public void testActiveListClicked() {


        // Click Active
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(6, mLeftDrawerIndex);
        mSolo.sleep(1000);

        assertEquals("Completed torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[4],
                mActivity.getSupportActionBar().getTitle());

    }


    // Test the Inactive list
    public void testInactiveListClicked() {


        // Click Inactive
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(7, mLeftDrawerIndex);
        mSolo.sleep(1000);

        assertEquals("Completed torrent list not loaded",
                mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[5],
                mActivity.getSupportActionBar().getTitle());

    }


    // Test if the Get Pro item appears in the listview
    public void testGetPRO() {


        if (mActivity.packageName.equals("com.lgallardo.qbittorrentclient")) {


            assertEquals("Get PRO not in menu drawer",
                    mActivity.getResources().getStringArray(R.array.navigation_drawer_items_array)[8],
                    ((TextView) mLeftDrawer.getChildAt(9)).getText().toString());

        }
    }

    // Test if RSS Activity is launched
    public void testRSSLaunched() {

        mSolo.clickOnMenuItem(mSolo.getString(R.string.action_rss));
        mSolo.assertCurrentActivity("Can't open RSS Feed activity", RSSFeedActivity.class);

    }

    // Test if Options is launched
    public void testOptionsLaunched() {

        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(8, mLeftDrawerIndex);
        mSolo.assertCurrentActivity("Can't open Options activity", OptionsActivity.class);

    }


    // Test if Settings is launched
    public void testSettingsLaunched() {

        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(9, mLeftDrawerIndex);
        mSolo.assertCurrentActivity("Can't open Settings activity", SettingsActivity.class);

    }


    // Test if Help is launched
    public void testHelpLaunched() {

        mSolo.clickOnActionBarHomeButton();

        if (mActivity.packageName.equals("com.lgallardo.qbittorrentclientpro")) {
            mSolo.clickInRecyclerView(10, mLeftDrawerIndex);
        } else {
            mSolo.clickInRecyclerView(11, mLeftDrawerIndex);
        }

        mSolo.assertCurrentActivity("Can't open Help activity", HelpActivity.class);

    }

    // Test Add torrent
    public void test1AddTorrent() {


        mSolo.clickOnMenuItem(mSolo.getString(R.string.action_add));

        // wait to dialog to pop up
        getInstrumentation().waitForIdleSync();

        EditText url = (EditText) mSolo.getView(R.id.url);

        // http://cdimage.debian.org/debian-cd/current/amd64/bt-cd/debian-8.2.0-amd64-CD-1.iso.torrent
        mSolo.enterText(url, "http://cdimage.debian.org/debian-cd/current/amd64/bt-cd/debian-8.2.0-amd64-CD-1.iso.torrent");
        mSolo.clickOnButton(mSolo.getString(R.string.ok));

        assertTrue("Torrent not sent", mSolo.waitForText(mSolo.getString(R.string.torrentAdded)));


        // Check is on the Downloading list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(3, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();

        mSolo.sleep(3000);

        // Get item with text "debian"
        assertNotNull("Torrent not on Downloading list", mSolo.getText("debian"));


    }

    // Test Pause torrent
    public void test1PauseTorrentFromCAB() {

        // Add torrent first
        mActivity.addTorrent("http://cdimage.debian.org/debian-cd/current/amd64/bt-cd/debian-8.2.0-amd64-CD-1.iso.torrent");
        getInstrumentation().waitForIdleSync();

        // Long click on pre-added torrent
        mSolo.clickLongOnText("debian");
        getInstrumentation().waitForIdleSync();

        // Click pause button on Action menu
        mSolo.clickOnView(getActivity().findViewById(R.id.action_pause));

        // Check is on the Paused list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(5, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();


        // Get item with text "debian"
        assertNotNull("Torrent not on Paused list", mSolo.getText("debian"));


    }


    // Test Resume torrent
    public void test1ResumeTorrentFromCAB() {

        // Add torrent first
        mActivity.addTorrent("http://cdimage.debian.org/debian-cd/current/amd64/bt-cd/debian-8.2.0-amd64-CD-1.iso.torrent");
        getInstrumentation().waitForIdleSync();

        // Long click on pre-added torrent
        mSolo.clickLongOnText("debian");
        getInstrumentation().waitForIdleSync();

        // Click pause button on Action menu
        mSolo.clickOnView(getActivity().findViewById(R.id.action_resume));

        // Check is on the All list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(3, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();

        // Get item with text "debian"
        assertNotNull("Torrent not on Downloading list", mSolo.getText("debian"));


    }


    // Test Delete torrent
    public void test2DeleteTorrentFromCAB() {

        // Add torrent first
        mActivity.addTorrent("http://cdimage.debian.org/debian-cd/current/amd64/bt-cd/debian-8.2.0-amd64-CD-1.iso.torrent");
        getInstrumentation().waitForIdleSync();

        // Long click on pre-added torrent
        mSolo.clickLongOnText("debian");
        getInstrumentation().waitForIdleSync();

        // Click pause button on Action menu
        mSolo.clickOnView(getActivity().findViewById(R.id.action_delete));

        // wait to dialog to pop up
        getInstrumentation().waitForIdleSync();

        // Confirm Delete
        mSolo.clickOnButton(mSolo.getString(R.string.ok));
        getInstrumentation().waitForIdleSync();

        // Check is on the All list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(2, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();

        // Get item with text "debian"
        assertFalse("Torrent not Deleted", mSolo.searchText("debian"));

    }

    // Test Delete with data torrent
    public void test2DeleteTorrentWithDataFromCAB() {

        // Add torrent first
        mActivity.addTorrent("http://cdimage.debian.org/debian-cd/current/amd64/bt-cd/debian-8.2.0-amd64-CD-1.iso.torrent");
        getInstrumentation().waitForIdleSync();

        // Long click on pre-added torrent
        mSolo.clickLongOnText("debian");
        getInstrumentation().waitForIdleSync();

        // Click pause button on Action menu
        mSolo.clickOnView(getActivity().findViewById(R.id.action_delete_drive));

        // wait to dialog to pop up
        getInstrumentation().waitForIdleSync();

        // Confirm Delete
        mSolo.clickOnButton(mSolo.getString(R.string.ok));
        getInstrumentation().waitForIdleSync();

        // Check is on the All list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(2, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();

        // Try to get item with text "debian"
        assertFalse("Torrent not Deleted with data", mSolo.searchText("debian"));

    }

    // Test Pause torrent
    public void test1PauseTorrentFromDetails() {

        // Add torrent first
        mActivity.addTorrent("http://cdimage.debian.org/debian-cd/current/amd64/bt-cd/debian-8.2.0-amd64-CD-1.iso.torrent");
        getInstrumentation().waitForIdleSync();

        // Open All list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(2, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();


        // Long click on pre-added torrent
        mSolo.clickOnText("debian");
        mSolo.waitForText(mSolo.getString(R.string.torrent_details_properties));

        // Click pause button on Action menu
//        mSolo = new Solo(getInstrumentation(), getActivity());
        mSolo.clickOnView(getActivity().findViewById(R.id.action_pause));

        // Check is on the Paused list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(5, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();

        // Get item with text "debian"
        assertNotNull("Torrent not on Paused list", mSolo.getText("debian"));


    }

    // Test Resume torrent
    public void test1ResumeTorrentFromDetails() {

        // Add torrent first
        mActivity.addTorrent("http://cdimage.debian.org/debian-cd/current/amd64/bt-cd/debian-8.2.0-amd64-CD-1.iso.torrent");
        getInstrumentation().waitForIdleSync();
//        test1AddTorrent();

        // Open All list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(2, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();


        // Long click on pre-added torrent
        mSolo.clickOnText("debian");
        mSolo.waitForText(mSolo.getString(R.string.torrent_details_properties));

        // Click pause button on Action menu
//        mSolo = new Solo(getInstrumentation(), getActivity());
        mSolo.clickOnView(getActivity().findViewById(R.id.action_resume));

        // Check is on the Downloading list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(3, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();

        // Get item with text "debian"
        assertNotNull("Torrent not on Downloading list", mSolo.getText("debian"));

    }

    // Test Resume torrent
    public void test2DeleteTorrentFromFromDetails() {

        // Add torrent first
        mActivity.addTorrent("http://cdimage.debian.org/debian-cd/current/amd64/bt-cd/debian-8.2.0-amd64-CD-1.iso.torrent");
        getInstrumentation().waitForIdleSync();


        // Open All list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(2, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();


        // Long click on pre-added torrent
        mSolo.clickOnText("debian");
        mSolo.waitForText(mSolo.getString(R.string.torrent_details_properties));

        // Click pause button on Action menu
        mSolo.clickOnView(getActivity().findViewById(R.id.action_delete));

        // wait to dialog to pop up
        getInstrumentation().waitForIdleSync();

        // Confirm Delete
        mSolo.clickOnButton(mSolo.getString(R.string.ok));
        getInstrumentation().waitForIdleSync();

        // Check is on the All list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(2, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();

        // Try to get item with text "debian"
        assertFalse("Torrent not Deleted with data", mSolo.searchText("debian"));

    }

    // Test Resume torrent
    public void test2DeleteTorrentWithDataFromFromDetails() {

        // Add torrent first
        mActivity.addTorrent("http://cdimage.debian.org/debian-cd/current/amd64/bt-cd/debian-8.2.0-amd64-CD-1.iso.torrent");
        getInstrumentation().waitForIdleSync();


        // Open All list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(2, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();


        // Long click on pre-added torrent
        mSolo.clickOnText("debian");
        mSolo.waitForText(mSolo.getString(R.string.torrent_details_properties));

        // Click pause button on Action menu
        mSolo.clickOnView(getActivity().findViewById(R.id.action_delete_drive));

        // wait to dialog to pop up
        getInstrumentation().waitForIdleSync();

        // Confirm Delete
        mSolo.clickOnButton(mSolo.getString(R.string.ok));
        getInstrumentation().waitForIdleSync();

        // Check is on the All list
        mSolo.clickOnActionBarHomeButton();
        mSolo.clickInRecyclerView(2, mLeftDrawerIndex);
        getInstrumentation().waitForIdleSync();

        // Try to get item with text "debian"
        assertFalse("Torrent not Deleted with data", mSolo.searchText("debian"));

    }

    // Check if Ads were loaded in MainActivity
    public void test1AdsUnitId() {

        if (mActivity.packageName.equals("com.lgallardo.qbittorrentclient")) {

            getInstrumentation().waitForIdleSync();

            // Get All Adviews
            ArrayList<AdView> mAdviews = mSolo.getCurrentViews(AdView.class);


            // Check ad unit id            // Check ad unit id
            assertTrue("Ads not loaded", mAdviews.size() > 0);
            assertNotNull("Ads not loaded", mAdviews.get(0).getAdUnitId());
            assertEquals("Ads not loaded", "ca-app-pub-1035265933040074/9260093694", mAdviews.get(0).getAdUnitId());

        }

    }


    // Check if Ads were loaded in RSS feed
    public void test1RSSFeedAdsUnitId() {
        mSolo.clickOnMenuItem(mSolo.getString(R.string.action_rss));

        if (mActivity.packageName.equals("com.lgallardo.qbittorrentclient")) {

            getInstrumentation().waitForIdleSync();

            // Get All Adviews
            ArrayList<AdView> mAdviews = mSolo.getCurrentViews(AdView.class);

            // Check ad unit id
            assertTrue("Ads not loaded in RSS Feed", mAdviews.size() > 0);
            assertNotNull("Ads not loaded in RSS Feed", mAdviews.get(0).getAdUnitId());
            assertEquals("Ads not loaded in RSS Feed", R.id.adViewRssFeed, mAdviews.get(0).getId());
            assertEquals("Ads not loaded in RSS Feed", "ca-app-pub-1035265933040074/9260093694", mAdviews.get(0).getAdUnitId());
        }
    }

    // Check if Ads were loaded in RSS item list
    public void test1RSSItemAdsUnitId() {
        mSolo.clickOnMenuItem(mSolo.getString(R.string.action_rss));

        if (mActivity.packageName.equals("com.lgallardo.qbittorrentclient")) {

            getInstrumentation().waitForIdleSync();

            mSolo.clickInRecyclerView(2, mLeftDrawerIndex);

            mSolo.sleep(3000);

            // Get All Adviews
            ArrayList<AdView> mAdviews = mSolo.getCurrentViews(AdView.class);

            // Check ad unit id
            assertTrue("Ads not loaded in RSS Item list", mAdviews.size() > 0);
            assertNotNull("Ads not loaded in RSS Item list", mAdviews.get(0).getAdUnitId());
            assertEquals("Ads not loaded in RSS Item list", R.id.adViewRssItem, mAdviews.get(0).getId());
            assertEquals("Ads not loaded in RSS Item list", "ca-app-pub-1035265933040074/9260093694", mAdviews.get(0).getAdUnitId());

        }
    }

}
