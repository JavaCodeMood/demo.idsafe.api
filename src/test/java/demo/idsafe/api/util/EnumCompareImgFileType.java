package demo.idsafe.api.util;

/**
 * 人脸比对照片类型参数
 *
 * @author geosmart
 * @date 2017/05/25
 */
public enum EnumCompareImgFileType {
    IDCARD_PORTRAIT_PHOTO("0", "证件照"),
    LIVING_PHOTO("1", "活体照"),
    VIDEO_PHOTO("2", "视频存证截图"),
    GRID_PHOTO("3", "网格照"),;
    private String code;
    private String desc;

    EnumCompareImgFileType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumCompareImgFileType newInstance(String srcCode) {
        EnumCompareImgFileType result = null;
        for (EnumCompareImgFileType code : EnumCompareImgFileType.values()) {
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