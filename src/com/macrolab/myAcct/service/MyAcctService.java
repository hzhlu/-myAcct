package com.macrolab.myAcct.service;

import com.macrolab.myAcct.model.TMyAcct;
import com.macrolab.myAcct.util.Base64;
import com.macrolab.myAcct.util.DateUtil;
import com.macrolab.myAcct.util.MyTools;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyAcctService {

    DBService dbService;

    // 注入dbService；
    public void setDbService(DBService dbService) {
        this.dbService = dbService;
    }

    /**
     * 在相同的key_verify_code的记录中搜索 name和content
     *
     * @param name
     */
    public List<TMyAcct> queryMyAcct(String name, String keyVerifyCode) {
        List<TMyAcct> result = new ArrayList<>();

        // 查询所有秘钥校验码相同的记录
        result = dbService.query("and name like '%" + name + "%' ");

        // 对秘钥校验码不同的记录返回资料名称，不反资料内容

        return result;
    }

    /**
     * 解密资料
     *
     * @param myAcct
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     */
    public String decodeContent(TMyAcct myAcct, String key) throws Exception {
        String codeContent = myAcct.getContent();
        String salt = myAcct.getSalt();
        if (keyVerifyCode(key).equals(myAcct.getKeyVerifyCode())) {
            String content = new String(MyTools.decryptAES(Base64.decode(codeContent.getBytes()), MyTools.md5(key + salt)), "UTF-8");
            return content;
        } else {
            return null;
        }
    }

    /**
     * 加密资料内容
     *
     * @param content
     * @param key
     * @param salt
     * @return
     * @throws UnsupportedEncodingException
     */
    private String encodeContent(String content, String key, String salt) throws UnsupportedEncodingException {
        return new String(Base64.encode(MyTools.encryptAES(content.getBytes("UTF-8"), MyTools.md5(key + salt))));
    }

    public TMyAcct saveMyAcct(TMyAcct myAcct, String key) throws UnsupportedEncodingException {
        String salt = MyTools.getStringPassword(16);
        myAcct.setUpdateDate(DateUtil.getTime(new Date()));
        myAcct.setMac(contentMAC(myAcct));
        myAcct.setKeyVerifyCode(keyVerifyCode(key));
        myAcct.setSalt(salt);
        myAcct.setContent(encodeContent(myAcct.getContent(), key, salt));
        dbService.updateMyAcct(myAcct);
        return myAcct;
    }

    public TMyAcct newMyAcct(TMyAcct myAcct, String key) throws UnsupportedEncodingException {
        String salt = MyTools.getStringPassword(16);
        myAcct.setCreateDate(DateUtil.getTime(new Date()));
        myAcct.setUpdateDate(DateUtil.getTime(new Date()));
        myAcct.setMac(contentMAC(myAcct));
        myAcct.setKeyVerifyCode(keyVerifyCode(key));
        myAcct.setSalt(salt);
        myAcct.setContent(encodeContent(myAcct.getContent(), key, salt));
        dbService.insertMyAcct(myAcct);
        return myAcct;
    }

    /**
     * 计算mac
     *
     * @param myAcct
     * @return
     */
    public String contentMAC(TMyAcct myAcct) {
        String mac = MyTools.md5(myAcct.getContent());
        return mac.substring(0, 4) + "-" + mac.substring(mac.length() - 4, mac.length());
    }

    /**
     * 计算mac
     *
     * @param key
     * @return
     */
    public String keyVerifyCode(String key) {
        String mac = MyTools.md5(key + "this is macrolab's app");
        for (int i = 0; i < 9; i++) {
            mac = MyTools.md5(mac);
        }
        return mac.substring(0, 4) + "-" + mac.substring(mac.length() - 4, mac.length());
    }

    public void deleteMyAcct(TMyAcct myAcct) {
        dbService.deleteMyAcct(myAcct);
    }
}
