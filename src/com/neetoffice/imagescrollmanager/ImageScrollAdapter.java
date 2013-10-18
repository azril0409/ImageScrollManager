package com.neetoffice.imagescrollmanager;

import android.graphics.drawable.Drawable;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

public abstract class ImageScrollAdapter extends BaseAdapter  implements CreateImageInterface,ScrollInterface{
	private ImageScrollManager imageScrollManager= new ImageScrollManager(this,this,this);
		
	public OnScrollListener getOnScrollListener(){
		return imageScrollManager.getOnScrollListener();
	}
	
	protected void setCreateFristImage(int limt){
		imageScrollManager.setCreateFristImage(limt);
	}
	
	protected Drawable[] getDrawable(int position){
		return imageScrollManager.getDrawable(position);
	}

	@Override
	public void onScroll(AbsListView view, int scrollState,	int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (scrollState == ImageScrollManager.SCROLL_STATE_IDLE && (firstVisibleItem + visibleItemCount >= totalItemCount)) {
			onScrollend(view);
		}
	}
	
	protected abstract void onScrollend(AbsListView view);
}
