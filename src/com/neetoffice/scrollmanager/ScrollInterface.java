package com.neetoffice.scrollmanager;

import android.widget.AbsListView;

public interface ScrollInterface {

	void onScroll(AbsListView view, int scrollState,int firstVisibleItem, int visibleItemCount, int totalItemCount);
}
