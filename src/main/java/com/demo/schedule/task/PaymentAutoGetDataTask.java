package com.demo.schedule.task;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.demo.model.ParamConfig;
import com.demo.schedule.AbstractJob;
import com.demo.util.Constants;

public class PaymentAutoGetDataTask extends AbstractJob {
	
	private static String JOB_NAME = "PYMT_AUTO_GET_DATA" ;

	private final static ReentrantLock lock = new ReentrantLock() ;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("清算接口-清算抓取数据线程【"+ PaymentAutoGetDataTask.JOB_NAME + "】已启动。");
		if(lock.isLocked()){
			logger.info("前一个线程{}运行中，请稍候....",PaymentAutoGetDataTask.JOB_NAME);
			return ;
		}
		lock.lock();
		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.info("核心清算通知抓取数据线程出错，详细信息：{}",e);
		}finally{
			lock.unlock();
		}
		logger.info("清算接口-清算抓取数据线程【"+ PaymentAutoGetDataTask.JOB_NAME + "】运行结束。");
	}
	
	public void init() {	
		setJobName(JOB_NAME); 
		
		try{
			Map<String , ParamConfig> map = Constants.getConfigMap() ;
			
			ParamConfig ifcm_flag = map.get("PYMT_AUTO_GET") ;
			ParamConfig ifcm_exp = map.get("PYMT_GET_DATA_CRON_EXP") ;
			
			String get_data_var = "0" ;
			String cron_exp = "" ;
			if(ifcm_flag != null && ifcm_exp != null){
				get_data_var = ifcm_flag.getConfig_value() ;
				cron_exp = ifcm_exp.getConfig_value() ;
				
				if("".equals(get_data_var) || "".equals(cron_exp)){
					logger.info("@@@@@任务[{}]未启动，运行变量[PYMT_AUTO_GET/PYMT_GET_DATA_CRON_EXP]配置为空。",JOB_NAME);
				}
			}else{
				logger.info("@@@@@任务[{}]未启动，运行变量[PYMT_AUTO_GET/PYMT_GET_DATA_CRON_EXP]未配置。",JOB_NAME);
			}
			
			super.initVariables(get_data_var, cron_exp);
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.info("初始化运行线程出错，线程名称【" + this.getJobName() + "】，详细原因：{}",e);
			
			super.initVariables("0", "");
		}
	}

}
