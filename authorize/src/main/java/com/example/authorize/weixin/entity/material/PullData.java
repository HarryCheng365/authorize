package com.example.authorize.weixin.entity.material;

public class PullData {
    /**
     * 有一个media_id，但是后面好像用不到，设为""即可
     */
    private String id;
    /**
     * 文章title，可以有
     */
    private String title;
    /**
     * 这个肯定要有，对于需要抓取的就是图文url，对于不需要抓取的就是nos的url
     */
    private String url;
    /**
     * 填0？不知道文章发布时间，只有url，填OL即可，如果不知道发布时间
     */
    private long pubTime;
    /**
     * nos的内容
     */
    private String content;
    /**
     * 设为null即可
     */
    private String read_num;
    private String like_num;
    /**
     * nos的key 所以需要抓取的不用管
     */
    private String storeid;

    public PullData(String id,String title,String url,long pubTime){
        this.id=id;
        this.title=title;
        this.url=url;
        this.content="";
        this.read_num="";
        this.like_num="";
        this.storeid="";
        this.pubTime=pubTime;


    }
    public String getStoreid() {
        return storeid;
    }
    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public long getPubTime() {
        return pubTime;
    }
    public void setPubTime(long pubTime) {
        this.pubTime = pubTime;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getRead_num() {
        return read_num;
    }
    public void setRead_num(String read_num) {
        this.read_num = read_num;
    }
    public String getLike_num() {
        return like_num;
    }
    public void setLike_num(String like_num) {
        this.like_num = like_num;
    }



}

