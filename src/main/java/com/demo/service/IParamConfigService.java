package com.demo.service;

import java.util.List;
import java.util.Map;
import com.demo.model.ParamConfig;

public interface IParamConfigService {

	/**
	 * 根据参数名称获取参数列表对象
	 * @param config_Name 参数名称
	 * @return ParamConfig 参数对象
	 */
	public List<ParamConfig> selectConfigModel(String config_name,String config_index);
	
	/**
	 * 更新接口参数对象
	 * @param icm 参数对象
	 */
	public void updateConfigModel(ParamConfig icm);
	
	/**
	 * 保存接口参数对象
	 * @param icm 参数对象
	 */
	public void insertConfigModel(ParamConfig icm);
	
	/**
	 * 删除接口参数对象
	 * @param config_Name 参数名称
	 */
	public void deleteConfigModel(List<ParamConfig> icmList);
	
	public Map<String,ParamConfig> selectAllConfigData() ;
}
