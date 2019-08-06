package com.demo.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程运行监控类. Spring自动任务，每5秒运行一次。
 * 
 * @author Administrator
 *
 */
public class ThreadManagement implements Observer {
	private static Logger logger = LoggerFactory.getLogger(ThreadManagement.class);

	private static Map<String, AbstractJob> jobMap = new ConcurrentHashMap<String, AbstractJob>();

	private static List<AbstractJob> allJobList = Collections.synchronizedList(new ArrayList<AbstractJob>());

	/**
	 * 将所有线程纳入线程管理类的管理。 包含所有运行中和未运行的线程。
	 * 
	 * @param job
	 */
	public synchronized static void addThread(AbstractJob job) {
		if (!allJobList.contains(job)) {
			allJobList.add(job);
		}
	}

	/**
	 * 将线程加入到运行队列。
	 * 
	 * @param job
	 */
	public synchronized static void regist(AbstractJob job) {
		if (!jobMap.containsKey(job.getJobName())) {
			jobMap.put(job.getJobName(), job);
		}
	}

	/**
	 * 将线程移出运行队列。
	 * 
	 * @param jobKey
	 */
	public synchronized static void unregist(String jobKey) {
		if (jobMap.containsKey(jobKey)) {
			AbstractJob job = jobMap.get(jobKey);
			job.deleteScheduledJob();

			jobMap.remove(jobKey);
		}
	}

	/**
	 * 管理线程运行监控入口方法
	 */
	public void run() {
		List<AbstractJob> jobList = new ArrayList<AbstractJob>(jobMap.values());
		logger.info("【" + jobList.size() + "】个线程运行中。");
		for (AbstractJob job : jobList) {
			logger.info("job【" + job.getJobName() + "】运行中，isAlive=【" + job.isAlive() + "】。");
			job.scheduleJob();
		}
	}

	/**
	 * 检查线程是否处于运行队列中。
	 * 
	 * @param jobKey
	 * @return
	 */
	public static AbstractJob getAbstractJob(String jobKey) {
		if (jobMap.containsKey(jobKey)) {
			return jobMap.get(jobKey);
		} else {
			return null;
		}
	}

	@Override
	public void update(Observable o, Object arg) { // 如果接口变量发生变化，则检查接口的运行状态。
		logger.info("运行参数发生变化，检查线程运行是否变化。");
		Iterator<AbstractJob> iter = allJobList.iterator();
		while (iter.hasNext()) {
			AbstractJob job = iter.next();
			logger.info("检查线程【" + job.getJobName() + "】是否需要重启。");
			try {
				job.init();
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("重启接口出错，请检查，详细信息：{}", e);
			}
		}
	}
}
