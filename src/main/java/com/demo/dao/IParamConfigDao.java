package com.demo.dao;

import java.util.List;
import java.util.Map;
import com.demo.model.ParamConfig;

public interface IParamConfigDao {
	
	public List<ParamConfig> selectConfigModel(Map<String,String> map);

	public void updateConfigModel(ParamConfig paramconfig);

	public void insertConfigModel(ParamConfig paramconfig);

	public void deleteConfigModel(ParamConfig paramconfig);
	
	/**
     * 查询全量配置参数
     * @return
     */
	public List<ParamConfig> selectAllConfigData() ;

}
