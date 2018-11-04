package cn.acewill.mobile.pos.service.retrofit.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/9/3.
 */
public class ScreenResponse implements Serializable {
    private int showDescription;//点菜时是否显示菜品图片及介绍 0:不显示
    private int timeOff;//无操作多久时全屏显示图片或视频, 0秒为不显示
    private String subtitle;//欢迎来到...”, //滚动字幕
    private String video;//指向该视频的地址
    private String imgs;//图片地址，多个逗号隔开
    private List<String> screenImgs;//图片地址，集合保存
    private String defaultImg; //当图片地址集合为空时，使用默认图片

    public int getShowDescription() {
        return showDescription;
    }

    public void setShowDescription(int showDescription) {
        this.showDescription = showDescription;
    }

    public String getDefaultImg() {
        return defaultImg;
    }

    public void setDefaultImg(String defaultImg) {
        this.defaultImg = defaultImg;
    }

    public List<String> getScreenImgs() {
        return screenImgs;
    }

    public void setScreenImgs(List<String> screenImgs) {
        this.screenImgs = screenImgs;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getTimeOff() {
        return timeOff;
    }

    public void setTimeOff(int timeOff) {
        this.timeOff = timeOff;
    }
}
