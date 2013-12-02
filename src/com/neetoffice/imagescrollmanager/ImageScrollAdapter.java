package com.neetoffice.imagescrollmanager;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

public abstract class ImageScrollAdapter extends BaseAdapter  implements CreateImageInterface,ScrollInterface{
	private BitmapScrollManager imageScrollManager= new BitmapScrollManager(this,this,this);
		
	public OnScrollListener getOnScrollListener(){
		return imageScrollManager.getOnScrollListener();
	}
	
	public void onScrollStateIdle(int position){
		imageScrollManager.onScrollStateIdle(position);
	}
	
	protected void setCreateFristImage(int limt){
		imageScrollManager.setCreateFristImage(limt);
	}
	
	protected Bitmap[] getBitmap(int position){
		return imageScrollManager.getBitmap(position);
	}

	@Override
	public void onScroll(AbsListView view, int scrollState,	int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (scrollState == BitmapScrollManager.SCROLL_STATE_IDLE & (firstVisibleItem + visibleItemCount >= totalItemCount)) {
			onScrollend(view);
		}
	}
	public void clear(){
		imageScrollManager.clear();
	}
	
	protected abstract void onScrollend(AbsListView view);
}
