/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Luis M. Gallardo D. - initial implementation
 ******************************************************************************/
package com.lgallardo.youtorrentcontroller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

public class TorrentDetailsFragment extends Fragment {

    // TAGS
    protected static final String TAG_SAVE_PATH = "save_path";
    protected static final String TAG_CREATION_DATE = "creation_date";
    protected static final String TAG_COMMENT = "comment";
    protected static final String TAG_TOTAL_WASTED = "total_wasted";
    protected static final String TAG_TOTAL_UPLOADED = "total_uploaded";
    protected static final String TAG_TOTAL_DOWNLOADED = "total_downloaded";
    protected static final String TAG_TIME_ELAPSED = "time_elapsed";
    protected static final String TAG_NB_CONNECTIONS = "nb_connections";
    protected static final String TAG_SHARE_RATIO = "share_ratio";
    protected static final String TAG_UPLOAD_LIMIT = "up_limit";
    protected static final String TAG_DOWNLOAD_LIMIT = "dl_limit";

    static ContentFile[] files;
    static Tracker[] trackers;
    static String[] names, trackerNames;

    // Torrent variables
    String name, info, hash, ratio, size, progress, state, priority, savePath, creationDate, comment, totalWasted, totalUploaded,
            totalDownloaded, timeElapsed, nbConnections, shareRatio, uploadRateLimit, downloadRateLimit, downloaded, eta, downloadSpeed, uploadSpeed,
            percentage = "";

    int  peersConnected, peersInSwarm, seedsConnected, seedsInSwarm;
    static String hashToUpdate;

    String url;
    int position;
    JSONObject json2;

    // Adapters
    myFileAdapter fileAdpater;
    myTrackerAdapter trackerAdapter;
    myPropertyAdapter propertyAdapter;

    private String qbQueryString = "query";
    private Torrent torrent;
    public static SwipeRefreshLayout mSwipeRefreshLayout;
    private RefreshListener refreshListener;

    // AdView for ads
    private AdView adView;

    public TorrentDetailsFragment() {
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Tell the host activity that your fragment has menu options that it
        // wants to add/replace/delete using the onCreateOptionsMenu method.
        setHasOptionsMenu(true);

        View rootView;

        rootView = inflater.inflate(R.layout.torrent_details, container, false);

        // Get Refresh Listener
        refreshListener = (RefreshListener) getActivity();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.details_refresh_layout);

