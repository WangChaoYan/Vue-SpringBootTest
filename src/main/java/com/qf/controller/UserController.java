package com.qf.controller;

import com.qf.domain.User;
import com.qf.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class UserController {
    @Resource
    private UserService userService;

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/show")
    public String show(){
        return "show";
    }

    @RequestMapping(value = "/loginVal",method = RequestMethod.POST)
    @ResponseBody
    public String loginVal(@RequestParam("username") String username,@RequestParam("password") String password){
        if(username!=""&&password!=""){
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(username,password);
            try {
                subject.login(token);
                if(subject.isAuthenticated()){        //判断是否成功登陆
                    return "1";              //1,登陆成功 2，重新登陆  3，值不能为空
                }else {
                    return "2";
                }
            }catch (Exception e){
                System.out.println("登陆出错");
            }


        }
        return "3";
    }

    //判断用户是否存在,如果不存在发送验证码
    @RequestMapping(value = "/judge")
    @ResponseBody
    public String judge(String name,String qq){
        if(userService.judge(name,qq)){
            return "0";        //0:可以注册  1:用户已存在
        }
        return "1";
    }

    @RequestMapping(value = "/insert")
    @ResponseBody
    public String insert(User user,String qq,String code){
        System.out.println(user);
        boolean s=userService.insert(user,qq,code);
        if(s){
            return "0";                        //0:注册成功     1:验证码有误
        }
        return "1";
    }

    @RequestMapping(value = "/delete")
    public String delete(int id, Model model){
        userService.delete(id);
        model.addAttribute("list",userService.selectAll());
        return "delete";
    }

    @RequestMapping(value = "/update")
    public String update(User user, Model model){
        userService.update(user);
        model.addAttribute("list",userService.selectAll());
        return "update";
    }

    @RequestMapping(value = "/selectAll")
    public String selectAll(Model model){
        model.addAttribute("list",userService.selectAll());
        return "selectAll";
    }

    @RequestMapping(value = "/selectOne")
    public String selectOne(int id,Model model){
        model.addAttribute("user",userService.selectOne(id));
        return "selectOne";
    }



    @RequestMapping(value = "/add")
    public String add(){
        return "add";
    }

    @RequestMapping(value = "/success")
    public String success(){
        return "redirect:selectAll";
    }

    //关系
    @RequiresPermissions(value = {"user_insert"})           //注解设置权限
    @RequestMapping(value = "/registered")
    public String registered(){
        return "insert";
    }

    @RequiresPermissions(value = {"user_delete"})           //注解设置权限
    @RequestMapping(value = "/del")
    public String del(Model model){
        model.addAttribute("list",userService.selectAll());
        return "delete";
    }

    @RequiresPermissions(value = {"user_update"})            //注解设置权限
    @RequestMapping(value = "/upd")
    public String upd(Model model){
        model.addAttribute("list",userService.selectAll());
        return "update";
    }

    @RequiresPermissions(value = {"user_select"})             //注解设置权限
    @RequestMapping(value = "/sel")
    public String sel(Model model){
        model.addAttribute("list",userService.selectAll());
        return "selectAll";
    }

    //没有权限
    @RequestMapping(value = "/noPermission")
    public String noPermission(){
        return "noPermission";
    }
}
