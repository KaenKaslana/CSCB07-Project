package com.example.b07demosummer2024;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * Abstract class that all fragments that provide and answer will implement
 *
 */

public abstract class QAnswerFrag extends Fragment {

    /**
     * A method that is intended for a higher class to get answers from this fragment
     * @return list of answer(s)
     */
    public abstract ArrayList<String> NotifyListener();


}
