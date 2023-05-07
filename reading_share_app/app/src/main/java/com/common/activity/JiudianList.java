package com.common.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blueberry.activity.R;
import com.bumptech.glide.Glide;
import com.common.bean.DuanziBean;
import com.common.util.HttpUtil;
import com.common.util.MyApp;
import com.common.util.Util;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class JiudianList extends Activity {
	@ViewInject(R.id.listView1)
	private ListView listView1;
	private List<DuanziBean> basemarkBeans = new ArrayList<DuanziBean>();
	@ViewInject(R.id.add_new_xd)
	private ImageView add_new_xd;
	@ViewInject(R.id.textView2)
	private TextView textView2;
	private MyApp myApp;
	private GameAdapter adapter;
	private int type;
	private String tag;
	private String jsonString;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		myApp = (MyApp) JiudianList.this.getApplication();
		setContentView(R.layout.jiudian_xd_list);
		ViewUtils.inject(this);
		if (TextUtils.isEmpty(myApp.getLoginName())) {
			add_new_xd.setVisibility(View.INVISIBLE);
		}
/*		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());*/
		adapter = new GameAdapter();
		listView1.setAdapter(adapter);
	/*	if(myApp.getName().equals("管理员")){
			add_new_xd.setVisibility(View.VISIBLE);
		}else{
			add_new_xd.setVisibility(View.GONE);

		}*/
	
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getGameXD();

	}

	@OnClick(R.id.top_back)
	public void back(View view) {
		finish();
	}

	@OnClick(R.id.add_new_xd)
	public void addNew(View view) {
		Util.toIntent(JiudianList.this, AddNews.class);
	}
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			if (msg.what == 0x126) {
				//
				ArrayList<DuanziBean> goods_list = DuanziBean
						.newInstanceList(jsonString);
				basemarkBeans.addAll(goods_list);
				adapter.notifyDataSetChanged();
			}
			if (msg.what == 0x127) {
				basemarkBeans.addAll(new ArrayList<DuanziBean>());
				adapter.notifyDataSetChanged();
			}
		};
	};
	public void getGameXD() {
		
		
		new Thread() {
			public void run() {
				try {
					
					String url = HttpUtil.URL_XINWENLIST;
					url = HttpUtil.URL_LIST3;
					textView2.setText("阅读精选");
					basemarkBeans=new ArrayList<DuanziBean>();


					String result = null;
					result = HttpUtil.queryStringConnectForPost(url);

							try {
								JSONObject obj = new JSONObject(result);
								String arrlist = obj.optString("jsonString");
								// JSONObject obj = new JSONObject(json);
								if (arrlist != "" && !arrlist.equals("arrlist")
										&& arrlist != null && !arrlist.equals("[]")) {
									jsonString =arrlist;
									
									
									
									handler.sendEmptyMessage(0x126);
								} else {
									handler.sendEmptyMessage(0x127);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}.start();
	}

	private class GameAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private ArrayList<DuanziBean> duanziList;

		private GameAdapter() {
			inflater = LayoutInflater.from(JiudianList.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return basemarkBeans.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return basemarkBeans.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("ResourceAsColor") @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder;
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.game_xd_list_item, null);
				holder = new Holder();
				holder.image_view = (CircleImageView) convertView
						.findViewById(R.id.image_view);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.time = (TextView) convertView.findViewById(R.id.times);
				holder.username = (TextView) convertView
						.findViewById(R.id.username);
				holder.status = (TextView) convertView.findViewById(R.id.status);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.time.setText("发布时间:"
					+ basemarkBeans.get(position).getUpdatetime());
			holder.username.setText("发布人:"
					+ basemarkBeans.get(position).getAuthor());
			holder.title
					.setText( basemarkBeans.get(position).getTitle());
			holder.status .setVisibility(View.GONE);
			if(basemarkBeans.get(position).getStatus2().equals("0")){
				holder.status .setText("未通过审核");
			}
			if(basemarkBeans.get(position).getStatus2().equals("1")){
				holder.status .setText("已通过审核");
				holder.status .setTextColor(R.color.green);
			}
			String imagename = basemarkBeans.get(position).getImage_url()
					.split("\\\\")[1];
//			MyBackAsynaTask asynaTask = new MyBackAsynaTask(
//					HttpUtil.URL_BASEUPLOAD + imagename, holder.image_view);
//			asynaTask.execute();

			Glide.with(JiudianList.this)
					.load(HttpUtil.URL_BASEUPLOAD + imagename)
					.into(holder.image_view);
			return convertView;
		}

		public ArrayList<DuanziBean> getDuanziList() {
			return duanziList;
		}

		public void setDuanziList(ArrayList<DuanziBean> duanziList) {
			this.duanziList = duanziList;
		}

	}

	private class Holder {
		CircleImageView image_view;
		TextView title;
		TextView time;
		TextView username;
		TextView status;
	}

	@OnItemClick(R.id.listView1)
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, InfoDetail.class);
		intent.putExtra("id", basemarkBeans.get(position).getId());
		intent.putExtra("tag", "阅读精选");
		startActivity(intent);
		
	}
}
