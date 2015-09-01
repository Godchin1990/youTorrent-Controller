package com.lgallardo.youtorrentcontroller;

/**
 * Created by lgallard on 28/08/15.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DrawerItemRecyclerViewAdapter extends RecyclerView.Adapter<DrawerItemRecyclerViewAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_ITEM_ACTIVE = 2;
    private static final int TYPE_SERVER = 3;
    private static final int TYPE_SERVER_ACTIVE = 4;
    private static final int TYPE_CATEGORY = 5;


    public static ArrayList<ObjectDrawerItem> items;
    public static int oldActionPosition = 1;
    public static int actionPosition = 0;

    private static MainActivity mainActivity;
    private static int drawerOffset = 2;

    private static int drawerOffset2 = 0;


    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int Holderid;

//        TextView textView;
//        ImageView imageView;
//
//        ImageView profile;
//        TextView Name;
//        TextView email;


        // New
        ImageView imageViewIcon;
        TextView textViewName;


        public ViewHolder(final View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            Holderid = 0;
            if (ViewType != TYPE_HEADER) {

                itemView.setClickable(true);
                itemView.setOnClickListener(this);

                Holderid = 1;
            }


            if (ViewType == TYPE_CATEGORY || ViewType == TYPE_SERVER || ViewType == TYPE_HEADER) {
                drawerOffset2 = drawerOffset2 + 1;
            }

            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
            imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);

        }


        // In order to track the item position in RecyclerView
        // Handle item click and set the selection
        @Override
        public void onClick(View view) {


            ObjectDrawerItem drawerItem;

            actionPosition = getLayoutPosition() - drawerOffset;




            // If the header is not selected and an action is selected
            if (actionPosition > 0 && actionPosition < 7) {

//                // Mark old item as inactive
//                drawerItem = DrawerItemRecyclerViewAdapter.items.get(oldActionPosition);
//                drawerItem.setActive(false);
//                DrawerItemRecyclerViewAdapter.items.set(oldActionPosition, drawerItem);

//                oldActionPosition = actionPosition;

                // Disable all items
                for (int i = 0; i < items.size(); i++) {
                    drawerItem = items.get(i);

                    if(drawerItem.getType() == TYPE_ITEM || drawerItem.getType() == TYPE_ITEM_ACTIVE) {
                        drawerItem.setActive(false);
                    }

                    items.set(i, drawerItem);
                }

                // Mark new item as active
                drawerItem = items.get(actionPosition + drawerOffset-1);
                drawerItem.setActive(true);
                items.set(actionPosition + drawerOffset-1, drawerItem);

//                notifyItemChanged(actionPosition + 1);

                notifyDataSetChanged();

            }
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - id: " + getLayoutPosition());

            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - actionPosition: " + (actionPosition));
//            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - oldActionPosition: " + (oldActionPosition));
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - offSetPosition: " + (drawerOffset));
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - offSetPosition2: " + (drawerOffset2));

            switch (actionPosition) {
                case 1:
                    mainActivity.refreshFromDrawerAction("all", actionPosition);
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: All");
                    break;
                case 2:
                    mainActivity.refreshFromDrawerAction("downloading", actionPosition);
                    mainActivity.saveLastState("downloading");
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Downloading");
                    break;
                case 3:
                    mainActivity.refreshFromDrawerAction("completed", actionPosition);
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Completed");
                    break;
                case 4:
                    mainActivity.refreshFromDrawerAction("pause", actionPosition);
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Pause");
                    break;
                case 5:
                    mainActivity.refreshFromDrawerAction("active", actionPosition);
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Active");
                    break;
                case 6:
                    mainActivity.refreshFromDrawerAction("inactive", actionPosition);
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Inactive");
                    break;
                case 7:
                    mainActivity.openSettings();
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Settings");
                    break;
                case 8:
                    if (MainActivity.packageName.equals("com.lgallardo.youtorrentcontroller")) {
                        // Get Pro version
                        mainActivity.getPRO();
                    } else {
                        mainActivity.openHelp();
                    }
                    break;
                case 9:
                    mainActivity.openHelp();
                    break;
                default:
                    mainActivity.saveLastState(MainActivity.currentState);
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Default");
                    break;
            }
            // Close drawer
            mainActivity.drawerLayout.closeDrawer(mainActivity.mRecyclerView);

            // Load banner
            mainActivity.loadBanner();


        }

    }


    DrawerItemRecyclerViewAdapter(MainActivity mainActivity, ArrayList<ObjectDrawerItem> items) {

        this.mainActivity = mainActivity;
        DrawerItemRecyclerViewAdapter.items = items;

    }


    //Below first we override the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public DrawerItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_CATEGORY) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_ITEM_ACTIVE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row_active, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {


            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhHeader; //returning the object created


        }
        return null;

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(DrawerItemRecyclerViewAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == 1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image


            ObjectDrawerItem item = items.get(position - 1);


            if (item.getType() != TYPE_CATEGORY) {
                holder.imageViewIcon.setImageResource(item.icon);
            }

            holder.textViewName.setText(item.name);

//            oldActionPosition = actionPosition;
//            actionPosition = position;

//            Log.d("Debug", "DrawerItemRecyclerViewAdapter - position: " + position);


        } else {

            // header

            return;
//            holder.profile.setImageResource(profile);           // Similarly we set the resources for header view
//            holder.Name.setText(name);
//            holder.email.setText(email);
        }
    }


    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        // Return the number of items in the list (header + item actions)
        return items.size() + 1;
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - TYPE_HEADER");
            return TYPE_HEADER;
        }


        if (items.get(position - 1).getType() == TYPE_ITEM && items.get(position - 1).isActive()) {
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - TYPE_ITEM_ACTIVE");
            return TYPE_ITEM_ACTIVE;
        }


        if (items.get(position - 1).getType() == TYPE_CATEGORY) {
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - TYPE_CATEGORY");
            return TYPE_CATEGORY;
        }

        // Default
        Log.d("Debug", "DrawerItemRecyclerViewAdapter - TYPE_ITEM");
        return TYPE_ITEM;

    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}
