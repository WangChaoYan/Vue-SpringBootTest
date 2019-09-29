package com.qf.shiro;

import com.qf.dao.PermissionMapper;
import com.qf.dao.UserMapper;
import com.qf.domain.Permission;
import com.qf.domain.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;


import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MyRealm extends AuthorizingRealm {
    @Resource
    private UserMapper userMapper;

    @Resource
    private PermissionMapper permissionMapper;
    //权限
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();     //得到用户名
        List<Permission> list = permissionMapper.selectPermissionByUsername(username);

        Collection set=new HashSet();       //HashSet底层由hashmap实现，不允许集合中有重
                                            // 复的值出现， 使用该方式时，需要重写equals（）
                                            //和hashcode（）方法

        for (Permission permission : list) {
            set.add(permission.getPerName());               //将权限去重
        }

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addStringPermissions(set);
        return simpleAuthorizationInfo;
    }

    //登陆
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();  //得到用户输入的username
        User user = userMapper.selectByName(username);
        SimpleAuthenticationInfo simpleAuthenticationInfo=new SimpleAuthenticationInfo(user.getLoginName(),user.getPassword(),ByteSource.Util.bytes(user.getLoginName()),getName());
        return simpleAuthenticationInfo;
    }

}
