package com.qf.qrcoderdemo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.qf.adapter.MyAdapter;
import com.qf.entity.ContactEntity;
import com.qf.util.JSONUtil;
import com.zxing.activity.CaptureActivity;
import com.zxing.util.QRcodeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

	/**
	 * 生成二维码scrolView集合
	 之前QRCoder只生成一个imgCoder
	 */
      private ContentResolver contentResolver;
		// 联系人的Uri
      	private Uri uri_query = Uri
      			.parse("content://com.android.contacts/contacts");
      	// 电话的Uri
      	private Uri uri_phone = Uri
      			.parse("content://com.android.contacts/data/phones");
      	// 邮件
//      	private Uri uri_email = Uri
//      			.parse("content://com.android.contacts/data/emails");

	//添加联系人
	private Uri uri_add = Uri.parse("content://com.android.contacts/raw_contacts");
	//添加联系人的数据表
	private Uri uri_add_data = Uri.parse("content://com.android.contacts/data");

      	private List<ContactEntity> datas;
      	private MyAdapter adapter;
      	private ListView listView;
      	private List<ContactEntity> CheckedDatas;
      	// 弹出窗
      	private PopupWindow myPopupWindow;
      	private MyDiaLog diaLog;

      	//显示选中条数的Button按钮
      	private Button button;

      	//设置二维码图片的字符串
      	private String str;

		//全选的Button按钮
		private Button btn_all;


//		private MakeQRcordImages makeQRcordImages;

		// diaLog中图片排列的线性布局
//		private LinearLayout imgLayout;


      	@Override
      	protected void onCreate(Bundle savedInstanceState) {
      		super.onCreate(savedInstanceState);
      		setContentView(R.layout.activity_main);

      		init();
      		getDatas();

      	}

      	/**
      	 * 初始化
      	 */
      	private void init() {

			btn_all = (Button) findViewById(R.id.choice_all);
			

			//弹出窗
			diaLog = new MyDiaLog(this);
			button = (Button) findViewById(R.id.btn_lookchecked);
			//选中的实体类集合
			CheckedDatas = new ArrayList<ContactEntity>();
			//所有的联系人
			datas = new ArrayList<ContactEntity>();
			adapter = new MyAdapter(this);
			listView = (ListView) findViewById(R.id.lv);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
			myPopupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT
					,ViewGroup.LayoutParams.WRAP_CONTENT);
