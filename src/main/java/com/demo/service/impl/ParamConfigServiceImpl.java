package com.demo.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.demo.dao.IParamConfigDao;
import com.demo.model.ParamConfig;
import com.demo.schedule.ThreadManagement;
import com.demo.service.IParamConfigService;
import com.demo.util.Constants;

public class ParamConfigServiceImpl extends Observable implements IParamConfigService {
	
	private static final Logger logger = LoggerFactory.getLogger(ParamConfigServiceImpl.class) ;
	
	public IParamConfigDao IParamConfigDao ;
	
	public ThreadManagement ThreadManagement ;

	public void setThreadManagement(ThreadManagement threadManagement) {
		ThreadManagement = threadManagement;
		this.addObserver(threadManagement);
	}

	public void setIParamConfigDao(IParamConfigDao iParamConfigDao) {
		IParamConfigDao = iParamConfigDao;
	}

	@Override
	public List<ParamConfig> selectConfigModel(String config_name, String config_index) {
		List<ParamConfig> list = null ;
		try {
			Map<String,String> map = new HashMap<String, String>() ;
			map.put("config_name", config_name) ;
			map.put("config_index", config_index) ;		
			list = IParamConfigDao.selectConfigModel(map) ;
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.info("查询配置信息出错，详细信息：{}",e);
			throw new RuntimeException("查询配置信息出错，详细信息：" + e.getMessage() ,e) ;
		}
		
		return list;
	}

	@Override
	public void updateConfigModel(ParamConfig paramconfig) {
		try {
			IParamConfigDao.updateConfigModel(paramconfig);	
			Constants.setConfigMap(selectAllConfigData()) ;		
			this.setChanged();
			this.notifyObservers();
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.info("更新配置数据出错，详细信息：{}",e);
			throw new RuntimeException("更新配置数据出错，详细信息：" + e.getMessage(),e) ;
		}
	}

	@Override
	public void insertConfigModel(ParamConfig paramconfig) {
		try {
			IParamConfigDao.insertConfigModel(paramconfig);
			Constants.setConfigMap(selectAllConfigData()) ;			
			this.setChanged();
			this.notifyObservers();
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.info("插入配置数据出错，详细信息：{}",e);
			throw new RuntimeException("插入配置数据出错，详细信息：" + e.getMessage(),e) ;
		}
	}

	@Override
	public void deleteConfigModel(List<ParamConfig> paramconfigs) {
		try {
			if(paramconfigs != null && paramconfigs.size() > 0){
				for(ParamConfig paramconfig : paramconfigs){
					IParamConfigDao.deleteConfigModel(paramconfig);
				}
			}
			Constants.setConfigMap(selectAllConfigData()) ;
			this.setChanged();
			this.notifyObservers();
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.info("删除配置数据出错，详细信息：{}",e);
			throw new RuntimeException("删除配置数据出错，详细信息：" + e.getMessage(),e) ;
		}
	}

	@Override
	public Map<String, ParamConfig> selectAllConfigData() {
		Map<String,ParamConfig> map = new HashMap<String, ParamConfig>() ;
		try {
			List<ParamConfig> list = IParamConfigDao.selectAllConfigData() ;
			for(ParamConfig cm : list){
				map.put(cm.getConfig_name(), cm) ;
			}
			Constants.setConfigMap(map);
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.info("查询配置数据出错，详细信息:{}",e);
			throw new RuntimeException("查询配置数据出错，详细信息:" + e.getMessage(),e) ;
		}
		return map;
	}

}