        if(mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshListener.swipeRefresh();
                }
            });
        }

        // Hide herderInfo and title in phone's view
        if (getActivity().findViewById(R.id.one_frame) != null && MainActivity.headerInfo != null) {
            MainActivity.headerInfo.setVisibility(View.GONE);
//            ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

            ((MainActivity) getActivity()).setTitle("");
        }

        savePath = "";
        creationDate = "";
        comment = "";
        uploadRateLimit = "";
        downloadRateLimit = "";
        totalWasted = "";
        totalUploaded = "";
        totalDownloaded = "";
        timeElapsed = "";
        nbConnections = "";
        shareRatio = "";

        try {

            if (savedInstanceState != null) {

                // Get saved values
                name = savedInstanceState.getString("torrentDetailName", "");
                size = savedInstanceState.getString("torrentDetailSize", "");
                hash = savedInstanceState.getString("torrentDetailHash", "");
                ratio = savedInstanceState.getString("torrentDetailRatio", "");
                state = savedInstanceState.getString("torrentDetailState", "");
                peersConnected = savedInstanceState.getInt("torrentDetailPeersConnected", 0);
                peersInSwarm = savedInstanceState.getInt("torrentDetailPeersInSwarm", 0);
                seedsConnected = savedInstanceState.getInt("torrentDetailSeedsConnected", 0);
                seedsInSwarm = savedInstanceState.getInt("torrentDetailSeedsInSwarm", 0);
                progress = savedInstanceState.getString("torrentDetailProgress", "");
                priority = savedInstanceState.getString("torrentDetailPriority", "");
                eta = savedInstanceState.getString("torrentDetailEta", "");
                uploadSpeed = savedInstanceState.getString("torrentDetailUploadSpeed", "");
                downloadSpeed = savedInstanceState.getString("torrentDetailDownloadSpeed", "");
                downloaded = savedInstanceState.getString("torrentDetailDownloaded", "");
                hashToUpdate = hash;

                // Only for Pro version
                if(MainActivity.packageName.equals("com.lgallardo.youtorrentcontrollerpro")) {
                    int index = progress.indexOf(".");

                    if (index == -1) {
                        index = progress.indexOf(",");

                        if (index == -1) {
                            index = progress.length();
                        }
                    }

                    percentage = progress.substring(0, index);
                }

            } else {

                // Get values from current activity
                name = this.torrent.getFile();
                size = this.torrent.getSize();
                hash = this.torrent.getHash();
                ratio = this.torrent.getRatio();
                state = this.torrent.getState();
                peersConnected = this.torrent.getPeersConnected();
                peersInSwarm = this.torrent.getPeersInSwarm();
                seedsConnected = this.torrent.getSeedsConnected();
                seedsInSwarm = this.torrent.getSeedInSwarm();
                progress = this.torrent.getProgress();
                priority = this.torrent.getPriority();
                eta = this.torrent.getEta();
                uploadSpeed = this.torrent.getUploadSpeed();
                downloadSpeed = this.torrent.getDownloadSpeed();
                hashToUpdate = hash;
                downloaded = this.torrent.getDownloaded();


                // Only for Pro version
                if(MainActivity.packageName.equals("com.lgallardo.youtorrentcontrollerpro")) {
                    int index = this.torrent.getProgress().indexOf(".");

                    if (index == -1) {
                        index = this.torrent.getProgress().indexOf(",");

                        if (index == -1) {
                            index = this.torrent.getProgress().length();
                        }
                    }

                    percentage = this.torrent.getProgress().substring(0, index);
                }
            }

            int donwloadSpeedWeigth = torrent.getDownloadSpeedWeight();
            int uploadSpeedWeigth = torrent.getUploadSpeedWeight();


            TextView nameTextView = (TextView) rootView.findViewById(R.id.torrentName);
            TextView sizeTextView = (TextView) rootView.findViewById(R.id.torrentSize);
            TextView ratioTextView = (TextView) rootView.findViewById(R.id.torrentRatio);
            TextView progressTextView = (TextView) rootView.findViewById(R.id.torrentProgress);
            TextView stateTextView = (TextView) rootView.findViewById(R.id.torrentState);
            TextView priorityTextView = (TextView) rootView.findViewById(R.id.torrentPriority);
            TextView leechsTextView = (TextView) rootView.findViewById(R.id.torrentLeechs);
            TextView seedsTextView = (TextView) rootView.findViewById(R.id.torrentSeeds);
            TextView hashTextView = (TextView) rootView.findViewById(R.id.torrentHash);
            TextView etaTextView = (TextView) rootView.findViewById(R.id.torrentEta);
            TextView uploadSpeedTextView = (TextView) rootView.findViewById(R.id.torrentUploadSpeed);
            TextView downloadSpeedTextView = (TextView) rootView.findViewById(R.id.torrentDownloadSpeed);

            CheckBox sequentialDownloadCheckBox;
            CheckBox firstLAstPiecePrioCheckBox;

            nameTextView.setText(name);
            ratioTextView.setText(ratio);
            stateTextView.setText(state);
            leechsTextView.setText("" + peersConnected+" ("+ peersInSwarm+")");
            seedsTextView.setText("" + seedsConnected+" ("+ seedsInSwarm+")");
            progressTextView.setText(progress);
            hashTextView.setText(hash);
            etaTextView.setText(eta);
            priorityTextView.setText(priority);

            // Set Downloaded vs Total size
            sizeTextView.setText(downloaded + " / " + size);

            // Only for Pro version
            if(MainActivity.packageName.equals("com.lgallardo.youtorrentcontrollerpro")) {
                downloadSpeedTextView.setText(Character.toString('\u2193') + " " + downloadSpeed);
                uploadSpeedTextView.setText(Character.toString('\u2191') + " " + uploadSpeed);

                // Set progress bar
                ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
                TextView percentageTV = (TextView) rootView.findViewById(R.id.percentage);

                progressBar.setProgress(Integer.parseInt(percentage));
                percentageTV.setText(percentage + "%");
            } else {
                downloadSpeedTextView.setText(downloadSpeed);
                uploadSpeedTextView.setText(uploadSpeed);
            }

            nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);

