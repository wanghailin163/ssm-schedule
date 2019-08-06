package com.demo.schedule.task;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.demo.model.ParamConfig;
import com.demo.schedule.AbstractJob;
import com.demo.util.Constants;

public class AccountAutoGetDuizhDataTask extends AbstractJob{

	private final static String JOB_NAME = "AUTO_GET_DUIZH_DATA" ;
	
	private ReentrantLock lock = new ReentrantLock() ;
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		logger.info("对账接口-数据请求线程【"+ AccountAutoGetDuizhDataTask.JOB_NAME + "】已启动。");
		
		if(lock.isLocked()){
			logger.info("前一个线程【{}】运行中",AccountAutoGetDuizhDataTask.JOB_NAME);
			return ;
		}
		lock.lock(); 
		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			lock.unlock(); 
		}
		logger.info("对账接口-数据请求线程【"+ AccountAutoGetDuizhDataTask.JOB_NAME + "】运行结束。");
		
	}

	@Override
	public void init() {
		setJobName(JOB_NAME);
		
		try{
			Map<String,ParamConfig> map = Constants.getConfigMap() ;
			
			ParamConfig ifcm_flag = map.get("ACC_DUIZH_AUTO_GET") ;
			ParamConfig ifcm_exp = map.get("ACC_DUIZH_CRON_EXP") ;
			
			String get_data_var = "0" ;
			String cron_exp = "" ;
			if(ifcm_flag != null && ifcm_exp != null ){
				get_data_var = ifcm_flag.getConfig_value() ;
				cron_exp = ifcm_exp.getConfig_value() ;
				
				if("".equals(ifcm_flag) || "".equals(cron_exp)){
					logger.info("@@@@@任务[{}]未启动，运行变量[ACC_AUTO_GET/ACC_AUTO_GET_CRON_EXP]配置为空。",JOB_NAME);
				}
			}else{
				logger.info("@@@@@任务[{}]未启动，运行变量[ACC_AUTO_GET/ACC_AUTO_GET_CRON_EXP]未配置。",JOB_NAME);
			}
				
			super.initVariables(get_data_var, cron_exp);
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.info("初始化运行线程出错，线程名称【" + this.getJobName() + "】，详细原因：{}",e);
			
			super.initVariables("0", "");
		}
	}

}
