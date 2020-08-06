package com.scmq.player.util;

import javafx.collections.ObservableMap;
import javafx.scene.Node;

/**
 * 视图数据恢复处理器.
 * <p>
 * 这通常在视图导航过程中会使用到,因为同样类型的视图,在来回切换之后,必须保留并恢复之前的数据.
 * 
 */
@FunctionalInterface
public interface ViewRestore {

	/**
	 * 处理并恢复视图数据.
	 * 
	 * @param data
	 *            视图待恢复数据
	 */
	void handle(Object data);

	/**
	 * 为视图绑定数据恢复实现.
	 * 
	 * @param view
	 *            视图
	 * @param viewRestore
	 *            视图数据恢复处理器
	 */
	static void bind(Node view, ViewRestore viewRestore) {
		ObservableMap<Object, Object> map = view.getProperties();
		map.put(ViewRestore.class, viewRestore);
	}

	/**
	 * 设置当前视图需要维护(下一次切换时待恢复)的数据
	 * 
	 * @param view
	 *            视图(节点)
	 * @param data
	 *            视图需要维护(下一次切换时待恢复)的数据
	 */
	static void setData(Node view, Object data) {
		ObservableMap<Object, Object> map = view.getProperties();
		// 若是可恢复数据的视图,则存放数据
		if (map.get(ViewRestore.class) != null) {
			map.put("view-data", data);
		}
	}

	/**
	 * 获取当前视图需要维护(下一次切换时待恢复)的数据
	 * 
	 * @param view
	 *            视图(节点)
	 * @return 视图需要维护(下一次切换时待恢复)的数据
	 */
	static Object getData(Node view) {
		ObservableMap<Object, Object> map = view.getProperties();
		return map.get(ViewRestore.class) == null ? null : map.get("view-data");
	}

	/**
	 * 恢复视图的数据
	 * 
	 * @param view
	 *            视图(节点)
	 */
	static void restore(Node view, Object data) {
		ObservableMap<Object, Object> map = view.getProperties();
		ViewRestore viewRestore = (ViewRestore) map.get(ViewRestore.class);
		data = viewRestore == null ? null : data;

		if (data != null) {
			viewRestore.handle(data);
		}
	}
}
