package com.demo.schedule.task;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.demo.model.ParamConfig;
import com.demo.schedule.AbstractJob;
import com.demo.util.Constants;

public class AccountAutoSendDataTask extends AbstractJob{
	
	private static Logger logger = LoggerFactory.getLogger(AccountAutoSendDataTask.class);
	
	private static final String JOB_NAME = "ACC_SEND_DATA";
	
	private final static ReentrantLock lock = new ReentrantLock();

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		logger.info("账务接口-数据发送线程【"+ AccountAutoSendDataTask.JOB_NAME + "】已启动。");
		
		if (lock.isLocked()) {
			logger.info("前一个发送线程{}运行中...",AccountAutoSendDataTask.JOB_NAME);
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
		logger.info("账务接口-数据发送线程【"+ AccountAutoSendDataTask.JOB_NAME + "】运行结束。");
	}

	@Override
	public void init() { // 如果为1启动自动任务
		setJobName(JOB_NAME);

		try {
			// 取出线程的所有配置数据
			Map<String, ParamConfig> map = Constants.getConfigMap() ; // 取出运行变量
			
			// 判断线程运行状态
			ParamConfig ifcm_flag = map.get("ACC_AUTO_SEND") ;
			ParamConfig ifcm_exp = map.get("ACC_AUTO_SEND_CRON_EXP") ;
			
			String send_data_var = "0" ;
			String cron_exp = "" ;
			if(ifcm_flag != null && ifcm_exp != null){
				send_data_var = ifcm_flag.getConfig_value() ;
				cron_exp = ifcm_exp.getConfig_value() ;
				
				if("".equals(ifcm_flag) || "".equals(cron_exp)){
					logger.info("@@@@@任务[{}]未启动，运行变量[ACC_AUTO_SEND/ACC_AUTO_SEND_CRON_EXP]配置为空。",JOB_NAME);
				}
				
			}else{ //如查监控变量都未配置，则纳入线程管理，不启动
				logger.info("@@@@@任务[{}]未启动，运行变量[ACC_AUTO_SEND/ACC_AUTO_SEND_CRON_EXP]未配置。",JOB_NAME);
			}
			super.initVariables(send_data_var, cron_exp);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("初始化运行线程出错，线程名称【" + this.getJobName() + "】，详细原因：{}", e);
			
			super.initVariables("0", "");
		}
	}

}
