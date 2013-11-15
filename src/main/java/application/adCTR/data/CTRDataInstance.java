package application.adCTR.data;

import commons.framework.data.DataInstance;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午3:08
 */
public class CTRDataInstance extends DataInstance {
    public CTRDataInstance(long dataInstanceId)
    {
        this.dataInstanceId = dataInstanceId;
    }
    private double targetValue = Double.MIN_VALUE;
    private int version = -1;
    private long timeStamp = -1;
    private String ip = null;
    private int castId = -1;
    private int creativeId = -1;
    private String category = null;
    private String subCategory = null;
    private String videoId = null;
    private String userId = null;
    private String cookie = null;
    private int adLength = -1;
    private boolean isLong = false;
    private String programId = null;
    private String totalDown = null;
    private double videoLength = -1;
    private int adPosition = -1;
    private String sessionId = null;
    private boolean copyright = false;
    private String orderId = null;
    private boolean tvb = false;
    private int productType = -1;
    private String appVersion = null;
    private String displayQuality = null;
    private int deviceType = -1;
    private int osType = -1;
    private String clientType = null;
    private String sdkId = null;
    private boolean isFullScreen = false;

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    private String keywords = null;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getCastId() {
        return castId;
    }

    public void setCastId(int castId) {
        this.castId = castId;
    }

    public int getCreativeId(){
        return creativeId;
    }

    public void setCreativeId(int creativeId) {
        this.creativeId = creativeId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public int getAdLength() {
        return adLength;
    }

    public void setAdLength(int adLength) {
        this.adLength = adLength;
    }

    public boolean isLong() {
        return isLong;
    }

    public void setLong(boolean aLong) {
        isLong = aLong;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getTotalDown() {
        return totalDown;
    }

    public void setTotalDown(String totalDown) {
        this.totalDown = totalDown;
    }

    public double getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(double videoLength) {
        this.videoLength = videoLength;
    }

    public int getAdPosition() {
        return adPosition;
    }

    public void setAdPosition(int adPosition) {
        this.adPosition = adPosition;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isCopyright() {
        return copyright;
    }

    public void setCopyright(boolean copyright) {
        this.copyright = copyright;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isTvb() {
        return tvb;
    }

    public void setTvb(boolean tvb) {
        this.tvb = tvb;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDisplayQuality() {
        return displayQuality;
    }

    public void setDisplayQuality(String displayQuality) {
        this.displayQuality = displayQuality;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getOsType() {
        return osType;
    }

    public void setOsType(int osType) {
        this.osType = osType;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getSdkId() {
        return sdkId;
    }

    public void setSdkId(String sdkId) {
        this.sdkId = sdkId;
    }

    public double getTargetValue()
    {
        return targetValue;
    }

    public void setTargetValue(double targetValue)
    {
        this.targetValue = targetValue;
    }
}
