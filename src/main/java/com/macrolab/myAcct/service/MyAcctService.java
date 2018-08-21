package com.macrolab.myAcct.service;

import com.macrolab.myAcct.common.AppContext;
import com.macrolab.myAcct.model.DBFile;
import com.macrolab.myAcct.model.TMyAcct;
import com.macrolab.myAcct.util.Base64;
import com.macrolab.myAcct.util.DateUtil;
import com.macrolab.myAcct.util.MyTools;
import com.macrolab.myAcct.util.ToolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyAcctService {
    Logger logger = LoggerFactory.getLogger(MyAcctService.class);

    DBService dbService;

    // 注入dbService；
    public void setDbService(DBFile dbFile) {
        dbService = new DBService(dbFile);
        AppContext.dbService = dbService;
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

        result.stream().filter(t -> !t.getKeyVerifyCode().equals(keyVerifyCode)).forEach(t -> {
            t.setName("【锁】" + t.getName());
        });

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
        if (!currentKeyVerifyCode.equals(myAcct.getKeyVerifyCode())) {
            if (ToolUtil.isNotEmpty(myAcct.getContent())) {
                logger.warn("提取资料失败，资料保密秘钥不正确！");
            }
            return null;
        }

        try {
            return new String(MyTools.decryptAES(Base64.decode(codeContent.getBytes()), MyTools.md5(key + salt)), "UTF-8");
        } catch (Exception e) {
            logger.warn("提取资料失败，资料保密秘钥不正确！");
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
            logger.warn("加密资料内容时异常！", e);
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

    /**
     * 获取工作目录下的数据文件列表
     *
     * @return
     */
    public List<DBFile> getDBFilelist(String path) {
        ArrayList<DBFile> files = new ArrayList<>();
        File file = new File(path);
        // 只提取工作目录下 后缀名为 *.myAcctDB 的数据库文件
        File[] tempList = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("myAcctDB");
            }
        });

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                DBFile dbFile = new DBFile(tempList[i]);
                files.add(dbFile);
            }
        }
        return files;
    }
}