//            Log.d("Debug", "TorrentDetailsFragment - state: " + state);

            if ("paused".equals(state)){
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.paused, 0, 0, 0);
            }

            if ("downloading".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stalleddl, 0, 0, 0);

                if (donwloadSpeedWeigth > 0 || seedsConnected > 0 || peersConnected > 0) {
                    nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downloading, 0, 0, 0);
                }
            }


            if ("seeding".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stalledup, 0, 0, 0);

                if (uploadSpeedWeigth > 0 || seedsConnected > 0 || peersConnected > 0) {
                    nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uploading, 0, 0, 0);
                }
            }

            if ("queued".equals(state) ) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.queued, 0, 0, 0);
            }

            if ("checking".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_recheck, 0, 0, 0);
            }

            if ("stopped".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_stopped, 0, 0, 0);
            }

//            // Get Content files in background
//            qBittorrentContentFile qcf = new qBittorrentContentFile();
//            qcf.execute(new View[]{rootView});
//
//            // Get trackers in background
//            qBittorrentTrackers qt = new qBittorrentTrackers();
//            qt.execute(new View[]{rootView});
//
//            // Get general info in background
//            qBittorrentGeneralInfoTask qgit = new qBittorrentGeneralInfoTask();
//            qgit.execute(new View[]{rootView});

        } catch (Exception e) {
            Log.e("Debug", "TorrentDetailsFragment - onCreateView: " + e.toString());
        }

        if(MainActivity.packageName.equals("com.lgallardo.youtorrentcontroller")) {
            // Load banner
            loadBanner();
        }

        return rootView;
    }

    public void updateDetails(Torrent torrent) {

        try {

            // Hide herderInfo in phone's view
            if (getActivity().findViewById(R.id.one_frame) != null) {
                MainActivity.headerInfo.setVisibility(View.GONE);
            }

            // Get values from current activity
            name = torrent.getFile();
            size = torrent.getSize();
            hash = torrent.getHash();
            ratio = torrent.getRatio();
            state = torrent.getState();
            peersConnected = torrent.getPeersConnected();
            peersInSwarm = torrent.getPeersInSwarm();
            seedsConnected = torrent.getSeedsConnected();
            seedsInSwarm = torrent.getSeedInSwarm();
            progress = torrent.getProgress();
            priority = torrent.getPriority();
            eta = torrent.getEta();
            uploadSpeed = torrent.getUploadSpeed();
            downloadSpeed = torrent.getDownloadSpeed();
            downloaded = torrent.getDownloaded();


            int index = torrent.getProgress().indexOf(".");

            if (index == -1) {
                index = torrent.getProgress().indexOf(",");

                if (index == -1) {
                    index = torrent.getProgress().length();
                }
            }

            percentage = torrent.getProgress().substring(0, index);

            int donwloadSpeedWeigth = torrent.getDownloadSpeedWeight();
            int uploadSpeedWeigth = torrent.getUploadSpeedWeight();


            FragmentManager fragmentManager = getFragmentManager();

            TorrentDetailsFragment detailsFragment = null;

            if (getActivity().findViewById(R.id.one_frame) != null) {
                detailsFragment = (TorrentDetailsFragment) fragmentManager.findFragmentByTag("firstFragment");
            } else {
                detailsFragment = (TorrentDetailsFragment) fragmentManager.findFragmentByTag("secondFragment");
            }

            View rootView = detailsFragment.getView();

            TextView nameTextView = (TextView) rootView.findViewById(R.id.torrentName);
            TextView sizeTextView = (TextView) rootView.findViewById(R.id.torrentSize);
            TextView ratioTextView = (TextView) rootView.findViewById(R.id.torrentRatio);
            TextView priorityTextView = (TextView) rootView.findViewById(R.id.torrentPriority);
            TextView stateTextView = (TextView) rootView.findViewById(R.id.torrentState);
            TextView leechsTextView = (TextView) rootView.findViewById(R.id.torrentLeechs);
            TextView seedsTextView = (TextView) rootView.findViewById(R.id.torrentSeeds);
            TextView progressTextView = (TextView) rootView.findViewById(R.id.torrentProgress);
            TextView hashTextView = (TextView) rootView.findViewById(R.id.torrentHash);

            TextView etaTextView = (TextView) rootView.findViewById(R.id.torrentEta);
            TextView uploadSpeedTextView = (TextView) rootView.findViewById(R.id.torrentUploadSpeed);
            TextView downloadSpeedTextView = (TextView) rootView.findViewById(R.id.torrentDownloadSpeed);

            CheckBox sequentialDownloadCheckBox;
            CheckBox firstLAstPiecePrioCheckBox;

            nameTextView.setText(name);
            ratioTextView.setText(ratio);
            stateTextView.setText(state);
            leechsTextView.setText("" + peersConnected+" ("+ peersInSwarm+")");
            seedsTextView.setText("" + seedsConnected+" ("+ seedsInSwarm+")");
            progressTextView.setText(progress);
            hashTextView.setText(hash);
            priorityTextView.setText(priority);
            etaTextView.setText(eta);

            // Set Downloaded vs Total size
            sizeTextView.setText(downloaded + " / " + size);

            // Only for Pro version
            if(MainActivity.packageName.equals("com.lgallardo.youtorrentcontrollerpro")) {
                downloadSpeedTextView.setText(Character.toString('\u2193') + " " + downloadSpeed);
                uploadSpeedTextView.setText(Character.toString('\u2191') + " " + uploadSpeed);

                // Set progress bar
                ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
                TextView percentageTV = (TextView) rootView.findViewById(R.id.percentage);

                progressBar.setProgress(Integer.parseInt(percentage));
                percentageTV.setText(percentage + "%");

            }else {
                downloadSpeedTextView.setText(downloadSpeed);
                uploadSpeedTextView.setText(uploadSpeed);
            }



            nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);

//            Log.d("Debug", "TorrentDetailsFragment - state: " + state);

            if ("paused".equals(state)){
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.paused, 0, 0, 0);
            }

            if ("downloading".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stalleddl, 0, 0, 0);

                if (donwloadSpeedWeigth > 0 || seedsConnected > 0 || peersConnected > 0) {
                    nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downloading, 0, 0, 0);
                }
            }


            if ("seeding".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stalledup, 0, 0, 0);

                if (uploadSpeedWeigth > 0 || seedsConnected > 0 || peersConnected > 0) {
                    nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uploading, 0, 0, 0);
                }
            }

            if ("queued".equals(state) ) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.queued, 0, 0, 0);
            }

            if ("checking".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_recheck, 0, 0, 0);
            }

            if ("stopped".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_stopped, 0, 0, 0);
            }


