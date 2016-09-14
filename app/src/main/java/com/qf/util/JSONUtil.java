package com.qf.util;
import android.text.TextUtils;

import com.qf.entity.ContactEntity;

import java.util.List;


public class JSONUtil {

	/**
	 * ���ʵ���෵��JSON
	 * @param datas
	 * @return
	 */
	public static String getJSONByList(List<ContactEntity> datas){
		StringBuilder stringBuilder = new StringBuilder();
		if(datas != null && datas.size() > 0){
			stringBuilder.append("[");
			for(int i = 0; i < datas.size(); i++){
				if(i != 0){
					stringBuilder.append(",");
				}
				//{"name":"Lucy", "phone":"182316821"}
				//ƴ������
				stringBuilder.append("{");
				stringBuilder.append("\"name\":");
				stringBuilder.append("\"" + datas.get(i).getName() + "\"");
				
				//ƴ�ӵ绰
				if(!TextUtils.isEmpty(datas.get(i).getPhone())){
					stringBuilder.append(",");
					stringBuilder.append("\"phone\":");
					stringBuilder.append("\"" + datas.get(i).getPhone() + "\"");
				}
				
				stringBuilder.append("}");
			}
			stringBuilder.append("]");
			
			return stringBuilder.toString();
		}
		return "[]";
	}
}
