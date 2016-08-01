package com.codepath.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.utils.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by santoshag on 7/31/16.
 */
public class SearchFilterFragment extends BottomSheetDialogFragment {


    @BindView(R.id.ivBeginDate)
    public ImageView ivBeginDate;
    @BindView(R.id.ivEndDate)
    public ImageView ivEndDate;
    @BindView(R.id.lvFilter)
    public ListView lvFilter;
    @BindView(R.id.svFilter)
    public ScrollView svFilter;
    @BindView(R.id.tvBeginDate)
    public TextView tvBeginDate;
    @BindView(R.id.tvEndDate)
    public TextView tvEndDate;

    private ArrayList<Filter_Object> mArrFilter;
    public Filter_Adapter mFilter_Adapter;

    @BindView(R.id.flFilter)
    public FlowLayout flFilter;
    SharedPreferences sharedpreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_filters, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.switch_compat);
        Log.i("DEBUG", "swicth:" + switchCompat);
        switchCompat.setOnCheckedChangeListener(onCheckedChanged());
        sharedpreferences = getActivity().getSharedPreferences(Utilities.FILTER_PREFERENCES, Context.MODE_PRIVATE);

        ivBeginDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "begin_date");

            }
        });

        ivEndDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "end_date");

            }
        });

        String[] strArr = getResources().getStringArray(R.array.news_desks);
        mArrFilter = new ArrayList<>();
        int lengthOfstrArr = strArr.length;

        for (int i = 0; i < lengthOfstrArr; i++) {
            Filter_Object filter_object = new Filter_Object();
            filter_object.mName = strArr[i];
            filter_object.mIsSelected = false;
            mArrFilter.add(filter_object);
        }

        setFilter(view);
    }

    private void setFilter(View v) {


        mFilter_Adapter = new Filter_Adapter(mArrFilter);
        lvFilter.setAdapter(mFilter_Adapter);

    }

    private CompoundButton.OnCheckedChangeListener onCheckedChanged() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.switch_compat:
                        setSortOrder(isChecked);
                        break;
                }
            }
        };
    }

    public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            setDate(this.getTag(), year, month, day);
        }

    }

    private void setDate(String tag, int year, int month, int day) {
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat displayFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);
        sharedpreferences.edit().putString(tag, apiFormat.format(calendar.getTime())).commit();
        switch (tag) {
            case "begin_date":
                tvBeginDate.setText(displayFormat.format(calendar.getTime()));
                tvBeginDate.setVisibility(View.VISIBLE);
                break;
            case "end_date":
                tvEndDate.setText(displayFormat.format(calendar.getTime()));
                tvEndDate.setVisibility(View.VISIBLE);
                break;

        }
    }


    private void setSortOrder(boolean state) {
        if (state) {
            sharedpreferences.edit().putString("sort", Utilities.SORT_BY_NEWEST).commit();
        } else {
            sharedpreferences.edit().putString("sort", Utilities.SORT_BY_OLDEST).commit();
        }
    }

    public void addFilterTag() {
        final ArrayList<Filter_Object> arrFilterSelected = new ArrayList<>();

        flFilter.removeAllViews();

        int length = mArrFilter.size();
        sharedpreferences.edit().putString("news_desk", "").commit();
        boolean isSelected = false;
        for (int i = 0; i < length; i++) {
            Filter_Object fil = mArrFilter.get(i);
            Log.i("nd", fil.mName);
            if (fil.mIsSelected) {
                String news_desk = sharedpreferences.getString("news_desk", "");
                //add to filters shared preferences
                sharedpreferences.edit().putString("news_desk", news_desk + " \"" + fil.mName + "\" ").commit();
                isSelected = true;
                arrFilterSelected.add(fil);
            }
        }

        if (isSelected) {
            svFilter.setVisibility(View.VISIBLE);
        } else {
            svFilter.setVisibility(View.INVISIBLE);
        }
        int size = arrFilterSelected.size();
        LayoutInflater layoutInflater = (LayoutInflater)
                getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < size; i++) {
            View view = layoutInflater.inflate(R.layout.filter_tag_edit, null);

            TextView tv = (TextView) view.findViewById(R.id.tvTag);
            LinearLayout linClose = (LinearLayout) view.findViewById(R.id.linClose);
            final Filter_Object filter_object = arrFilterSelected.get(i);
            linClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showToast(filter_object.name);


                    int innerSize = mArrFilter.size();
                    for (int j = 0; j < innerSize; j++) {
                        Filter_Object mFilter_Object = mArrFilter.get(j);
                        if (mFilter_Object.mName.equalsIgnoreCase(filter_object.mName)) {
                            mFilter_Object.mIsSelected = false;

                        }
                    }
                    addFilterTag();
                    mFilter_Adapter.updateListView(mArrFilter);
                }
            });


            tv.setText(filter_object.mName);
            int color = getResources().getColor(R.color.colorPrimary);

            View newView = view;
            newView.setBackgroundColor(color);

            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = 10;
            params.topMargin = 5;
            params.leftMargin = 10;
            params.bottomMargin = 5;

            newView.setLayoutParams(params);

            flFilter.addView(newView);
        }
    }

    public class Filter_Adapter extends BaseAdapter {
        ArrayList<Filter_Object> arrMenu;

        public Filter_Adapter(ArrayList<Filter_Object> arrOptions) {
            this.arrMenu = arrOptions;
        }

        public void updateListView(ArrayList<Filter_Object> mArray) {
            this.arrMenu = mArray;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.arrMenu.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.filter_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Filter_Object mService_Object = arrMenu.get(position);
            viewHolder.tvName.setText(mService_Object.mName);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mService_Object.mIsSelected = !mService_Object.mIsSelected;
                    svFilter.setVisibility(View.VISIBLE);

                    addFilterTag();
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        public class ViewHolder {
            TextView tvName;

        }
    }

}
