package com.pkmmte.techdissected.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pkmmte.techdissected.R;

public class AboutFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_about, container, false);
		//initViews(view);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}
}