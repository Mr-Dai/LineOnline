package com.playfun.lineonline.widget;

import com.playfun.lineonline.MainActivity;
import com.playfun.lineonline.R;
import com.playfun.lineonline.SignUpActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

/**
 *
 * 基类Activity，用于实现大部分Activity共有的功能，包括：<br />
 * 唤出菜单、退出登录、关闭程序
 *
 * @author Robert Peng
 */
public abstract class BaseActivity extends Activity {
    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            finish();  
        }  
    };  
      
    @Override  
    public void onResume() {  
        super.onResume();  
        // 在当前的activity中注册广播  
        IntentFilter filter = new IntentFilter();  
        filter.addAction("ExitApp");  
        filter.addAction("Logout");
        this.registerReceiver(this.broadcastReceiver, filter); 
    }  
      
    @Override  
    protected void onDestroy() {
        super.onDestroy();  
        this.unregisterReceiver(this.broadcastReceiver);    
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 按下菜单键，唤出菜单
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.logout) {
			// 关闭自动登录
			SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("autoLogIn", false);
			editor.commit();
			
			// 启动登录页面
			startActivity(new Intent(BaseActivity.this, SignUpActivity.class));
			
            // 发送广播，关闭其余Activity
			Intent mIntent = new Intent("Logout");
            sendBroadcast(mIntent);  
			
			return true;
		} else if (id == R.id.exit) {
			// 发送广播，关闭所有Activity
			Intent mIntent = new Intent("ExitApp");
            sendBroadcast(mIntent);  
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}  