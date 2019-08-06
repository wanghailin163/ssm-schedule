package com.demo.schedule.task;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.demo.model.ParamConfig;
import com.demo.schedule.AbstractJob;
import com.demo.util.Constants;
import com.demo.util.DateUtils;

/**
 * @author WangHaiLin
 * 账务接口数据抓取线程
 */
public class AccountAutoGetDataTask extends AbstractJob {
	
	
	private static final String JOB_NAME = "JOB_ACC_GET_DATA" ;
	
	private final static ReentrantLock lock = new ReentrantLock() ;
	
	//private InterfaceAccountService interfaceAccountService = null ;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException { 
		
		logger.info("账务接口-数据抓取线程【"+ AccountAutoGetDataTask.JOB_NAME + "】已启动。");
		if(lock.isLocked()){
			logger.info("前一个线程{}运行中，请稍候...", AccountAutoGetDataTask.JOB_NAME);
			return;
		}
		lock.lock();
		try {
			Thread.sleep(150000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
		logger.info("账务接口-数据抓取线程【"+ AccountAutoGetDataTask.JOB_NAME + "】运行结束。");
		
	}
	
	@Override
	public void init() {	//如果为1启动自动任务
		setJobName(JOB_NAME);
		
		try{
			Map<String,ParamConfig> map = Constants.getConfigMap() ;
			
			ParamConfig ifcm_flag = map.get("ACC_AUTO_GET") ;
			ParamConfig ifcm_exp = map.get("ACC_AUTO_GET_CRON_EXP") ;
			
			String get_data_var = "0" ;
			String cron_exp =  "" ;
			if(ifcm_flag != null && ifcm_exp != null){ //如果监控变量未配置，则只纳入线程管理而不启动
				get_data_var = DateUtils.null2Str(ifcm_flag.getConfig_value()) ;
				cron_exp = DateUtils.null2Str(ifcm_exp.getConfig_value()) ;
				
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