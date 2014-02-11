package com.neetoffice.scrollmanager;

import android.graphics.drawable.Drawable;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public abstract class ScrollAdapter extends BaseAdapter  implements CreateDataCallBack<Drawable,ImageView>,ScrollInterface{
	private ScrollManager<Drawable,ImageView> scrollManager = new ScrollManager<Drawable,ImageView>(this,this);
	
	public OnScrollListener getOnScrollListener(){
		scrollManager.setScrollInterface(this);
		return scrollManager.getOnScrollListener();
	}
	
	protected void addTask(ImageView imageview,int position,int index){
		scrollManager.addTask(imageview, position, index);
	}
	@Override
	public void onScroll(AbsListView view, int scrollState,	int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (scrollState == OldScrollManager.SCROLL_STATE_IDLE & (firstVisibleItem + visibleItemCount+1 >= totalItemCount)) {
			onScrollend(view);
		}
	}
	
	public void clear() {
		scrollManager.clear();
	}
	
	protected abstract void onScrollend(AbsListView view);
}
