package com.neetoffice.imagescrollmanager;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

@SuppressLint("UseSparseArrays")
public class AdapterScrollManager<T> {
	/**The user had previously been scrolling using touch and had performed a fling. The animation is now coasting to a stop.*/
	public final static int SCROLL_STATE_FLING = OnScrollListener.SCROLL_STATE_FLING;
	/**The view is not scrolling. Note navigating the list using the trackball counts as being in the idle state since these transitions are not animated.*/
	public final static int SCROLL_STATE_IDLE = OnScrollListener.SCROLL_STATE_IDLE;
	/**The user is scrolling using touch, and their finger is still on the screen.*/
	public final static int SCROLL_STATE_TOUCH_SCROLL = OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
	private BaseAdapter adapter;
	private CreateImageInterface<T> imageScrollInterface;
	private ScrollInterface scrollInterface;
	private HashMap<Integer,T[]> drawableMap = new HashMap<Integer,T[]>();
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private int firstVisibleItem = 0;
	private int visibleItemCount = 0;
	private int totalItemCount = 0;
	private Handler handler = new Handler();
	private Listener listener;
	
	public AdapterScrollManager(BaseAdapter adapter, CreateImageInterface<T> imageScrollInterface,ScrollInterface scrollInterface){
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
	
	public T[] getDrawable(int position){
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
				T[] drawables = imageScrollInterface.onCreateImage(position);
				handler.post(new TaskRunnable(position,drawables));
			}
		}
	}
	
	private class TaskRunnable implements Runnable{
		private int position;
		private T[] drawables;
		TaskRunnable(int position,T[] drawables){
			this.position = position;
			this.drawables = drawables;
		}
		public void run() {
			drawableMap.put(position,drawables);
			if(adapter != null)adapter.notifyDataSetChanged();
		}		
	}
	
	private class Listener implements OnScrollListener{



		public void onScroll(AbsListView view, int f, int v, int t) {
			firstVisibleItem = f;
			visibleItemCount = v;
			totalItemCount = t;		
		}

		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case SCROLL_STATE_FLING:
				clearTasks();
				break;
			case SCROLL_STATE_IDLE:
				HashMap<Integer,T[]> drawables = new HashMap<Integer,T[]>();
				for(Integer key:drawableMap.keySet()){
					if(key>firstVisibleItem && key <(firstVisibleItem+visibleItemCount)){
						drawables.put(key, drawableMap.get(key));
					}
				}
				drawableMap.clear();
				drawableMap.putAll(drawables);
				if (firstVisibleItem + visibleItemCount >= totalItemCount) {
					//imageScrollInterface.onScrollend(view, scrollState);
				}else{
					for(int i = 0; i < visibleItemCount; i++) {
						Task task = new Task(firstVisibleItem+i);
						tasks.add(task);
						task.start();
					}
				}			
				break;
			case SCROLL_STATE_TOUCH_SCROLL:
				clearTasks();
				break;
			}
			if(scrollInterface != null)scrollInterface.onScroll(view, scrollState, firstVisibleItem, visibleItemCount, totalItemCount);
		}		
		
	}

}
