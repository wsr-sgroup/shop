package com.project.platform.controller;

import com.project.platform.entity.User;
import com.project.platform.service.UserService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping( "/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 分页模糊查询
     * @param query 查询
     * @param pageNum 页数
     * @param pageSize 大小
     * @return 返回
     */
    @GetMapping("page")
    public ResponseVO<PageVO<User>> page(
            @RequestParam Map<String,Object> query,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize){
        PageVO<User> page=userService.page(query,pageNum,pageSize);
        return ResponseVO.ok(page);
    }
    /**
     * 列表查询
     * @return 返回
     */

    @GetMapping("list")
    public ResponseVO<List<User>> list(){
        return ResponseVO.ok(userService.list());
    }

    /**
     * 通过id查询
     * @param id id
     * @return 返回
     */

    @GetMapping("selectById/{id}")
    public ResponseVO<User> selectById(@PathVariable("id") Integer id){
        return ResponseVO.ok(userService.selectById(id));
    }

    /**
     * 通过用户名查询
     * @param username 用户名
     * @return 返回
     */
    @GetMapping("selectByUsername/{username}")
    public ResponseVO<User> selectByUsername(@PathVariable("username") String username){
        return ResponseVO.ok(userService.selectByUsername(username));
    }

    /**
     * 新增
     * @param entity 编辑
     * @return 返回
     */
    @PostMapping("add")
    public ResponseVO add(@RequestBody User entity){
        userService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     * @param entity 编辑
     * @return 返回
     */
    @PutMapping("update")
    public ResponseVO update(@RequestBody User entity){
        userService.updateById(entity);
        return ResponseVO.ok();
    }

    /**
     * 删除
     * @param ids ids
     * @return 返回
     */
    @DeleteMapping("delBatch")
    public ResponseVO delBatch(@RequestBody List<Integer> ids){
        userService.removeByIds(ids);
        return ResponseVO.ok();
    }

}
