package com.pkmmte.techdissected.util;

import android.text.format.DateUtils;

public class Utils {
	public static CharSequence getRelativeDate(long date) {
		return DateUtils.getRelativeTimeSpanString(date, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, 0);
	}
}