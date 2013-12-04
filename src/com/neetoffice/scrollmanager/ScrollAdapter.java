package com.neetoffice.scrollmanager;

import java.lang.ref.SoftReference;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

public abstract class ScrollAdapter extends BaseAdapter  implements CreateInterface<Drawable>,ScrollInterface{
	private ScrollManager<Drawable> scrollManager= new ScrollManager<Drawable>(this,this,this);
		
	public OnScrollListener getOnScrollListener(){
		return scrollManager.getOnScrollListener();
	}
	
	public void onScrollStateIdle(int position){
		scrollManager.onScrollStateIdle(position);
	}
	
	protected void setCreateFristData(int limt){
		scrollManager.setCreateFristData(limt);
	}
	
	@SuppressWarnings("unchecked")
	protected SoftReference<Drawable>[] getDatas(int position){
		List<SoftReference<Drawable>> list = scrollManager.getItem(position);		
		return list.toArray(new SoftReference[list.size()]);
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