//			myPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.lbs_popup_bg));
			myPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this,R.mipmap.lbs_popup_bg));
			myPopupWindow.setOutsideTouchable(true);
			View view = LayoutInflater.from(this).inflate(
					R.layout.layout_mypopuwindow, null);
			myPopupWindow.setContentView(view);

		}

	// 加载数据
	private void getDatas() {
		contentResolver = getContentResolver();
		Cursor cursor_query = contentResolver.query(uri_query, null, null,
				null, null);
		if (cursor_query != null && cursor_query.getCount() > 0) {
			while (cursor_query.moveToNext()) {
				ContactEntity entity = new ContactEntity();

				int id = cursor_query
						.getInt(cursor_query.getColumnIndex("_id"));
				String name = cursor_query.getString(cursor_query
						.getColumnIndex("display_name"));
				entity.setName(name);
				// 查询电话
				Cursor cursor_phone = contentResolver.query(uri_phone, null,
						"raw_contact_id = ?", new String[] { id + "" }, null);
				if (cursor_phone != null && cursor_phone.getCount() > 0) {
					while (cursor_phone.moveToNext()) {
						String phone = cursor_phone.getString(cursor_phone
								.getColumnIndex("data1"));
						entity.setPhone(phone);
						break;
					}
					cursor_phone.close();
				}

				// 查询邮件
//				Cursor cursor_email = contentResolver.query(uri_email, null,
//						"raw_contact_id = ?", new String[] { id + "" }, null);
//				if (cursor_email != null && cursor_email.getCount() > 0) {
//					while (cursor_email.moveToNext()) {
//						String email = cursor_email.getString(cursor_email
//								.getColumnIndex("data1"));
//					}
//					cursor_email.close();
//				}

				datas.add(entity);
			}
			cursor_query.close();
		}
		adapter.setDatas(datas);
	}

	/**
	 * 点击事件
	 *
	 * @param view
	 */
	public void doClick(View view) {
		switch (view.getId()) {
			case R.id.ac_popu:
				myPopupWindow.showAsDropDown(view, 0, 0, Gravity.BOTTOM);
				break;

			case R.id.choice_all:
				if (btn_all.getText().equals("全选")){
					adapter.setAllCheckBox(true);
					btn_all.setText("取消全选");
//
				}else {
					adapter.setAllCheckBox(false);
					btn_all.setText("全选");
				}

				break;

			case R.id.popu_add:
				//生成二维码
				myPopupWindow.dismiss();
				generateQRcode();//生成二维码
//				diaLog.shows();
				break;
			case R.id.popu_scan:
				//扫一扫
				myPopupWindow.dismiss();
				Intent intent = new Intent(this,CaptureActivity.class);
				startActivityForResult(intent, 0x001);

				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0x001 && resultCode == RESULT_OK){
			String result = data.getStringExtra("result");//json
//			Log.d("print", "扫码结果：" + result);
			if (result != null){
				try {
					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0; i < jsonArray.length() ; i++) {
						JSONObject ja = jsonArray.optJSONObject(i);
						String name = ja.optString("name");
						String phone = ja.optString("phone");
						insertContact(name,phone);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else {
				Toast.makeText(MainActivity.this, "扫描内容为空", Toast.LENGTH_SHORT).show();
			}


		}
	}

	/**
	 * 点击生成二维码时回调
	 */
	public void generateQRcode(){
		/**
		 * List<Person> isCheck = new ArrayList<Person>();
		 List<Person> becomeBitmap = new ArrayList<Person>();
		 Collection<Person> collection = myAdapter.getCheckedDatas();
		 for (Person p : collection) {
		 isCheck.add(p);
		 }
		 */
		Collection<ContactEntity> cdatas = adapter.getCheckedDatas();
		Iterator<ContactEntity> iterator =  cdatas.iterator();
		ContactEntity entity2 = null;
		int i = 0;
		CheckedDatas.removeAll(CheckedDatas);
		while(iterator.hasNext()){
			entity2 =  iterator.next();
			CheckedDatas.add(entity2);
			i++;
		}
		if(cdatas!=null){
			button.setText(i + "");
		}


		List<ContactEntity> toQRlist = new ArrayList<>();
		List<Bitmap> bitlist = new ArrayList<>();
		for (ContactEntity checkedData : CheckedDatas) {
			toQRlist.add(checkedData);
			if (toQRlist.size() %5 == 0 ){
				str = JSONUtil.getJSONByList(toQRlist);
				if (str != null) {
					Bitmap bmp = QRcodeUtil.btnGenRawCode(this,str);
					bitlist.add(bmp);
					toQRlist.clear();
					str = null;
				}

			}
		}

		Log.d("print", "setImage: bitlist的长度为" +bitlist.size());
		String strs = JSONUtil.getJSONByList(toQRlist);
		if (strs != null && !strs.equals("[]")) {
			Bitmap bmp2 = QRcodeUtil.btnGenRawCode(this,strs);
			bitlist.add(bmp2);
			Log.d("print", "setImage不为5整除的bmp2的长度为" +bitlist.size());
		}
		diaLog.setBitmaps(bitlist);
		diaLog.show();
		cdatas.clear();
		bitlist.clear();
		cdatas.clear();
		CheckedDatas.clear();

	}



	/**
	 * 自定义DiaLog设置二维码
	 */
//	@Override
//	public void setLayout(LinearLayout layout, View view) {
//		//临时的集合
//		List<ContactEntity> linshi = new ArrayList<ContactEntity>();
//		//	layout = (LinearLayout) view.findViewById(R.id.sl_ll);
//
//		layout.removeAllViews();
//		int num = checkedDatas.size();
//		//向线性布局中添加ImanView
//		//当联系人数小于五时
//		if(num<5){
//			ImageView imageView = new ImageView(this);
//
//			for(int y = 0; y < num; y++){
//				linshi.add(checkedDatas.get(y));
//			}
//			str = MyJSONUtil.getJSONByList(linshi);
//			if(str!=null){
//				Bitmap bitmap = QRcodeUtil.btnGenRawCode(this, str);
//				imageView.setImageBitmap(bitmap);
//			}
//			layout.addView(imageView);
//
//			linshi.clear();
//			str = "";
//		} else if(num%5 == 0 &&num!=0){			//数量刚好是5的整数倍时
//			layout.removeAllViews();
//			int tm = num / 5;
//			//创建多少个图片
//			for (int x = 0; x < tm ; x++) {
//				ImageView imageView = new ImageView(this);
//				layout.addView(imageView);
//			}
//
//			int index = 0;
//			ImageView imageView1;
//			for(int x = 0 ;x<num;x++){
//				linshi.add(checkedDatas.get(x));
//				if(x!=0 && (x+1)%5==0){
//					imageView1 = (ImageView) layout.getChildAt(index);
//					index++;
//					str = MyJSONUtil.getJSONByList(linshi);
//					if(str!=null && imageView1!=null){
//						Bitmap bitmap = QRcodeUtil.btnGenRawCode(this, str);
//						imageView1.setImageBitmap(bitmap);
//						linshi.clear();
//						str = "";
//					}
//				}
//			}
//
//			index = 0;
//		} else if(num%5 != 0){		//数量不是的整数倍时
//			layout.removeAllViews();
//			int tm = num / 5;
//			//创建多少个图片
//			for (int x = 0; x < (tm+1) ; x++) {
//				ImageView imageView = new ImageView(this);
//				layout.addView(imageView);
//			}
//
//			int index = 0;
//			ImageView imageView1;
//			for(int x = 0 ;x<num;x++){ //数量是整数倍时
//				linshi.add(checkedDatas.get(x));
//				if(x!=0 && (x+1)%5 == 0){
//					imageView1 = (ImageView) layout.getChildAt(index++);
//					str = MyJSONUtil.getJSONByList(linshi);
//					if(str!=null && imageView1!=null){
//						Bitmap bitmap = QRcodeUtil.btnGenRawCode(this, str);
//						imageView1.setImageBitmap(bitmap);
//						linshi.clear();
//						str = "";
//					}
//				}
//			}
//			index = 0;
//
//			int x = 5*tm;
//
//			int s = num-x;
//
//			if(s!=0){//数量不是整数倍
//				//获得线性布局中最后一个ImagView
//				ImageView imgLast = (ImageView) layout.getChildAt(layout.getChildCount()-1);
//
//				for(int y = 0; y < s; y++){
//					linshi.add(checkedDatas.get(y));
//				}
//				str = MyJSONUtil.getJSONByList(linshi);
//				if(str!=null){
//					Bitmap bitmap = QRcodeUtil.btnGenRawCode(this, str);
//					imgLast.setImageBitmap(bitmap);
//				}
//				linshi.clear();
//				str = "";
//			}
//		}
//		checkedDatas.clear();
//	}

//	@Override
//	public void setImage(LinearLayout ll) {
//		str = MyJSONUtil.getJSONByList(CheckedDatas);
//		Log.d("pri", "解析结果-----"+str);
//		if(str!=null){
//			Bitmap bitmap = QRcodeUtil.btnGenRawCode(this, str);
//			ll.setOrientation(LinearLayout.HORIZONTAL);
//
//			str = null;
//		}
//	}
//
//	@Override
//	public void setImage(ImageView img) {
//		str = MyJSONUtil.getJSONByList(CheckedDatas);
//		Log.d("pri", "解析结果-----"+str);
//		if(str!=null){
//			Bitmap bitmap = QRcodeUtil.btnGenRawCode(this, str);
//			img.setImageBitmap(bitmap);
//			str = null;
//		}

		// /每五个生成一个二维码

//	}





	//插入联系人的方法
	public void insertContact(String name, String phone){
		ContentValues values = new ContentValues();
		Uri resultUri = contentResolver.insert(uri_add, values);
		//获得插入记录的id
		long id = ContentUris.parseId(resultUri);

		//插入联系人的姓名
		values.clear();
		values.put("data1", name);
		values.put("raw_contact_id", id);
		values.put("mimetype", "vnd.android.cursor.item/name");//当前类型是联系人
		contentResolver.insert(uri_add_data, values);

		//插入电话号码
		values.clear();
		values.put("data1", phone);
		values.put("raw_contact_id", id);
		values.put("mimetype", "vnd.android.cursor.item/phone_v2");//当前类型是电话
		values.put("data2", 2);//默认是手机类型
		contentResolver.insert(uri_add_data, values);


		//插入邮箱
//		values.clear();
//		values.put("data1", email);
//		values.put("raw_contact_id", id);
//		values.put("mimetype", "vnd.android.cursor.item/email_v2");//当前类型是邮箱
//		values.put("data2", 1);//默认是个人邮箱
//		contentResolver.insert(uri_add_data, values);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CheckBox box = (CheckBox) view.findViewById(R.id.cb);
		boolean ischeck = box.isChecked();

		if (ischeck) {
			box.setChecked(false);
			adapter.notifyDataSetChanged();
		} else {
			box.setChecked(true);
			adapter.notifyDataSetChanged();
		}
	}


	//
//	public interface MakeQRcordImages {
//		void makeimages();
//	}
}


