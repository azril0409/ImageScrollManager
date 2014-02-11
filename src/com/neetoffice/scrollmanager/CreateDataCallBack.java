package com.neetoffice.scrollmanager;

import java.util.List;


public interface CreateDataCallBack<T,V> {

	T onCreateData(int position,int index);
	void callBack(V view,T data);
}
