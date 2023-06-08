package com.work.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.work.common.R;
import com.work.entity.Employee;
import com.work.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    //登录成功后，把Employee的ID信息存在session，可通过request获取session
    public R<Employee> login(HttpServletRequest request,@RequestBody  Employee employee){
       //密码用md5加密
        String password=employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        //查询数据库
        LambdaQueryWrapper<Employee> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //数据库的用户名和传入的比对
        objectLambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        //查出唯一的对象
        Employee emp = employeeService.getOne(objectLambdaQueryWrapper);
        //用户不存在
        if(emp==null){
            return R.error("登录失败");
        }
        //密码错误
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }
        //用户没有权限
        if(emp.getStatus()==0){
            return R.error("账号已禁用，登录失败");
        }
        //一切正确，存入session
        request.getSession().setAttribute("employee",emp.getId());
        //返回成功的对象
        return R.success(emp);
    }
    //员工退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session中保存的当前员工ID
    request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    //新增员工
    @PostMapping
    //传来的实体是Json格式，需要@RequestBody
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息{}",employee.toString());
        //新增员工的初始密码等
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录管理用户的ID作为更改人
        Long empID = (Long)request.getSession().getAttribute("employee");
//        employee.setCreateUser(empID);
//        employee.setUpdateUser(empID);

        //Controller调用Service去将employee存入数据库
        employeeService.save(employee);
        return R.success("新增员工成功");
    }
//员工信息的分页查询
    @GetMapping("/page")
    //get方法直接传来的是键值对，不用Json解析，一一对应
    public R<Page> page(int page,int pageSize,String name){
       log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器，查第几页第几行
        Page pageInfo=new Page(page,pageSize);

        //构造条件构造器，查询数据库，mybatis数据库查询框架
        LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper();
        //添加过滤条件,name!=null,就可以匹配名字，like相当于Sql的like查询
        lambdaQueryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询,查询完毕就会进行封装到pageInfo中，不会返回
        employeeService.page(pageInfo,lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    //根据ID修改员工信息
    public R<String> update(HttpServletRequest  request,@RequestBody Employee employee){
        log.info(employee.toString());
        Long empID = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empID);
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    //根据ID查询员工信息，通过网站的id内容传入路径变量@PathVariable Long id
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if(employee!=null)
        return R.success(employee);
        return R.error("没有查询到对应员工信息");
    }
}
