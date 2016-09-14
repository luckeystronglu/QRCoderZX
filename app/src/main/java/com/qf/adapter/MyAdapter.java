package com.qf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.qf.entity.ContactEntity;
import com.qf.qrcoderdemo.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter implements OnCheckedChangeListener{


	private List<ContactEntity> datas;
	private Context context;
	private Map<Integer, ContactEntity> cacheMap;//缓存当前checkbox的选中状态

	public MyAdapter(Context context) {
		super();
		this.context = context;
		this.datas = new ArrayList<ContactEntity>();
		this.cacheMap = new HashMap<Integer, ContactEntity>();
	}
	
	public void  setDatas(List<ContactEntity> datas){
		this.datas = datas;
		this.notifyDataSetChanged();
	}
	
//	public void addDatas(List<ContactEntity> datas){
//		datas.addAll(datas);
//		this.notifyDataSetChanged();
//	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView!=null){
			holder = (ViewHolder) convertView.getTag();
		}else {
			holder = new ViewHolder();
			convertView  = LayoutInflater.from(context).inflate(R.layout.lv_item_layout, null);
			holder.tv1 = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv2 = (TextView) convertView.findViewById(R.id.tv_phone);
			holder.box = (CheckBox) convertView.findViewById(R.id.cb);
			//设置checkbox的监听
			holder.box.setOnCheckedChangeListener(this);
			convertView.setTag(holder);
		}
		//得到checkbox对象 -- 保证当前的checkbox里的tag是当前所在的item的下标
		holder.box.setTag(position);
		
		holder.tv1.setText(datas.get(position).getName());
		holder.tv2.setText(datas.get(position).getPhone());
		//给checkbox状态复制
		holder.box.setChecked(cacheMap.containsKey(position));
		return convertView;
	}
	
	class ViewHolder{
		TextView tv1,tv2;
		CheckBox box;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int position = (Integer) buttonView.getTag();
		//当checkbox被选中时，把相应的下标和数据缓存进map中
		if(isChecked){
			cacheMap.put(position, datas.get(position));

//			if (cacheMap.size() < 5){
//				cacheMap.put(position, datas.get(position));
//			}else {
//				cacheMap.remove(position);
//				buttonView.setChecked(false);
//				Toast.makeText(context, "不能选择超过5个联系人", Toast.LENGTH_SHORT).show();
//			}

		} else {
			//如果取消选中，则从map中移除该数据
			cacheMap.remove(position);
		}
	}
	
	/**
	 * 返回被选中的数据
	 * @return
	 */
	public Collection<ContactEntity> getCheckedDatas(){
		Collection<ContactEntity> collection = cacheMap.values();
		return collection;
	}


	public void setAllCheckBox(Boolean bool){
		if (bool){
			for (int i = 0; i < datas.size(); i++) {
				cacheMap.put(i,datas.get(i));
			}
			this.notifyDataSetChanged();
		}else {
			cacheMap.clear();
			this.notifyDataSetChanged();
		}

	}






}
