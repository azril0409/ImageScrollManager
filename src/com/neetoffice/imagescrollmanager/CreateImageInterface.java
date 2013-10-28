package com.neetoffice.imagescrollmanager;

import android.graphics.drawable.Drawable;

public interface CreateImageInterface<T> {

	T[] onCreateImage(int position);
}
