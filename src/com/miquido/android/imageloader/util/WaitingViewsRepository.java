package com.miquido.android.imageloader.util;

import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

/**
* Created with IntelliJ IDEA.
* User: ljazgar
* Date: 17.08.12
* Time: 11:58
* To change this template use File | Settings | File Templates.
*/
public class WaitingViewsRepository<ImageKey,ViewType> {
    private final HashMap<ImageKey, WeakHashMap<ViewType, WeakReference<ViewType>>> imageKeysMap;
    private final WeakHashMap<ViewType, ImageKey> imageViewsMap;

    public WaitingViewsRepository() {
        this.imageKeysMap = new HashMap<ImageKey, WeakHashMap<ViewType, WeakReference<ViewType>>>();
        this.imageViewsMap = new WeakHashMap<ViewType, ImageKey>();
    }


    public synchronized void addExplicitely(ImageKey imageKey, ViewType imageView) {
        remove(imageView);

        WeakHashMap<ViewType, WeakReference<ViewType>> imageViewSet = imageKeysMap.get(imageKey);
        if (imageViewSet==null) {
            imageViewSet = new WeakHashMap<ViewType, WeakReference<ViewType>>();
            imageKeysMap.put(imageKey, imageViewSet);
        }
        if (!imageViewSet.containsKey(imageView))
            imageViewSet.put(imageView, new WeakReference<ViewType>(imageView));

        if (!imageViewsMap.containsKey(imageView)) {
            imageViewsMap.put(imageView, imageKey);
        }
    }

    public synchronized void remove(ViewType imageView) {
        ImageKey imageKey = imageViewsMap.get(imageView);
        if (imageKey==null)
            return;
        imageViewsMap.remove(imageView);

        WeakHashMap<ViewType, WeakReference<ViewType>> imageViewsSet =  imageKeysMap.get(imageKey);
        if (imageViewsSet==null)
            return;
        if (imageViewsSet.containsKey(imageView)) {
            imageViewsSet.remove(imageView);
        };
    }

    public synchronized List<ViewType> getImageViewsWaitingFor(ImageKey imageKey) {
        WeakHashMap<ViewType, WeakReference<ViewType>> imageViewSet = imageKeysMap.get(imageKey);
        List<ViewType> imageViews = new ArrayList<ViewType>();
        if (imageViewSet!=null)
            imageViews.addAll(imageViewSet.keySet());

        return imageViews;
    }

}
