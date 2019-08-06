package com.demo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.demo.model.ParamConfig;
import com.demo.service.IParamConfigService;

@Controller
@RequestMapping("/paramconfig")
public class ParamConfigController {
     
    @Resource
    private IParamConfigService paramconfigService;
     
    /*
     * 参数列表与分页Action
     */
    @RequestMapping("/list")
    public String list(Model model,@RequestParam(required=false,defaultValue="1") int pageNO){
        int size=5;
        model.addAttribute("size",size);
        model.addAttribute("pageNO",pageNO);
        model.addAttribute("count",null);
        model.addAttribute("paramconfigList", paramconfigService.selectAllConfigData());
        return "paramconfig/list";
    }
    
    /**
     * 添加用户
     * @param model
     * @return
     *//*
    @RequestMapping("/add")
    public String add(Model model){
        // 与form绑定的模型
        model.addAttribute("user",new User());
        // 用于生成“性别”下拉列表
        model.addAttribute("sexList",dictService.getDictByField("SEX"));
        // 用于生成“学历”下拉列表
        model.addAttribute("eduList",dictService.getDictByField("EDU"));
        
        return "user/add";
    }
    
     *//**
      * 添加用户保存
      * @param model
      * @param entity
      * @param bindingResult
      * @return
      *//*
     @RequestMapping("/addSave")
     public String addSave(Model model,@ModelAttribute("user") @Valid User user,BindingResult bindingResult){
        //如果模型中存在错误
        if(!bindingResult.hasErrors()){
            if(userService.insertUser(user)>0){
                return "redirect:/user/list";
            }
        }
        model.addAttribute("user", user);
        // 用于生成“性别”下拉列表
        model.addAttribute("sexList",dictService.getDictByField("SEX"));
        // 用于生成“学历”下拉列表
        model.addAttribute("eduList",dictService.getDictByField("EDU"));
        return "user/add";
     }
     
     *//**
      * 编辑参数
      * @param model
      * @param config_name
      * @return
      */
     @RequestMapping("/edit/{value.config_name}")
     public String edit(Model model,@PathVariable("value.config_name") String config_name){
        model.addAttribute("paramconfig", paramconfigService.selectConfigModel(config_name, null).get(0));
        return "paramconfig/edit";
    }
    
    /**
     * 修改参数并保存
     * @param model
     * @param paramconfig
     * @param bindingResult
     * @return
     */
    @RequestMapping("/editSave")
    public String editSave(Model model,@ModelAttribute("paramconfig") @Valid ParamConfig paramconfig,BindingResult bindingResult){
    	paramconfigService.updateConfigModel(paramconfig);
    	model.addAttribute("paramconfig", paramconfig);
        return "redirect:list";    
    }
    
    /**
     * 根据用户id删除用户
     * @param model
     * @param user_id
     * @param pageNO
     * @param redirectAttributes
     * @return
     */
    @RequestMapping("/deleteParamConfigByConfigName/{value.config_name}")
    public String deleteUserById(Model model,@PathVariable("value.config_name") String config_name,@RequestParam(required=false,defaultValue="1") int pageNO,
            RedirectAttributes redirectAttributes){
    	
    	List<ParamConfig> paramconfigs = new ArrayList<ParamConfig>();
    	paramconfigs.add(paramconfigService.selectConfigModel(config_name, null).get(0));
    	paramconfigService.deleteConfigModel(paramconfigs);
        redirectAttributes.addFlashAttribute("message", "删除成功！");

        return "redirect:/paramconfig/list?pageNO="+pageNO;
    }
    
    /**
     * 删除多个用户
     * @param model
     * @param userIds
     * @param pageNO
     * @param redirectAttributes
     * @return
     *//*
    @RequestMapping("/deleteUsers")
    public String deleteUsers(Model model,@RequestParam int[] user_id,@RequestParam(required=false,defaultValue="1") int pageNO,
            RedirectAttributes redirectAttributes){

        if(userService.deleteUsers(user_id)>0){
            redirectAttributes.addFlashAttribute("message", "删除成功！");
        }else{
            redirectAttributes.addFlashAttribute("message", "删除失败！");
        }
        return "redirect:/user/list?pageNO="+pageNO;
    }*/
}