package com.example.authorize.weixin.entity.material;

import com.example.authorize.weixin.consts.MaterialConsts;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Haoyu
 */
public class DataRes {
	private ArrayList<PullData> list;
	/**
	 * createTime 接收端会自己创建，填0L即可
	 */
	private long ct;
	/**
	 * sourceId是 在PC端nosContent的sourceId，移动端呢？
	 */
	private String sourceId;
	/**
	 * 账号名称，origin 很明显是作者
	 */
	private String sourceName;
	/**
	 * type肯定是content 1
	 */
	private int type;
	/**
	 * nosContent的appid
	 */
	private int appId;
	/**
	 * collar需不需要自己抓取 填false
	 */
	private boolean collar;
	/**
	 * false 很明显不是 但是也用不到
	 */
	private boolean comment;
	/**
	 * 机器名，这个自己填就好了 比如pc
	 */
	private String machineId;
	/**
	 * bn？？？
	 */
	private int bn;
	/**
	 * 填0即可
	 */
	private int taskId;
	/**
	 * 密钥，虽然没啥用
	 */
	private String secretKey;

	/**
	 * 记得调用该方法时，添加secretKey和data List
	 * @param sourceId
	 * @param sourceName
	 */
	public DataRes(String sourceId, String sourceName){
		this.machineId= MaterialConsts.MACHINE_NAME;
		this.bn=0;
		this.taskId=0;
		this.comment=true;
		this.collar=false;
		this.appId=0;
		this.type=1;
		this.sourceName=sourceName;
		this.sourceId=sourceId;
		this.ct=0L;

	}

	public ArrayList<PullData> getList() {
		return list;
	}
	public void setList(ArrayList<PullData> list) {
		this.list = list;
	}
	public long getCt() {
		return ct;
	}
	public void setCt(long ct) {
		this.ct = ct;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getAppId() {
		return appId;
	}
	public void setAppId(int appId) {
		this.appId = appId;
	}
	public boolean isCollar() {
		return collar;
	}
	public void setCollar(boolean collar) {
		this.collar = collar;
	}
	public boolean isComment() {
		return comment;
	}
	public void setComment(boolean comment) {
		this.comment = comment;
	}
	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	public int getBn() {
		return bn;
	}
	public void setBn(int bn) {
		this.bn = bn;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	
}
