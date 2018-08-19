package com.macrolab.myAcct.service;

import com.macrolab.myAcct.common.CommUI;
import com.macrolab.myAcct.controller.MainController;
import com.macrolab.myAcct.model.TMyAcct;
import com.macrolab.myAcct.util.Base64;
import com.macrolab.myAcct.util.DateUtil;
import com.macrolab.myAcct.util.MyTools;
import com.macrolab.myAcct.util.ToolUtil;
import javafx.scene.paint.Paint;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        List<TMyAcct> result = new ArrayList();

        if (ToolUtil.isEmpty(name)) {
            // 查询所有秘钥校验码相同的记录
            result = dbService.query("");
        } else {
            // 查询所有秘钥校验码相同的记录
            result = dbService.query("and name like '%" + name + "%' and key_verify_code = '" + keyVerifyCode + "'");
        }

        // 对秘钥校验码不同的记录返回资料名称，不反资料内容

        return result;
    }

    /**
     * 解密资料
     *
     * @param myAcct
     * @param key
     * @return
     */
    public String decodeContent(TMyAcct myAcct, String key) {
        String codeContent = myAcct.getContent();
        String salt = myAcct.getSalt();
        String currentKeyVerifyCode = keyVerifyCode(key);
        if (!keyVerifyCode(key).equals(myAcct.getKeyVerifyCode())) {
            CommUI.warningBox(null, "资料保密秘钥不正确！");
            return null;
        }

        try {
            return new String(MyTools.decryptAES(Base64.decode(codeContent.getBytes()), MyTools.md5(key + salt)), "UTF-8");
        } catch (Exception e) {
            CommUI.errorBox("解读资料内容时异常，您使用的秘钥可能不正确！", e.getMessage());
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
     */
    private String encodeContent(String content, String key, String salt) {
        try {
            return new String(Base64.encode(MyTools.encryptAES(content.getBytes("UTF-8"), MyTools.md5(key + salt))));
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger(MyAcctService.class.getName()).log(Level.WARNING, "加密资料内容时异常！", e);
            return null;
        }
    }

    public TMyAcct saveMyAcct(TMyAcct myAcct, String key) {
        String salt = MyTools.getStringPassword(16);
        myAcct.setUpdateDate(DateUtil.getTime(new Date()));
        myAcct.setMac(contentMAC(myAcct));
        myAcct.setKeyVerifyCode(keyVerifyCode(key));
        myAcct.setSalt(salt);
        myAcct.setContent(encodeContent(myAcct.getContent(), key, salt));
        dbService.updateMyAcct(myAcct);
        return myAcct;
    }

    public TMyAcct newMyAcct(TMyAcct myAcct, String key) {
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