//            // Get Content files in background
//            qBittorrentContentFile qcf = new qBittorrentContentFile();
//            qcf.execute(new View[]{rootView});
//
//            // Get trackers in background
//            qBittorrentTrackers qt = new qBittorrentTrackers();
//            qt.execute(new View[]{rootView});
//
//            // Get General info in background
//            qBittorrentGeneralInfoTask qgit = new qBittorrentGeneralInfoTask();
//            qgit.execute(new View[]{rootView});

        } catch (Exception e) {

            Log.e("Debug", "TorrentDetailsFragment - onCreateView: " + e.toString());
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("torrentDetailName", name);
        outState.putString("torrentDetailSize", size);
        outState.putString("torrentDetailHash", hash);
        outState.putString("torrentDetailRatio", ratio);
        outState.putString("torrentDetailState", state);
        outState.putInt("torrentDetailPeersConnected", peersConnected);
        outState.putInt("torrentDetailPeersInSwarm", peersInSwarm);
        outState.putInt("torrentDetailSeedsConnected", seedsConnected);
        outState.putInt("torrentDetailSeedsInSwarm", seedsInSwarm);
        outState.putString("torrentDetailProgress", progress);
        outState.putString("torrentDetailPriority", priority);
        outState.putString("torrentDetailEta", eta);
        outState.putString("torrentDetailUploadSpeed", uploadSpeed);
        outState.putString("torrentDetailDownloadSpeed", downloadSpeed);
        outState.putString("torrentDetailDownloaded", downloaded);

    }

    // @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {

    }

    // @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {

