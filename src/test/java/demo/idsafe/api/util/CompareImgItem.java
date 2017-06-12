package com.udcredit.idsafe.client.front.constant;


import com.udcredit.biss.json.JSONException;
import com.udcredit.biss.json.JSONObject;
import com.udcredit.idsafe.client.front.helper.OSSHelper;
import com.udcredit.idsafe.client.service.OrderAuthExtService;
import com.udcredit.idsafe.client.service.OrderAuthService;
import com.udcredit.idsafe.client.service.PackageSessionOrderService;
import com.udcredit.idsafe.inf.beans.PackageSessionOrder;
import com.udcredit.idsafe.inf.beans.mongodb.OrderAuthExt;
import com.udcredit.idsafe.utils.Validator;

import java.io.IOException;

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

    public static CompareImgItem newInstance(String jsonStr) throws JSONException, IOException {
        JSONObject json = new JSONObject(jsonStr);
        EnumCompareImgFileSource fileSource = EnumCompareImgFileSource.newInstance(json.optString("img_file_source"));
        //照片类型不传，默认值设置为1：活体照
        EnumCompareImgFileType fileType = EnumCompareImgFileType.newInstance(json.optString("img_file_type", "1"));
        if (fileSource == null || fileType == null) {
            return null;
        }
        String file = json.optString("img_file");
        if (fileSource.equals(EnumCompareImgFileSource.BASE_64)) {
            //base64转码时，加号会变成空格；所以需在转码后把空格转加号
            file = file.replace("\r\n", "").replace("\n", "");
            file = file.replace(" ", "+");
            json.put("img_file", file);
            Validator.photoParamValidator(file, null, APIConfig.ocrFrontPhotoMaxSize);
        }
        return new CompareImgItem(fileSource, fileType, file);
    }


    /**
     * 获取照片名称
     */
    public static String getImgName(Long blockId, String partnerCode, String imgFile, EnumCompareImgFileSource imgFileSource, EnumCompareImgFileType imgFileType, boolean isAPICall) {
        String photoName = null;
        switch (imgFileSource) {
            case SESSION_ID:
                photoName = getImgNameBySessionId(blockId, imgFile, partnerCode, imgFileType, isAPICall);
                break;
            case URL:
                //从URL获取照片名称
                photoName = OrderAuthService.getImgNameByDownloadUrl(blockId, partnerCode, imgFile);
                break;
            case BASE_64:
                //上传到阿里云OSS
                photoName = OSSHelper.uploadBase64Photo(imgFile, blockId);
                break;
        }
        return photoName;
    }

    /**
     * 通过会话ID从关联订单获取照片名称
     */
    public static String getImgNameBySessionId(Long blockId, String sessionId, String partnerCode, EnumCompareImgFileType enumCompareImgFileType, boolean isAPICall) {
        String photoName = null;
        switch (enumCompareImgFileType) {
            case IDCARD_PORTRAIT_PHOTO:
                //从OCR正面订单获取照片名称
                PackageSessionOrder OcrFSessionOrder = PackageSessionOrderService.singleQuery(blockId, partnerCode, sessionId, EnumProductCode.OCR_FRONT);
                if (OcrFSessionOrder != null) {
                    OrderAuthExt orderExt = OrderAuthExtService.singleQuery(blockId, OcrFSessionOrder.getOidAuthorder());
                    photoName = orderExt.getPhoto_info().getPhoto_get();
                }
                break;
            case LIVING_PHOTO:
                //从活体检测订单获取照片名称
                PackageSessionOrder livingSessionOrder = PackageSessionOrderService.singleQuery(blockId, partnerCode, sessionId, EnumProductCode.LIVING_DETECT);
                if (livingSessionOrder != null) {
                    OrderAuthExt orderExt = OrderAuthExtService.singleQuery(blockId, livingSessionOrder.getOidAuthorder());
                    photoName = orderExt.getPhoto_info().getPhoto_living();
                }
                if (isAPICall) {
                    //从活体检测(视频唇语)订单获取照片名称
                    PackageSessionOrder livingVideoSessionOrder = PackageSessionOrderService.singleQuery(blockId, partnerCode, sessionId, EnumProductCode.LIVING_DETECT_BY_VIDEO);
                    if (livingVideoSessionOrder != null) {
                        OrderAuthExt orderExt = OrderAuthExtService.singleQuery(blockId, livingVideoSessionOrder.getOidAuthorder());
                        photoName = orderExt.getPhoto_info().getPhoto_living();
                    }
                }
                break;
            case VIDEO_PHOTO:
                //从视频存证订单获取照片名称
                PackageSessionOrder videoSessionOrder = PackageSessionOrderService.singleQuery(blockId, partnerCode, sessionId, EnumProductCode.VIDEO_AUTH);
                if (videoSessionOrder != null) {
                    OrderAuthExt orderExt = OrderAuthExtService.singleQuery(blockId, videoSessionOrder.getOidAuthorder());
                    photoName = orderExt.getPhoto_info().getVideo_living_screenshot();
                }
                break;
            case GRID_PHOTO:
                //从实名验证订单获取照片名称
                PackageSessionOrder verifySessionOrder = PackageSessionOrderService.singleQuery(blockId, partnerCode, sessionId, EnumProductCode.VERIFY_RETURN_PHOTO);
                if (verifySessionOrder != null) {
                    OrderAuthExt orderExt = OrderAuthExtService.singleQuery(blockId, verifySessionOrder.getOidAuthorder());
                    photoName = orderExt.getPhoto_info().getPhoto_branch();
                }
                break;
        }
        return photoName;
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