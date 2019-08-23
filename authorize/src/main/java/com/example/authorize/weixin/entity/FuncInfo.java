package com.example.authorize.weixin.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class FuncInfo {

	private  FuncscopeCategory funcscope_category;



	public FuncscopeCategory getFuncscope_category() {
		return funcscope_category;
	}



	public void setFuncscope_category(FuncscopeCategory funcscope_category) {
		this.funcscope_category = funcscope_category;
	}



	public static class FuncscopeCategory{
		private Integer id;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		@Override
		public String toString(){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("funcscopeCategory",id);
			return jsonObject.toJSONString();
		}
	}
}

