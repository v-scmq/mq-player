package com.scmq.player.util;

/**
 * 视图数据恢复接口.
 * <p>
 * 这通常在视图导航过程中会使用到,因为同样类型的视图,在来回切换之后,必须保留并恢复之前的数据.
 * 
 */
@FunctionalInterface
public interface ViewRestore {

	/** 处理并恢复视图数据. */
	void handle();
}
