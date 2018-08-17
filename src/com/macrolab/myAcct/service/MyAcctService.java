package com.macrolab.myAcct.service;

import com.macrolab.myAcct.model.TMyAcct;

import java.util.ArrayList;
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
        List<TMyAcct> result=new ArrayList<>();

        // 查询所有秘钥校验码相同的记录
        dbService.query("name like '%" + name + "%' ");

        // 对秘钥校验码不同的记录返回资料名称，不反资料内容

        return result;
    }


}
