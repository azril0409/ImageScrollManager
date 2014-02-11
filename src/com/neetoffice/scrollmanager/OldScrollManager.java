package com.neetoffice.scrollmanager;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

@SuppressLint("UseSparseArrays")
public class OldScrollManager<T> {
	/**The user had previously been scrolling using touch and had performed a fling. The animation is now coasting to a stop.*/
	public final static int SCROLL_STATE_FLING = OnScrollListener.SCROLL_STATE_FLING;
	/**The view is not scrolling. Note navigating the list using the trackball counts as being in the idle state since these transitions are not animated.*/
	public final static int SCROLL_STATE_IDLE = OnScrollListener.SCROLL_STATE_IDLE;
	/**The user is scrolling using touch, and their finger is still on the screen.*/
	public final static int SCROLL_STATE_TOUCH_SCROLL = OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
	private CreateInterface<T> imageScrollInterface;
	private ScrollInterface scrollInterface;
	private HashMap<Integer,T[]> tMap = new HashMap<Integer,T[]>();
	
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private int firstVisibleItem = 0;
	private int visibleItemCount = 0;
	private int totalItemCount = 0;
	private Handler handler = new Handler();
	private Listener listener;
	private TaskRunnable taskRunnable;
	private int rage = 1;
	
	public OldScrollManager(BaseAdapter adapter, CreateInterface imageScrollInterface,ScrollInterface scrollInterface){
		this.imageScrollInterface = imageScrollInterface;
		this.scrollInterface = scrollInterface;
		listener = new Listener();
		taskRunnable  = new TaskRunnable(adapter);
	}
	
	public void setRage(int rage){
		this.rage = rage;
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
	
	public void setCreateFristData(int limt){
		firstVisibleItem = 0;
		visibleItemCount = limt;

		for(int i = 0; i < visibleItemCount; i++) {
			Task task = new Task(firstVisibleItem+i);
			tasks.add(task);
			task.start();
		}
	}
	
	public T[] getItem(int position){
		return tMap.get(position);
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
				T[] ts = null;
				if(tMap.get(position) == null){
					ts = imageScrollInterface.onCreateDatas(position);
				};
				tMap.put(position,ts);	
				handler.post(taskRunnable);
			}
		}
	}
	
	private class TaskRunnable implements Runnable{
		private BaseAdapter adapter;
		private TaskRunnable(BaseAdapter adapter){
			this.adapter = adapter;
		}
		public void run() {		
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
				claer(0,firstVisibleItem-rage);
				claer(firstVisibleItem+visibleItemCount+rage,totalItemCount);
				for(int i = -rage; i < visibleItemCount+rage; i++) {
					int index = firstVisibleItem+i;
					if(index <0)continue;
					if(index>=totalItemCount)continue;
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
	private void claer(int start ,int end){
		for(int i=0;i<end;i++){
			tMap.remove(i);
		}
	}
	
	public void onScrollStateIdle(int position){
		Task task = new Task(position);
		tasks.add(task);
		task.start();
	}
	
	public void clear(){
		tMap.clear();
		clearTasks();		
	}
}
