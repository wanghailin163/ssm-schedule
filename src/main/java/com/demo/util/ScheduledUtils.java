package com.demo.util;

import java.util.Properties;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务管理类
 */
public class ScheduledUtils  {
	
	private static Logger logger = LoggerFactory.getLogger(ScheduledUtils.class);
	private volatile static SchedulerFactory sf = new StdSchedulerFactory() ;
	
	static{
		/*创建接口自身的scheduler,避免与缺省的DefaultQuartzScheduler重名*/
		Properties props = new Properties();
		props.put("org.quartz.scheduler.instancename","interfacescheduler");
		props.put("org.quartz.threadPool.threadCount","1000");
		props.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		try {
			if(sf == null){
				synchronized (ScheduledUtils.class){
					if(sf == null){
						sf = new StdSchedulerFactory(props) ;
					}
				}
			}
		} catch (SchedulerException e) {
			logger.error("创建接口的scheduler异常："+e.getMessage());
			e.printStackTrace();
		}		
	}
	
	/**
	 * 删除定时任务
	 */
	public static void deleteScheduledJob(String name, String group){
		try{
			logger.info("删除自动任务");
			Scheduler sc = sf.getScheduler();
			sc.deleteJob(name, group);
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("删除自动任务失败，详情如下：\n" + e.getMessage());
			throw new RuntimeException("删除自动任务失败，详情如下：\n" + e.getMessage());
		}
	}
	
	
	/**
	 * 启动定时任务
	 * @param jobClass 运行定时任务类的Class
	 * @param cronExpression 运行时间表达式
	 * @param service 服务类,放置SchedulerContext中,用于启动调用service方法使用
	 */
	public static void scheduleJob(Class<?> jobClass, String cronExpression, JobDataMap jobData, String name, String group){
		try {
			logger.info("调度自动任务！");
			Scheduler sc = sf.getScheduler();
			
			JobDetail jobDetail = new JobDetail();
			jobDetail.setName(name);
			jobDetail.setGroup(group);
			jobDetail.setJobClass(jobClass);
			jobDetail.setJobDataMap(jobData);
			
			CronTrigger trigger = new CronTrigger(name, group, cronExpression);
			
			sc.scheduleJob(jobDetail, trigger);
			sc.start();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("调度自动任务出错，详细信息如下：\n" + e.getMessage());
			throw new RuntimeException("调度自动任务出错，详细信息如下：\n" + e.getMessage());
		}
	}
}