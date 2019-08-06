package com.demo.schedule;

import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.demo.util.ScheduledUtils;


public abstract class AbstractJob implements Job {

public static Logger logger = LoggerFactory.getLogger(AbstractJob.class) ;
	
	private static String DEFAULT_GROUP = "DEFAULT" ;
	
	private boolean isAlive ;
	
	private String jobName ;
	
	private String cronExpression ;
	
	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	/**
	 *  启动线程
	 */
	public void scheduleJob(){
		logger.info("#####################" + this.isAlive());
		if(!isAlive()){
			logger.info("正在启动【" + getJobName() + "】线程。");
			ScheduledUtils.scheduleJob(this.getClass(), getCronExpression(), null , getJobName()  , DEFAULT_GROUP);
			this.setAlive(true);
			logger.info("线程【" + getJobName()  + "】已启动。");
		}else{
			logger.info("线程"+ getJobName() +"运行中");
		}
		logger.info("++++++++++++++++++" + this.isAlive());
	}
	
	/**
	 * 删除调度线程。
	 */
	public void deleteScheduledJob(){
		if(isAlive){ //如果线程运行中则停止运行
			logger.info("正在停止【" + getJobName()  + "】线程运行。");
			ScheduledUtils.deleteScheduledJob(getJobName() , DEFAULT_GROUP);
			this.setAlive(false);
			logger.info("线程【" + getJobName()  + "】已停止运行。");
		}else{
			logger.info("线程【" + getJobName()  + "】未运行。");
		}
	}
	
	/**
	 * 作用：1、初次运行时，初始化变量
	 *           2、监控变量，当变量改变时决定是否改变线程的运行状态。
	 * @param p_run_flag
	 * @param p_cron_exp
	 */
	public void initVariables(String p_run_flag,String p_cron_exp){
		boolean isRestart = false ;
		
		ThreadManagement.addThread(this); //将线程加入到管理类
		
		if("".equals(p_cron_exp)){ //如果运行频率配置为空，则不启动线程
			p_run_flag = "0" ;
		}
		
		if("1".equals(p_run_flag)){	//如果需要运行则加入到运行队列中，否则需要从运行队列中移除
			if(!p_cron_exp.equals(cronExpression)){
				setCronExpression(p_cron_exp);
				isRestart = true ;
			}
			if(isRestart){
				ThreadManagement.unregist(getJobName());
				ThreadManagement.regist(this);
			}else{
				ThreadManagement.regist(this);
			}
		}else{ // 停止运行
			ThreadManagement.unregist(getJobName());
		}
	}
	
	/**
	 * 重写步骤：
	 *  setJobName 
	 *  取出监控变量：运行状态和运行时间cronexpression
	 *  super.initVariables
	 */
	public abstract void init() ;

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AbstractJob)){
			return false ;
		}else{
			AbstractJob job = (AbstractJob) obj ;
			if(this.jobName.equals(job.getJobName())){
				return true ;
			}else{
				return false ;
			}
		}
	}	

}
