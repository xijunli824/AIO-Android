package org.cmaaio.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.util.Log;

public class ActivityManager {
	private List<Activity> activityList = new LinkedList<Activity>();
    private static ActivityManager instance;
 
    private ActivityManager() {
    	
    }
 
    // 单例模式中获取唯一的ActivityManager实例
    public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }
 
    // 添加Activity到容器中
	public void addActivity(Activity activity) {
//		boolean flg = false;
//		String cn = activity.getClass().getName();
//		for (Activity act : activityList) {
//			String className = act.getClass().getName();
//			if (className.equals(cn)) {				
//				flg = true;
//				break;
//			}
//		}
//		if (!flg) {
			activityList.add(activity);	
//		}

	}
 
    // 遍历所有Activity并finish 
    public void exit() {
        for (Activity activity : activityList) {
        	if (activity != null) {
        		Log.e("Tag", activity.getClass().getName() + "");
        		activity.finish();
        	}
        }
        activityList.clear();
    }
}
