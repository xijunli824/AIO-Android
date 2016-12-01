package org.cmaaio.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 * 退出程序Dialog
 * @author lhy 2013/06/21
 *
 */
public class ConfirmDialog extends Dialog
{
	private Activity owner;
	
	public ConfirmDialog(Context context, int theme)
	{
		super(context, theme);
		owner = (context instanceof Activity) ? (Activity)context : null; 
	}
    
	@SuppressWarnings("deprecation")
	public void show()
	{
		super.show();
        if (owner != null)
        { 
    		WindowManager m = owner.getWindowManager();    
    		//为获取屏幕宽、高        
    		Display d = m.getDefaultDisplay();        
    		//获取对话框当前的参数值
    		WindowManager.LayoutParams p = getWindow().getAttributes();  
    		//宽度设置为屏幕的0.85  
    		p.width = (int) (d.getWidth() * 0.85);   
    		//设置生效  
    		getWindow().setAttributes(p);     
        } 
	}
}