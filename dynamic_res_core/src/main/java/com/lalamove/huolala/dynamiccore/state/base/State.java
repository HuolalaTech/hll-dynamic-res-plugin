package com.lalamove.huolala.dynamiccore.state.base;

/**
 * @copyright：深圳依时货拉拉科技有限公司
 * @fileName: State
 * @author: huangyuchen
 * @date: 3/3/22
 * @description:
 * @history:
 */
public enum State {
    //开始状态
    INIT(0, "初始化"),
    //检查版本状态
    CHECK_VERSION(1, "检查版本"),
    //开始下载状态
    START_DOWNLOAD(2, "开始下载"),
    //下载中状态
    DOWNLOADING(3, "下载中"),
    //校验下载资源
    VERIFY_RES(4, "验证资源"),
    //解压缩状态
    UNZIP(5, "解压缩"),
    //校验zip压缩包内容状态
    VERIFY_ZIP(6, "验证压缩包"),
    //成功
    SUCCEED(7, "成功"),
    //失败
    ERROR(8, "失败");

    public final int id;
    public final String msg;

    State(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "State{" +
                "id=" + id +
                ", msg='" + msg + '\'' +
                '}';
    }
}
