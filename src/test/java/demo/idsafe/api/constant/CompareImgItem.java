package demo.idsafe.api.constant;


/**
 * 人脸比对照片参数
 *
 * @author geosmart
 * @date 2017/05/25
 */
public class CompareImgItem {
    /**
     * 照片来源
     * 字段值：
     * 1-会话ID（获取输入照片源）；
     * 2-有盾返回的照片URL地址；
     * 3- 照片的Base64格式字符串，照片可为有盾活体检测返回的活体清晰照或用户上传的清晰人脸照片，如手持证件照；
     */
    EnumCompareImgFileSource img_file_source;

    /**
     * 照片来源
     * 字段值：
     * 0-证件照；
     * 1-活体照；
     * 2-网格照；
     */
    EnumCompareImgFileType img_file_type;

    /**
     * 1）当img_file_source=1时，该字段值为会话ID;
     * 2）当img_file_source=2时，该字段值为URL地址；
     * 3）当img_file_source=3时，该字段值为照片的Base64格式字符串；
     */
    String img_file;

    public CompareImgItem() {
    }

    public CompareImgItem(EnumCompareImgFileSource img_file_source, EnumCompareImgFileType img_file_type, String image_file) {
        this.img_file_source = img_file_source;
        this.img_file_type = img_file_type;
        this.img_file = image_file;
    }


    public String getImg_file_source() {
        return img_file_source.getCode();
    }

    public void setImg_file_source(EnumCompareImgFileSource img_file_source) {
        this.img_file_source = img_file_source;
    }

    public String getImg_file_type() {
        return img_file_type.getCode();
    }

    public void setImg_file_type(EnumCompareImgFileType img_file_type) {
        this.img_file_type = img_file_type;
    }

    public String getImg_file() {
        return img_file;
    }

    public void setImg_file(String img_file) {
        this.img_file = img_file;
    }
}