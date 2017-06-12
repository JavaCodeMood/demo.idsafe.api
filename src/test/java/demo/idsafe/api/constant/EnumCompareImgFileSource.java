package demo.idsafe.api.constant;

/**
 * 人脸比对照片来源参数
 *
 * @author geosmart
 * @date 2017/05/25
 */
public enum EnumCompareImgFileSource {
    SESSION_ID("0", "会话ID"),
    URL("1", "有盾返回的照片URL地址"),
    BASE_64("2", "照片的Base64格式字符串"),;
    private String code;
    private String desc;

    EnumCompareImgFileSource(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumCompareImgFileSource newInstance(String srcCode) {
        EnumCompareImgFileSource result = null;
        for (EnumCompareImgFileSource code : EnumCompareImgFileSource.values()) {
            if (code.getCode().equals(srcCode)) {
                result = code;
                break;
            }
        }
        return result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}