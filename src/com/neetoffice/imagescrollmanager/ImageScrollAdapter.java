package com.neetoffice.imagescrollmanager;

import java.lang.ref.SoftReference;
import java.util.List;

import android.graphics.Bitmap;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

public abstract class ImageScrollAdapter extends BaseAdapter  implements CreateInterface<Bitmap>,ScrollInterface{
	private ScrollManager<Bitmap> scrollManager= new ScrollManager<Bitmap>(this,this,this);
		
	public OnScrollListener getOnScrollListener(){
		return scrollManager.getOnScrollListener();
	}
	
	public void onScrollStateIdle(int position){
		scrollManager.onScrollStateIdle(position);
	}
	
	protected void setCreateFristImage(int limt){
		scrollManager.setCreateFristImage(limt);
	}
	
	protected Bitmap[] getBitmap(int position){
		List<SoftReference<Bitmap>> list = scrollManager.getItem(position);
		if(list == null || list.size() < 1)return null;
		Bitmap[] bitmaps = new Bitmap[list.size()];
		for(int i=0;i<list.size();i++){
			bitmaps[i] = list.get(i).get();
		}
		return bitmaps;
	}

	@Override
	public void onScroll(AbsListView view, int scrollState,	int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (scrollState == ScrollManager.SCROLL_STATE_IDLE & (firstVisibleItem + visibleItemCount >= totalItemCount)) {
			onScrollend(view);
		}
	}
	public void clear(){
		scrollManager.clear();
	}
	
	protected abstract void onScrollend(AbsListView view);
}
