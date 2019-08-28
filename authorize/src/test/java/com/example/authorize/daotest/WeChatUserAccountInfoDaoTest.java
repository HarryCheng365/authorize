package com.example.authorize.daotest;

import com.example.authorize.weixin.dao.WeChatUserAccessTokenDao;
import com.example.authorize.weixin.dao.WeChatUserAccountInfoDao;
import com.example.authorize.weixin.entity.AuthorizeAccountInfoMsg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeChatUserAccountInfoDaoTest {
    @Autowired
    WeChatUserAccountInfoDao weChatUserAccountInfoDao;

    @Test
    public void saveuser(){
        AuthorizeAccountInfoMsg authorizeAccountInfoMsg = new AuthorizeAccountInfoMsg("1","4","1","1","1",
                "1",1,1,"1","1","1","1");
        //weChatUserAccountInfoDao.insertRecord(authorizeAccountInfoMsg);
        authorizeAccountInfoMsg.setAlias("3");
        weChatUserAccountInfoDao.updateRecord(authorizeAccountInfoMsg);




    }
}
