package com.qf.service.impl;

import com.qf.dao.UserCodeMapper;
import com.qf.dao.UserMapper;
import com.qf.domain.User;
import com.qf.domain.UserCode;
import com.qf.service.UserService;
import com.qf.utils.EmailUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserCodeMapper userCodeMapper;
    @Resource
    private EmailUtils emailUtils;
    @Override
    public boolean judge(String name, String qqEmail) {
        User user = userMapper.selectByName(name);
        if(user==null){
            String s = emailUtils.sendEmail(qqEmail);

            UserCode email = userCodeMapper.findByQqEmail(qqEmail);
            if(email==null){
                email.setQqEmail(qqEmail);
                email.setStatus(1);
                email.setCode(s);
                userCodeMapper.save(email);
            }else {
                email.setCode(s);
                userCodeMapper.saveAndFlush(email);
            }
            return true;
        }
        return false;
    }

    //注册
    @Override
    public boolean insert(User user,String qqEmail,String code) {
        UserCode email = userCodeMapper.findByQqEmail(qqEmail);

        if(code.equals(email.getCode())){               //验证码正确，可以注册
            String hashAlgorithName = "MD5";//加密算法
            String password = user.getPassword();//登陆时的密码
            int hashIterations =1024;//加密次数
            ByteSource credentialsSalt = ByteSource.Util.bytes(user.getLoginName());//使用登录名做为salt
            SimpleHash simpleHash = new SimpleHash(hashAlgorithName, password, credentialsSalt, hashIterations);

            user.setPassword(simpleHash.toString());
            user.setCreateTime(new Date());
            user.setState(Byte.valueOf("1"));

            return userMapper.insert(user)>0;
        }
        return false;
    }

    @Override
    public Integer delete(int userid) {
        return userMapper.delete(userid);
    }

    @Override
    public Integer update(User user) {
        String hashAlgorithName = "MD5";//加密算法
        String password = user.getPassword();//登陆时的密码
        int hashIterations =1024;//加密次数
        ByteSource credentialsSalt = ByteSource.Util.bytes(user.getLoginName());//使用登录名做为salt
        SimpleHash simpleHash = new SimpleHash(hashAlgorithName, password, credentialsSalt, hashIterations);

        user.setPassword(simpleHash.toString());
        return userMapper.update(user);
    }

    @Override
    public List<User> selectAll() {
        return userMapper.selectAll();
    }

    @Override
    public User selectOne(int userid) {
        return userMapper.selectOne(userid);
    }
}
