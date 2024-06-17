package com.work37.napnap.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Game implements Serializable {
    private Long id;
    /**
     * 游戏名称
     */
    private String gameName;
    /**
     * 游戏简介
     */
    private String gameProfile;
    /**
     * 游戏图片
     */
    private String gameIcon;
    /**
     *
     */
    private List<String> tag;
    /**
     * 评分
     */
    private BigDecimal gameScore;
    /**
     * 游戏大小
     */
    private BigDecimal gameSize;
    /**
     * 游戏链接
     */
    private String gameUrl;
    /**
     * 评分数
     */
    private Long gameNum;
    /**
     * 收藏数
     */
    private Long collectNum;
    /**
     * 下载量
     */
    private Long downloadNum;

    public Game() {
    }

    public Game(Long id, String gameName, String gameProfile, String gameIcon, List<String> tag,
                BigDecimal gameScore, BigDecimal gameSize, String gameUrl, Long gameNum, Long collectNum, Long downloadNum) {
        this.id = id;
        this.gameName = gameName;
        this.gameProfile = gameProfile;
        this.gameIcon = gameIcon;
        this.tag = tag;
        this.gameScore = gameScore;
        this.gameSize = gameSize;
        this.gameUrl = gameUrl;
        this.gameNum = gameNum;
        this.collectNum = collectNum;
        this.downloadNum = downloadNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameProfile() {
        return gameProfile;
    }

    public void setGameProfile(String gameProfile) {
        this.gameProfile = gameProfile;
    }

    public String getGameIcon() {
        return gameIcon;
    }

    public void setGameIcon(String gameIcon) {
        this.gameIcon = gameIcon;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public BigDecimal getGameScore() {
        return gameScore;
    }

    public void setGameScore(BigDecimal gameScore) {
        this.gameScore = gameScore;
    }

    public BigDecimal getGameSize() {
        return gameSize;
    }

    public void setGameSize(BigDecimal gameSize) {
        this.gameSize = gameSize;
    }

    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }

    public Long getGameNum() {
        return gameNum;
    }

    public void setGameNum(Long gameNum) {
        this.gameNum = gameNum;
    }

    public Long getCollectNum() {
        return collectNum;
    }

    public void setCollectNum(Long collectNum) {
        this.collectNum = collectNum;
    }

    public Long getDownloadNum() {
        return downloadNum;
    }

    public void setDownloadNum(Long downloadNum) {
        this.downloadNum = downloadNum;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", gameName='" + gameName + '\'' +
                ", gameProfile='" + gameProfile + '\'' +
                ", gameIcon='" + gameIcon + '\'' +
                ", tag='" + tag + '\'' +
                ", gameScore=" + gameScore +
                ", gameSize=" + gameSize +
                ", gameUrl='" + gameUrl + '\'' +
                ", gameNum=" + gameNum +
                ", collectNum=" + collectNum +
                ", downloadNum=" + downloadNum +
                '}';
    }
}
