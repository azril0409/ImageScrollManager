package com.neetoffice.imagescrollmanager;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

@SuppressLint("UseSparseArrays")
public class BitmapScrollManager {
	/**The user had previously been scrolling using touch and had performed a fling. The animation is now coasting to a stop.*/
	public final static int SCROLL_STATE_FLING = OnScrollListener.SCROLL_STATE_FLING;
	/**The view is not scrolling. Note navigating the list using the trackball counts as being in the idle state since these transitions are not animated.*/
	public final static int SCROLL_STATE_IDLE = OnScrollListener.SCROLL_STATE_IDLE;
	/**The user is scrolling using touch, and their finger is still on the screen.*/
	public final static int SCROLL_STATE_TOUCH_SCROLL = OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
	private BaseAdapter adapter;
	private CreateImageInterface imageScrollInterface;
	private ScrollInterface scrollInterface;
	private HashMap<Integer,Bitmap[]> drawableMap = new HashMap<Integer,Bitmap[]>();
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private int firstVisibleItem = 0;
	private int visibleItemCount = 0;
	private int totalItemCount = 0;
	private Handler handler = new Handler();
	private Listener listener;
	private int rage = 1;
	
	public BitmapScrollManager(BaseAdapter adapter, CreateImageInterface imageScrollInterface,ScrollInterface scrollInterface){
		this.adapter = adapter;
		this.imageScrollInterface = imageScrollInterface;
		this.scrollInterface = scrollInterface;
		listener = new Listener();
	}
	
	public OnScrollListener getOnScrollListener(){
		return listener;
	}
	
	private void clearTasks(){
		for(Task task:tasks){
			task.cancel();
		}
		tasks.clear();		
	}
	
	public void setCreateFristImage(int limt){
		firstVisibleItem = 0;
		visibleItemCount = limt;

		for(int i = 0; i < visibleItemCount; i++) {
			Task task = new Task(firstVisibleItem+i);
			tasks.add(task);
			task.start();
		}
	}
	
	public Bitmap[] getBitmap(int position){
		return drawableMap.get(position);
	}
	
	private class Task extends Thread{
		private int position;
		private boolean isCancel = false;
		private Task(int position){
			this.position = position;
		}
		public void cancel(){
			isCancel = true;
		}
		@Override
		public void run() {
			if(!isCancel && imageScrollInterface != null){
				Bitmap[] t = null;
				if(drawableMap.get(position) == null){
					Log.e("", "drawableMap.get(position) == null");
					t = imageScrollInterface.onCreateImage(position);
				};
				handler.post(new TaskRunnable(position,t));
			}
		}
	}
	
	private class TaskRunnable implements Runnable{
		private int position;
		private Bitmap[] t;
		TaskRunnable(int position,Bitmap[] t){
			this.position = position;
			this.t = t;
		}
		public void run() {
			if(t != null){
				drawableMap.put(position,t);
			}
			if(adapter != null && listener.scrollState == SCROLL_STATE_IDLE){
				adapter.notifyDataSetChanged();
			}
		}		
	}
	
	private class Listener implements OnScrollListener{
		int scrollState = SCROLL_STATE_IDLE;

		public void onScroll(AbsListView view, int f, int v, int t) {
			firstVisibleItem = f;
			visibleItemCount = v;
			totalItemCount = t;		
		}

		public void onScrollStateChanged(AbsListView view, int scrollState) {
			this.scrollState = scrollState;
			switch (scrollState) {
			case SCROLL_STATE_FLING:
				clearTasks();
				break;
			case SCROLL_STATE_IDLE:
				for(int i = -rage; i < visibleItemCount+rage; i++) {
					int index = firstVisibleItem+i;
					if(index <0)index = 0;
					if(index>totalItemCount)index = index-1;
					onScrollStateIdle(index);
				}
							
				break;
			case SCROLL_STATE_TOUCH_SCROLL:
				clearTasks();
				break;
			}
			if(scrollInterface != null)scrollInterface.onScroll(view, scrollState, firstVisibleItem, visibleItemCount, totalItemCount);
		}		
	}
	
	public void onScrollStateIdle(int position){
		Task task = new Task(position);
		tasks.add(task);
		task.start();
	}
	
	public void clear(){
		for(Bitmap[] bitmaps : drawableMap.values()){
			for(Bitmap bitmap:bitmaps){
				bitmap.recycle();
			}
		}
		clearTasks();		
	}
}
