package com.scmq.player.model;

/**
 * 这个Page类的对象,作为较多的数据量处理分页对象
 *
 * @author SCMQ
 */
public class Page {
    /** 当前页 */
    private int current = 1;
    /** 每页数据量大小 */
    private int size = 30;
    /** 总页数 */
    private int total = 1;

    /**
     * 构造一个 默认“当前为第1页,每页数量为30,总页数为1”的分页(Page)对象.
     */
    public Page() {
    }

    /**
     * 通过当前页和每页数据量大小,构造一个Page对象
     *
     * @param current 当前页编号
     * @param size    每页数据量大小
     */
    public Page(int current, int size) {
        setCurrent(current);
        setSize(size);
    }

    /**
     * 通过当前页编号和默认页数据量大小,来构造一个Page对象
     *
     * @param current 当前页编号
     */
    public Page(int current) {
        setCurrent(current);
    }

    /**
     * 获取当前页编号
     *
     * @return 当前页编号
     */
    public int getCurrent() {
        return current;
    }

    /**
     * 设置当前页编号.当前页编号 , 总是一个合法数字. <br>
     * 1.若当前页编号小于1时 , 则当前页编号还是1;<br>
     * 2.若当前页编号大于等于总页数时 ,则当前页编号为总页数.
     *
     * @param current 当前页编号
     */
    public void setCurrent(int current) {
        this.current = current < 1 ? 1 : current < total ? current : total;
    }

    /**
     * 获取每页数据量大小
     *
     * @return 每页数据量大小
     */
    public int getSize() {
        return size;
    }

    /**
     * 设置每页数据量大小
     *
     * @param size 每页数据量大小
     */
    public void setSize(int size) {
        this.size = size < 10 ? 10 : size;
    }

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public int getTotal() {
        return total;
    }

    /**
     * 计算总页数
     *
     * @param totalItem 数据总量
     */
    public void calculate(int totalItem) {
        total = totalItem <= size ? 1 : (int) Math.ceil((double) totalItem / size);
    }

    public void reset() {
        current = 1;
        total = 1;
        size = 30;
    }

    @Override
    public String toString() {
        return "Page [current=" + current + ", size=" + size + ", total=" + total + "]";
    }
}