//            menu.findItem(R.id.action_resume_all).setVisible(false);
//            menu.findItem(R.id.action_pause_all).setVisible(false);

            if (getActivity().findViewById(R.id.one_frame) != null) {
                menu.findItem(R.id.action_refresh).setVisible(false);
            }else{
                menu.findItem(R.id.action_refresh).setVisible(true);
            }

            if (getActivity().findViewById(R.id.one_frame) != null) {
                menu.findItem(R.id.action_sort_menu).setVisible(false);
            }else{
                menu.findItem(R.id.action_sort_menu).setVisible(true);
            }

            menu.findItem(R.id.action_resume).setVisible(true);
            menu.findItem(R.id.action_pause).setVisible(true);
            menu.findItem(R.id.action_stop).setVisible(true);
            menu.findItem(R.id.action_priority_menu).setVisible(true);
            menu.findItem(R.id.action_increase_prio).setVisible(true);
            menu.findItem(R.id.action_decrease_prio).setVisible(true);
            menu.findItem(R.id.action_max_prio).setVisible(true);
            menu.findItem(R.id.action_min_prio).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_delete_drive).setVisible(true);
            menu.findItem(R.id.action_recheck).setVisible(true);

            if (getActivity().findViewById(R.id.one_frame) != null) {
                menu.findItem(R.id.action_search).setVisible(false);
            } else {
                menu.findItem(R.id.action_search).setVisible(true);
            }

            menu.findItem(R.id.action_add).setVisible(true);



        }
    }

    // Load Banner method
    public void loadBanner() {

        // Get the adView.
        adView = (AdView) getActivity().findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

    }

    public void setTorrent(Torrent torrent) {
        this.torrent = torrent;
    }

    // // Here is where the action happens
    private class qBittorrentContentFile extends AsyncTask<View, View, View[]> {

        String name, size;
        Double progress;
        int priority;

        protected View[] doInBackground(View... rootViews) {

            // Get torrent's files
            qbQueryString = "query";


            url = qbQueryString + "/propertiesFiles/";

            files = null;
            names = null;

            try {

                JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port,
                        MainActivity.username, MainActivity.password, MainActivity.connection_timeout, MainActivity.data_timeout);

                jParser.setCookie(MainActivity.cookie);

                JSONArray jArray = jParser.getJSONArrayFromUrl(url + hash);

                if (jArray != null) {

                    files = new ContentFile[jArray.length()];
                    TorrentDetailsFragment.names = new String[jArray.length()];

                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject json = jArray.getJSONObject(i);

                        name = json.getString(MainActivity.TAG_NAME);
                        size = json.getString(MainActivity.TAG_SIZE).replace(",", ".");
                        progress = json.getDouble(MainActivity.TAG_PROGRESS);
                        priority = json.getInt(MainActivity.TAG_PRIORITY);

                        size = Common.calculateSize(json.getString(MainActivity.TAG_SIZE)).replace(",", ".");

                        files[i] = new ContentFile(name, size, progress, priority);
                        names[i] = name;

                    }

                }

            } catch (Exception e) {

                Log.e("TorrentFragment:", e.toString());

            }

            return rootViews;

        }

        @Override
        protected void onPostExecute(View[] rootViews) {

            try {

                View rootView = rootViews[0];

                fileAdpater = new myFileAdapter(getActivity(), names, files);

                ListView lv = (ListView) rootView.findViewById(R.id.theList);

                lv.setFocusable(false);

                lv.setAdapter(fileAdpater);

                setListViewHeightBasedOnChildren(lv);

            } catch (Exception e) {
                Log.e("Content2", e.toString());

            }

        }

    }

    // // Here is where the action happens
    private class qBittorrentTrackers extends AsyncTask<View, View, View[]> {

        String url;

        protected View[] doInBackground(View... rootViews) {
            // Get torrent's trackers

            qbQueryString = "query";

            url = qbQueryString + "/propertiesTrackers/";

            trackers = null;
            trackerNames = null;

            try {

                JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port,
                        MainActivity.username, MainActivity.password, MainActivity.connection_timeout, MainActivity.data_timeout);

                jParser.setCookie(MainActivity.cookie);

                JSONArray jArray = jParser.getJSONArrayFromUrl(url + hash);

                if (jArray != null) {

                    trackers = new Tracker[jArray.length()];
                    TorrentDetailsFragment.trackerNames = new String[jArray.length()];

                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject json = jArray.getJSONObject(i);

                        url = json.getString(MainActivity.TAG_URL);

                        trackers[i] = new Tracker(url);
                        trackerNames[i] = url;

                    }

                }

            } catch (Exception e) {

                Log.e("TorrentFragment:", e.toString());

            }

            return rootViews;

        }

        @Override
        protected void onPostExecute(View[] rootViews) {

            try {

                View rootView = rootViews[0];

                trackerAdapter = new myTrackerAdapter(getActivity(), trackerNames, trackers);

                LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.trackers);
                layout.removeAllViews();

                for (int i = 0; i < trackerAdapter.getCount(); i++) {
                    View item = trackerAdapter.getView(i, null, null);
                    layout.addView(item);
                }

            } catch (Exception e) {
                Log.e("Trackers", e.toString());

            }

        }

    }

    // Here is where the action happens
    private class qBittorrentGeneralInfoTask extends AsyncTask<View, View, View[]> {

        String[] labels;
        String[] values;

        protected View[] doInBackground(View... rootViews) {
            // Get torrent's extra info

            qbQueryString = "query";

            url = qbQueryString + "/propertiesGeneral/";

            try {

                JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port,
                        MainActivity.username, MainActivity.password, MainActivity.connection_timeout, MainActivity.data_timeout);

                jParser.setCookie(MainActivity.cookie);

                json2 = jParser.getJSONFromUrl(url + hash);

                if (json2 != null && json2.length() > 0) {

                    labels = new String[11];
                    values = new String[11];

                    // Save path
                    labels[0] = getString(R.string.torrent_details_save_path);
                    values[0] = json2.getString(TAG_SAVE_PATH);

                    // Creation date
                    labels[1] = getString(R.string.torrent_details_created_date);
                    values[1] = json2.getString(TAG_CREATION_DATE);

                    // Comment
                    labels[2] = getString(R.string.torrent_details_comment);
                    values[2] = json2.getString(TAG_COMMENT);

                    // Total wasted
                    labels[3] = getString(R.string.torrent_details_total_wasted);
                    values[3] = json2.getString(TAG_TOTAL_WASTED);

                    // Total uploaded
                    labels[4] = getString(R.string.torrent_details_total_uploaded);
                    values[4] = json2.getString(TAG_TOTAL_UPLOADED);

                    // Total downloaded
                    labels[5] = getString(R.string.torrent_details_total_downloaded);
                    values[5] = json2.getString(TAG_TOTAL_DOWNLOADED);

                    // Time elapsed
                    labels[6] = getString(R.string.torrent_details_time_elapsed);
                    values[6] = json2.getString(TAG_TIME_ELAPSED);

                    // Number of connections
                    labels[7] = getString(R.string.torrent_details_num_connections);
                    values[7] = json2.getString(TAG_NB_CONNECTIONS);

                    // Share ratio
                    labels[8] = getString(R.string.torrent_details_share_ratio);
                    values[8] = json2.getString(TAG_SHARE_RATIO);

                    // Upload limit
                    labels[9] = getString(R.string.torrent_details_upload_rate_limit);
                    values[9] = json2.getString(TAG_UPLOAD_LIMIT);

                    // Download limit
                    labels[10] = getString(R.string.torrent_details_download_rate_limit);
                    values[10] = json2.getString(TAG_DOWNLOAD_LIMIT);


//                    if (MainActivity.qb_version.equals("3.2.x")) {

                        // Creation date
                        values[1] = Common.unixTimestampToDate(json2.getString(TAG_CREATION_DATE));
                        // Total wasted
                        values[3] = Common.calculateSize(json2.getString(TAG_TOTAL_WASTED)).replace(",", ".");

                        // Total uploaded
                        values[4] = Common.calculateSize(json2.getString(TAG_TOTAL_UPLOADED)).replace(",", ".");

                        // Time elapsed
                        values[6] = Common.secondsToEta(json2.getString(TAG_TIME_ELAPSED));

                        // Total downloaded
                        values[5] = Common.calculateSize(json2.getString(TAG_TOTAL_DOWNLOADED)).replace(",", ".");

                        // Upload limit
                        values[9] = json2.getString(TAG_UPLOAD_LIMIT);

                        if (!values[9].equals("-1")) {
                            values[9] = Common.calculateSize(values[9]) + "/s";
                        } else {
                            values[9] = "∞";
                        }

                        // Download limit
                        values[10] = json2.getString(TAG_DOWNLOAD_LIMIT);

                        if (!values[10].equals("-1")) {
                            values[10] = Common.calculateSize(values[10]) + "/s";
                        } else {
                            values[10] = "∞";
                        }

                    }


//                }

            } catch (Exception e) {

                Log.e("TorrentFragment:", e.toString());

            }

            return rootViews;

        }

        @Override
        protected void onPostExecute(View[] rootViews) {

            try {

                View rootView = rootViews[0];

                propertyAdapter = new myPropertyAdapter(getActivity(), labels, values);

                LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.lines);
                layout.removeAllViews();

                for (int i = 0; i < propertyAdapter.getCount(); i++) {
                    View item = propertyAdapter.getView(i, null, null);
                    layout.addView(item);
                }

            } catch (Exception e) {
                Log.e("TorrentFragment:", e.toString());
            }

        }

    }

    // My custom adapters
    class myPropertyAdapter extends ArrayAdapter<String> {

        private String[] labels;
        private String[] values;
        private Context context;

        public myPropertyAdapter(Context context, String[] labels, String[] values) {
            super(context, R.layout.property_row, R.id.label, values);

            this.context = context;
            this.labels = labels;
            this.values = values;

        }

        @Override
        public int getCount() {
            return (labels != null) ? labels.length : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = super.getView(position, convertView, parent);

            TextView label = (TextView) row.findViewById(R.id.label);
            TextView value = (TextView) row.findViewById(R.id.value);

            label.setText("" + labels[position]);
            value.setText("" + values[position]);

            return (row);
        }
    }

    class myFileAdapter extends ArrayAdapter<String> {

        private String[] filesNames;
        private ContentFile[] files;
        private Context context;

        public myFileAdapter(Context context, String[] filesNames, ContentFile[] files) {
            super(context, R.layout.contentfile_row, R.id.file, filesNames);

            this.context = context;
            this.filesNames = filesNames;
            this.files = files;

        }

        @Override
        public int getCount() {
            return (filesNames != null) ? filesNames.length : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = super.getView(position, convertView, parent);

            TextView info = (TextView) row.findViewById(R.id.info);

            info.setText("" + files[position].getSize());

            // Set progress bar
            ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progressBar1);
            TextView percentageTV = (TextView) row.findViewById(R.id.percentage);

            int index = files[position].getProgressAsString().indexOf(".");

            if (index == -1) {
                index = files[position].getProgressAsString().indexOf(",");

                if (index == -1) {
                    index = files[position].getProgressAsString().length();
                }
            }

            String percentage = files[position].getProgressAsString().substring(0, index);

            progressBar.setProgress(Integer.parseInt(percentage));

            percentageTV.setText(percentage + "%");

            return (row);
        }
    }

    class myTrackerAdapter extends ArrayAdapter<String> {

        private String[] trackersNames;
        private Tracker[] trackers;
        private Context context;

        public myTrackerAdapter(Context context, String[] trackersNames, Tracker[] trackers) {
            super(context, R.layout.tracker_row, R.id.tracker, trackersNames);

            this.context = context;
            this.trackersNames = trackersNames;
            this.trackers = trackers;

        }

        @Override
        public int getCount() {
            return (trackersNames != null) ? trackersNames.length : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = super.getView(position, convertView, parent);

            TextView tracker = (TextView) row.findViewById(R.id.tracker);

            tracker.setText("" + trackers[position].getUrl());

            return (row);
        }
    }

    /**
     * *
     * Method for Setting the Height of the ListView dynamically. Hack to fix
     * the issue of not showing all the items of the ListView when placed inside
     * a ScrollView
     * **
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++) {

            long numOfLines = 1;
            view = listAdapter.getView(i, view, listView);

            if (i == 0) {
                view.setLayoutParams(new LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));
            }

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);

            TextView file = (TextView) view.findViewById(R.id.file);
            TextView info = (TextView) view.findViewById(R.id.info);

            if (view.getMeasuredWidth() > desiredWidth) {

                double viewWidthLong = Double.valueOf(view.getMeasuredWidth());
                double desiredWidthLong = Double.valueOf(desiredWidth);

                numOfLines = Math.round(viewWidthLong / desiredWidthLong) + 1;

                totalHeight += file.getMeasuredHeight() * numOfLines + info.getMeasuredHeight();

            } else {
                totalHeight += view.getMeasuredHeight();
            }

        }

        LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();

    }

}
