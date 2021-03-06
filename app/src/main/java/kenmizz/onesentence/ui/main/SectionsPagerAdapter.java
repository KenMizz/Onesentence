package kenmizz.onesentence.ui.main;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import kenmizz.onesentence.MainActivity;
import kenmizz.onesentence.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.sentence, R.string.sentence_list};
    private final MainActivity mActivity;

    public SectionsPagerAdapter(MainActivity mainActivity, FragmentManager fm) {
        super(fm);
        mActivity = mainActivity;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return SentenceListFragment.newInstance(mActivity.getSentenceCollection(), mActivity.getSentencesList());
        }
        return SentenceFragment.newInstance(mActivity.getSentencesList());
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mActivity.getApplicationContext().getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}