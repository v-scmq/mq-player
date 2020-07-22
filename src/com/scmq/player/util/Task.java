package com.scmq.player.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Task是一个异步执行的任务.
 *
 * @author SCMQ
 */
public final class Task {
	/** 一个默认为3个线程的线程池 */
	private static final ExecutorService SERVICE = Executors.newFixedThreadPool(3);

	/** 任务执行标记 */
	private boolean state = true;

	/**
	 * 异步执行任务.
	 * 
	 * 该方法启动的线程,不具有状态标记,用于执行无进度管理的任务.
	 *
	 * @param runnable
	 *            任务(线程)执行的具体任务
	 */
	public static void async(Runnable runnable) {
		SERVICE.execute(runnable);
	}

	/** 关闭线程池 */
	public static void shutdown() {
		if (!SERVICE.isShutdown()) {
			SERVICE.shutdown();
		}
	}

	/** 尝试取消任务,但不能保证任务成功取消 */
	public void stop() {
		state = false;
	}

	/**
	 * 异步执行任务.<br>
	 * 此方法启动的线程,具有状态标记,用于执行需要进度管理的任务.
	 *
	 * @param runnable
	 *            任务(线程)执行的具体任务
	 */
	public void start(Runnable runnable) {
		SERVICE.execute(runnable);
	}

	/**
	 * 获得任务执行状态,但这个判断是不安全的,因为它仅仅是一个标记</br>
	 * 该标记用于在run方法中使用来控制线程的运行
	 *
	 * @return 如果为true则任务仍在执行, 否则任务结束
	 */
	public boolean state() {
		return state;
	}
}
