

package com.example.capsenderreceiver;

import java.util.Calendar;

import com.googlecode.android.widgets.DateSlider.labeler.TimeLabeler;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Demonstrates a "card-flip" animation using custom fragment transactions ({@link
 * android.app.FragmentTransaction#setCustomAnimations(int, int)}).
 *
 * <p>This sample shows an "info" action bar button that shows the back of a "card", rotating the
 * front of the card out and the back of the card in. The reverse animation is played when the user
 * presses the system Back button or the "photo" action bar button.</p>
 * 
 */

abstract interface onDateTimeSelectedListener {
    /** Called by onDateTimeSelected when a list item is selected */
    public void onDateTimeSelected(String title);
}

public class CreateCoupon extends FragmentActivity
        implements FragmentManager.OnBackStackChangedListener, onDateTimeSelectedListener
 {
    /**
     * A handler object, used for deferring UI operations.
     */
    private Handler mHandler = new Handler();
    
    // The container Activity must implement this interface so the frag can deliver messages

    
    static final int MAP_ACTIVITY_RESULT = 1;  // The request code

    public static String[] constraintText = {
    	    "Add Geo Awareness",
    	     "Add Date & Time"
    	  } ;
    	  Integer[] imageId = {
    	      R.drawable.locationaware,
    	      R.drawable.dateandtime
    	  };
    	  
    	  public static String[] titleText = {
    	    	    "",
    	    	     ""
    	    	  } ;
    	  ListView list;
    	  
    	
    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private boolean mShowingBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_flip);

        list=(ListView)findViewById(R.id.list);
        if (savedInstanceState == null) {
            // If there is no saved instance state, add a fragment representing the
            // front of the card to this activity. If there is saved instance state,
            // this fragment will have already been added to the activity.
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new CardFrontFragment())
                    .commit();
        } else {
            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        }

        // Monitor back stack changes to ensure the action bar shows the appropriate
        // button (either "photo" or "info").
        getFragmentManager().addOnBackStackChangedListener(this);
        
        
        
    }
    
    public void onDateTimeSelected(String title) {
    	DialogFragment dialog = new DateTimeDialog();
		dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Add either a "photo" or "finish" button to the action bar, depending on which page
        // is currently selected.
        MenuItem item = menu.add(Menu.NONE, R.id.action_flip, Menu.NONE,
                mShowingBack
                        ? R.string.action_photo
                        : R.string.action_info);
        item.setIcon(mShowingBack
                ? R.drawable.ic_action_photo
                : R.drawable.ic_action_info);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_flip:
                flipCard();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    private void flipCard() {
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }

        // Flip to the back.

        mShowingBack = true;

        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.
        ListViewCustomAdapter adapter = new ListViewCustomAdapter(this, constraintText, titleText);
        getFragmentManager()
                .beginTransaction()

                // Replace the default fragment animations with animator resources representing
                // rotations when switching to the back of the card, as well as animator
                // resources representing rotations when flipping back to the front (e.g. when
                // the system Back button is pressed).
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                 
                // Replace any fragments currently in the container view with a fragment
                // representing the next page (indicated by the just-incremented currentPage
                // variable).
                .replace(R.id.container, new CardBackFragment(list, adapter))

                // Add this transaction to the back stack, allowing users to press Back
                // to get to the front of the card.
                .addToBackStack(null)

                // Commit the transaction.
                .commit();

        // Defer an invalidation of the options menu (on modern devices, the action bar). This
        // can't be done immediately because the transaction may not yet be committed. Commits
        // are asynchronous in that they are posted to the main thread's message loop.
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);

        // When the back stack changes, invalidate the options menu (action bar).
        invalidateOptionsMenu();
    }

    /**
     * A fragment representing the front of the card.
     */
    public static class CardFrontFragment extends Fragment {

    	  //FragmentManager frag = getChildFragmentManager ();
        public CardFrontFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_card_front, container, false);
            return rootView;
        }
    }
    /**
     * A fragment representing the back of the card.
     */
    public static class CardBackFragment extends Fragment {
    	ListView list;
    	ListViewCustomAdapter adapter;
    	onDateTimeSelectedListener mCallback;

    	
    	public CardBackFragment() {
    		
    	}
        public CardBackFragment(ListView list, ListViewCustomAdapter adapter) {
        	this.list = list;
        	this.adapter = adapter;
        }

   
        
        @Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
        	double lat , lng , radius = 0.0;
        	String title = "";
            // Check which request we're responding to
            if (requestCode == MAP_ACTIVITY_RESULT) {
                // Make sure the request was successful
                if (resultCode == RESULT_OK) {
                	String text = "";
                	lat = data.getDoubleExtra("lat", 0.0);
                	lng = data.getDoubleExtra("lng", 0.0);
                	radius = data.getDoubleExtra("radius", 1.0);
                	title = data.getStringExtra("title");
                	
                	text = title + " \n [ lat : " + lat + " ] , [ lng : " + lng + " ] \n [ radius : " + radius + " ]";
                	
                	constraintText[0] = "GEO-AWARE COUPON!";
        	    	titleText[0] = text;

                }
            }
        }
        
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception.
            try {
                mCallback = (onDateTimeSelectedListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnHeadlineSelectedListener");
            }
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_card_back, container, false);

            list=(ListView)rootView.findViewById(R.id.list);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //Toast.makeText(CreateCoupon.this, "You Clicked at " +constraintText[+ position], Toast.LENGTH_SHORT).show();
                	switch (position) {
	                	case 0:
	                		Intent intent = new Intent(parent.getContext(), MapFragmentActivity.class);
	                		startActivityForResult(intent, MAP_ACTIVITY_RESULT);
	                	  break;
	                	case 1:
	                		//DialogFragment dialog = new DatePickerFragment();
	                		 mCallback.onDateTimeSelected("");
	                		//dialog.show(getSupportFragmentManager(), "tag");
	                	  break;
	                	default:
	                	  break;
                	}
                }
            });
            return rootView;
        }
        
        
    }
    
    private DateSlider.OnDateSetListener mDateTimeSetListener =
            new DateSlider.OnDateSetListener() {
                public void onDateSet(DateSlider view, Calendar selectedDate) {
                    // update the dateText view with the corresponding date
                    int minute = selectedDate.get(Calendar.MINUTE) /
                            TimeLabeler.MINUTEINTERVAL*TimeLabeler.MINUTEINTERVAL;
                    titleText[1] = (String.format("The chosen date and time:%n%te. %tB %tY%n%tH:%02d",
                            selectedDate, selectedDate, selectedDate, selectedDate, minute));
                    Toast.makeText(CreateCoupon.this, titleText[1], Toast.LENGTH_LONG).show();
                }
        };


	
        public class DateTimeDialog extends DialogFragment
        {
            public DateTimeDialog()
            {
            }

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState)
            {
            	final Calendar c = Calendar.getInstance();
                return new DateTimeSlider(getActivity(), mDateTimeSetListener,c);
            }
        }
        
        
    public static class ViewHolder
	{
		ImageView imgViewLogo;
		TextView txtViewTitle;
		TextView txtViewDescription;
	}

    public class ListViewCustomAdapter extends BaseAdapter
    {
    	public String title[];
    	public String description[];
    	public Activity context;
    	public LayoutInflater inflater;

    	public ListViewCustomAdapter(Activity context,String[] title, String[] description) {
    		super();

    		this.context = context;
    		this.title = title;
    		this.description = description;

    	    this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}

    	@Override
    	public int getCount() {
    		// TODO Auto-generated method stub
    		return title.length;
    	}

    	@Override
    	public Object getItem(int position) {
    		// TODO Auto-generated method stub
    		return null;
    	}

    	@Override
    	public long getItemId(int position) {
    		// TODO Auto-generated method stub
    		return 0;
    	}


    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		// TODO Auto-generated method stub

    		ViewHolder holder;
    		if(convertView==null)
    		{
    			holder = new ViewHolder();
    			convertView = inflater.inflate(R.layout.list_item, null);

    			holder.imgViewLogo = (ImageView) convertView.findViewById(R.id.img);
    			holder.txtViewTitle = (TextView) convertView.findViewById(R.id.txt);
    			holder.txtViewDescription = (TextView) convertView.findViewById(R.id.txtSmall);

    			convertView.setTag(holder);
    		}
    		else
    			holder=(ViewHolder)convertView.getTag();

    		holder.imgViewLogo.setImageResource(imageId[position]);
    		holder.txtViewTitle.setText(title[position]);
    		holder.txtViewDescription.setText(titleText[position]);
    		//holder.txtViewDescription.setFocusable(true);
    		//holder.txtViewDescription.setSingleLine();
    		//holder.txtViewDescription.setMarqueeRepeatLimit(100);

    		return convertView;
    	}

    }
}
