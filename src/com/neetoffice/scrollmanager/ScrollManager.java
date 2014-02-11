package com.neetoffice.scrollmanager;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

@SuppressLint("UseSparseArrays")
public class ScrollManager<T,V> {
	private static final String TAG = "ScrollManager";
	private HashMap<Integer, HashMap<Integer, SoftReference<T>>> datas = new HashMap<Integer, HashMap<Integer, SoftReference<T>>>();
	private HashMap<Integer, HashMap<Integer, V>> views = new HashMap<Integer, HashMap<Integer, V>>();

	private HashMap<String, LoadDataTask> tasks = new HashMap<String, LoadDataTask>();
	
	private Handler handler = new Handler();
	private CreateDataCallBack<T,V> createDataCallBack;
	private BaseAdapter adapter;
	
	private int firstVisibleItem;
	private int visibleItemCount;
	private int totalItemCount;
	private int scrollState = Listener.SCROLL_STATE_IDLE;
	
	public ScrollManager(BaseAdapter adapter, CreateDataCallBack<T,V> createDataCallBack){
		this.createDataCallBack = createDataCallBack;
		this.adapter = adapter;
	}
	

	protected void addTask(V v,int position,int index){		
		Log.d(TAG, "addTask position : "+position+",index : "+index);
		
		HashMap<Integer, V> map = views.get(position);
		if(map == null){
			map = new HashMap<Integer, V>();
			views.put(position, map);
		}
		map.put(index, v);		
		
		HashMap<Integer, SoftReference<T>> dmap = datas.get(position);
		if(dmap == null){
			dmap = new HashMap<Integer, SoftReference<T>>();
			datas.put(position, dmap);			
		}
		SoftReference<T> softReference = dmap.get(index);
		if(softReference == null || softReference.get() == null){			
			if(tasks.get(getKey(position, index)) == null){
				LoadDataTask task = new LoadDataTask(position,index,createDataCallBack);
				tasks.put(getKey(position, index), task);
				task.execute();
			}
			return;
		}
		createDataCallBack.callBack(v, softReference.get());
	}
	
	private class LoadDataTask{
		private int position;
		private int index;
		private CreateDataCallBack<T,V> callBack;
		private boolean isCancel = false;

		private LoadDataTask(int position,int index,CreateDataCallBack<T,V> callBack){
			this.position = position;
			this.index = index;
			this.callBack = callBack;
		}
		
		public void execute(){
			if(isCancel)return;
			HashMap<Integer, SoftReference<T>> dmap = datas.get(position);
			if(dmap != null){
				SoftReference<T> softReference = dmap.get(index);
				if(softReference != null && softReference.get() !=null){
					R3.run();
					return;
				}
			};
			new Thread(R2).start();
		}
		private Runnable R2 = new Runnable(){
			@Override
			public void run() {
				try{
					HashMap<Integer, SoftReference<T>>  map = datas.get(position);		
					if(map == null){
						createData();
					}else{
						SoftReference<T> softReference = map.get(index);
						if(softReference == null || softReference.get() == null){
							createData();
						}else{ 
							handler.post(R3);
						}					
					}
				}catch(RuntimeException e){
					handler.post(R3);
				}
			}
			
			private void createData(){
				if(isCancel)return;
				T t = callBack.onCreateData(position, index);
				HashMap<Integer, SoftReference<T>>  map = datas.get(position);
				if(map == null){
					map = new HashMap<Integer, SoftReference<T>>();
					datas.put(position, map);
				}
				map.put(index, new SoftReference<T>(t));
				handler.post(R3);
			}
		};
		
		private Runnable R3 = new Runnable(){
			@Override
			public void run() {
				tasks.remove(getKey(position, index));
				if(isCancel)return;
				/*
				HashMap<Integer, V> map = views.get(position);
				if(map == null)return;
				V v = map.get(index);
				if(v == null)return;
				
				HashMap<Integer, SoftReference<T>> dmap = datas.get(position);
				if(dmap == null)return;
				SoftReference<T> softReference = dmap.get(index);
				if(softReference == null || softReference.get() == null)return;
				callBack.callBack(v, softReference.get());*/
				Log.d(TAG, "firstVisibleItem : "+firstVisibleItem);
				Log.d(TAG, "firstVisibleItem+visibleItemCount : "+(firstVisibleItem+visibleItemCount));
				Log.d(TAG, "position : "+position);
				Log.d(TAG, "scrollState == SCROLL_STATE_IDLE : "+(scrollState == OnScrollListener.SCROLL_STATE_IDLE));
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && firstVisibleItem<=position && (firstVisibleItem+visibleItemCount)>=position){
					adapter.notifyDataSetChanged();
				}
			}		
		};

		public void cancel() {
			isCancel = true;
		}
	}	
	
	
	private String getKey(int position,int index){
		return "P"+position+"I"+index;
	}
	
	//------------------------------------------------------------------------------------------//

	
	private ScrollInterface scrollInterface;
	private Listener listener = new Listener();
	
	public void setScrollInterface(ScrollInterface scrollInterface) {
		this.scrollInterface = scrollInterface;
	}

	public OnScrollListener getOnScrollListener() {
		return listener;
	}

	private class Listener implements OnScrollListener {

		public void onScroll(AbsListView view, int f, int v, int t) {
			firstVisibleItem = f;
			visibleItemCount = v;
			totalItemCount = t;
		}

		public void onScrollStateChanged(AbsListView view, int s) {
			scrollState = s;
			switch (scrollState) {
			case SCROLL_STATE_FLING:
				Log.d(TAG, "SCROLL_STATE_FLING");
				clearTasks();
				break;
			case SCROLL_STATE_IDLE:
				Log.d(TAG, "SCROLL_STATE_IDLE");
				addTasks(firstVisibleItem,visibleItemCount,totalItemCount);
				break;
			case SCROLL_STATE_TOUCH_SCROLL:
				Log.d(TAG, "SCROLL_STATE_IDLE");
				clearTasks();
				break;
			}
			if (scrollInterface != null)scrollInterface.onScroll(view, scrollState, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}
	
	private void addTasks(int firstVisibleItem,int visibleItemCount,int totalItemCount){
		//HashMap<Integer,Boolean> temvmap = new HashMap<Integer,Boolean>();
		for(int i = 0; i < visibleItemCount; i++) {
			int position = firstVisibleItem+i;
			if(position <0)continue;
			if(position>=totalItemCount)continue;
			HashMap<Integer, V> map = views.get(position);
			if(map == null)continue;
			for(int index : map.keySet()){
				/*if(temvmap.get(map.get(index).hashCode())==null?false:true){
					Log.d(TAG,"hashCode 重複");
					Log.d(TAG,"position : "+position);
					Log.d(TAG,"index : "+index);
					continue;
				}
				temvmap.put(map.get(index).hashCode(),true);*/
				LoadDataTask task = new LoadDataTask(position ,index,createDataCallBack);
				tasks.put(getKey(position, index), task);
				task.execute();
			}
		}
	}

	private void clearTasks(){
		for(LoadDataTask task : tasks.values()){
			task.cancel();
		}
		tasks.clear();
	}


	public void clear() {
		views.clear();
		datas.clear();
	}
}
