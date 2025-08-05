package com.example.b07demosummer2024;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 *
 *Bryce Chen
 *
 * Fragment state adapater changed to display Question Fragments
 *
 *
 */
public class QuestionFSA extends FragmentStateAdapter {
QuestionView qView;
int  count;

    public QuestionFSA(@NonNull FragmentActivity fragmentActivity,QuestionView qView, int count) {
        super(fragmentActivity);
        this.count = count;
        this.qView = qView;
    }
    //creates a question
    @NonNull
    @Override
    public Fragment createFragment(int position) {

          return QuestionFrag.CreateQFrag(position);
    }
    // returns how many expected questions to display
    @Override
    public  int getItemCount() {
        return count;
    }
}
