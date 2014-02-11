package com.neetoffice.scrollmanager;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

@SuppressLint("UseSparseArrays")
public abstract class OldScrollAdapter extends BaseAdapter  implements CreateInterface<Drawable>,ScrollInterface{
	private OldScrollManager<Drawable> scrollManager= new OldScrollManager<Drawable>(this,this,this);
	private HashMap<Integer,HashMap<Integer,ImageView>> ImageViewMap = new HashMap<Integer,HashMap<Integer,ImageView>>();
	
		
	public OnScrollListener getOnScrollListener(){
		return scrollManager.getOnScrollListener();
	}
	
	public void setRage(int rage){
		scrollManager.setRage(rage);
	}
	
	public void onScrollStateIdle(int position){
		scrollManager.onScrollStateIdle(position);
	}
	
	protected void setCreateFristData(int limt){
		scrollManager.setCreateFristData(limt);
	}
	
	protected Drawable[] getDatas(int position){
		Drawable[] list = scrollManager.getItem(position);
		return list;		
	}

	@Override
	public void onScroll(AbsListView view, int scrollState,	int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (scrollState == OldScrollManager.SCROLL_STATE_IDLE & (firstVisibleItem + visibleItemCount+1 >= totalItemCount)) {
			onScrollend(view);
		}
	}
	public void clear(){
		scrollManager.clear();
	}
	
	protected abstract void onScrollend(AbsListView view);
	
}
