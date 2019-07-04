package ar.gob.jiaac.flysafe.fragments;


import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import ar.gob.jiaac.flysafe.R;

@SuppressWarnings("WeakerAccess")
public class ReportsTabsFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports_tabs, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null) {
            ViewPager viewPager = view.findViewById(R.id.viewpager);
            setupViewPager(viewPager);
            TabLayout tabs = view.findViewById(R.id.result_tabs);
            tabs.setupWithViewPager(viewPager);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager(), this);
        adapter.addFragment(HomeFragment.newInstance(), getString(R.string.LatestReports));
        adapter.addFragment(HomeFragment.newInstance(true, false), getString(R.string.MyReports));
        adapter.addFragment(HomeFragment.newInstance(false, true), getString(R.string.Search), true);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final Fragment fragment;
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<Boolean> mFragmentSearchList = new ArrayList<>();

        Adapter(FragmentManager manager, Fragment fragment) {
            super(manager);
            this.fragment = fragment;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title, boolean search) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            mFragmentSearchList.add(search);
        }

        void addFragment(Fragment fragment, String title) {
            addFragment(fragment, title, false);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (mFragmentSearchList.get(position)) {
                Drawable d;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    d = fragment.getResources().getDrawable(R.drawable.ic_search_kitkat);
                } else {
                    d = fragment.getResources().getDrawable(R.drawable.ic_search_black_24dp);
                }
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                ImageSpan s = new ImageSpan(d, DynamicDrawableSpan.ALIGN_BOTTOM);
                SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append(" ");
                sb.setSpan(s, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return sb;
            }
            return mFragmentTitleList.get(position);
        }
    }
}
