package demo.idsafe.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

import demo.idsafe.api.constant.CompareImgItem;
import demo.idsafe.api.util.MD5Utils;
import demo.idsafe.api.util.TestCaseUtil;

import static demo.idsafe.api.constant.EnumCompareImgFileSource.BASE_64;
import static demo.idsafe.api.constant.EnumCompareImgFileSource.SESSION_ID;
import static demo.idsafe.api.constant.EnumCompareImgFileType.IDCARD_PORTRAIT_PHOTO;
import static demo.idsafe.api.constant.EnumCompareImgFileType.LIVING_PHOTO;


/**
 * 云慧眼接口调用测试用例
 *
 * @author geosmart
 * @date 2017-06-12
 */
public class APITest {
    //TODO 线上地址见对接文档中的服务描述，商户公钥，商户私钥，套餐编号商户开户时会下发到商户邮件
    //商户公钥
    static final String pub_key = "4ad2c7c4-f9fa-456b-92cd-056d5e5bcd59";
    //商户私钥
    static final String security_key = "2e6b6da8-77b9-4268-a8ba-8ff47ca7e6b6";

    //身份证正面OCR接口
    static final String IDCARD_FRONT_OCR = "http://10.1.30.51:8000/idsafe-front/front/4.3/api/idcard_front_photo_ocr/pub_key/" + pub_key;
    //身份证背面OCR接口
    static final String IDCARD_BACK_OCR = "http://10.1.30.51:8000/idsafe-front/front/4.3/api/idcard_back_photo_ocr/pub_key/" + pub_key;
    //OCR识别更新接口
    static final String UPDATE_OCR_INFO = "http://10.1.30.51:8000/idsafe-front/front/4.3/api/update_ocr_info/pub_key/" + pub_key;

    //实名验证接口
    static final String IDCARD_VERIFY = "http://10.1.30.51:8000/idsafe-front/front/4.3/api/idcard_verify/pub_key/" + pub_key;
    //人脸比对接口
    static final String FACE_COMPARE = "http://10.1.30.51:8000/idsafe-front/front/4.3/api/face_compare/pub_key/" + pub_key;

    //活体检测接口
    static final String LVING_DETECTION = "http://10.1.30.51:8000/idsafe-front/front/4.3/api/living_detection/pub_key/" + pub_key;
    //活体检测唇语验证接口
    static final String VALIDATEDATA = "http://10.1.30.51:8000/idsafe-front/front/4.3/api/get_living_validate_data/pub_key/" + pub_key;

    /**
     * 生成md5签名
     */
    public static String getMD5Sign(String pub_key, String partner_order_id, String sign_time, String security_key) throws UnsupportedEncodingException {
        String signStr = String.format("pub_key=%s|partner_order_id=%s|sign_time=%s|security_key=%s", pub_key, partner_order_id, sign_time, security_key);
        System.out.println("测试输入签名signField：" + signStr);
        return MD5Utils.MD5Encrpytion(signStr.getBytes("UTF-8"));
    }

    @Before
    public void setup() throws IOException {
        System.out.println("setup...");
    }

    /**
     * 身份证OCR-测试用例
     */
    @Test
    public void test_ocr() throws IOException {
        //身份证正面OCR
        idCardFrontOCR("front.jpg");
        //身份证背面OCR
        idCardBackOCR("back.jpg");
        //身份证正面OCR识别结果更新
        updateIdcardFrontOCR("王良仁", "330326199308224113");
    }

    /**
     * 活体检测-测试用例
     */
    @Test
    public void test_living_detect() throws IOException {
        String fileName = "";
        //获取-活体检测唇语验证码
        LivingValidateData();
        //活体检测
        LivingDetection(fileName);
    }

    /**
     * 实名验证与人脸比对-测试用例
     */
    @Test
    public void test_verify_and_compare() throws IOException {
        //实名验证
        idCardVerify("13032219841008915X", "冯国良");
        //人脸比对
        faceCompare();
    }

    /**
     * 生成请求header
     */
    JSONObject getRequestHeader(String session_id) throws IOException {
        JSONObject header = new JSONObject();
        if (session_id != null && !session_id.equals("")) {
            header.put("session_id", session_id);
        }
        String sign_time = TestCaseUtil.getStringDate(new Date());
        String partner_order_id = UUID.randomUUID().toString();
        String sign = getMD5Sign(pub_key, partner_order_id, sign_time, security_key);
        header.put("partner_order_id", partner_order_id);
        header.put("sign", sign);
        header.put("sign_time", sign_time);
        return header;
    }

    /**
     * 身份证正面OCR
     */
    JSONObject idCardFrontOCR(String fileName) throws IOException {
        JSONObject reqJson = new JSONObject();
        //调用OCR识别-正面
        reqJson.put("header", getRequestHeader(""));
        JSONObject body = new JSONObject();
        body.put("idcard_front_photo", TestCaseUtil.getFileBase64Str(fileName));
        reqJson.put("body", body);

        System.out.println("身份证正面OCR识别-输入参数：" + JSON.toJSONString(reqJson, true));
        reqJson = TestCaseUtil.doHttpRequest(IDCARD_FRONT_OCR, reqJson);
        System.out.println("身份证正面OCR识别-输出结果：" + JSON.toJSONString(reqJson, true));
        return reqJson;
    }

    /**
     * 身份证反面OCR
     */
    JSONObject idCardBackOCR(String fileName) throws IOException {
        JSONObject reqJson = new JSONObject();
        //调用OCR识别-反面
        reqJson.put("header", getRequestHeader(""));
        JSONObject body = new JSONObject();
        body.put("idcard_back_photo", TestCaseUtil.getFileBase64Str(fileName));
        reqJson.put("body", body);

        System.out.println("身份证背面OCR识别-输入参数：" + JSON.toJSONString(reqJson, true));
        JSONObject resp_front = TestCaseUtil.doHttpRequest(IDCARD_BACK_OCR, reqJson);
        System.out.println("身份证背面OCR识别-输出结果：" + JSON.toJSONString(resp_front, true));
        return resp_front;
    }

    /**
     * 身份证正面OCR识别结果更新
     */
    JSONObject updateIdcardFrontOCR(String id_name, String id_number) throws IOException {
        JSONObject reqJson = new JSONObject();
        //调用OCR识别更新
        JSONObject jsonObject = idCardFrontOCR("front.jpg");
        JSONObject json = jsonObject.getJSONObject("data");
        String session_id = json.getString("session_id");
        reqJson.put("header", getRequestHeader(session_id));

        JSONObject body = new JSONObject();
        body.put("id_name", id_name);
        body.put("id_number", id_number);
        reqJson.put("body", body);
        System.out.println("OCR身份信息更新-输入参数：" + JSON.toJSONString(reqJson, true));
        JSONObject resp_front = TestCaseUtil.doHttpRequest(UPDATE_OCR_INFO, reqJson);
        System.out.println("OCR身份信息更新-输出结果：" + JSON.toJSONString(resp_front, true));
        return reqJson;
    }

    /**
     * 实名验证接口
     */
    JSONObject idCardVerify(String id_number, String id_name) throws IOException {
        JSONObject reqJson = new JSONObject();
        reqJson.put("header", getRequestHeader(""));

        JSONObject body = new JSONObject();
        body.put("id_number", id_number);
        body.put("id_name", id_name);
        body.put("verify_type", "1");
        reqJson.put("body", body);

        System.out.println("实名验证接口-输入参数：" + JSON.toJSONString(reqJson, true));
        JSONObject resp_front = TestCaseUtil.doHttpRequest(IDCARD_VERIFY, reqJson);
        System.out.println("实名验证接口-识别结果：" + JSON.toJSONString(resp_front, true));
        return resp_front;
    }

    /**
     * 人脸比对接口
     */

    JSONObject faceCompare() throws IOException {
        JSONObject reqJson = new JSONObject();
        reqJson.put("header", getRequestHeader(null));

        JSONObject body = new JSONObject();
        reqJson.put("body", body);

        //photo1参数
        String sessionId = "196139620663033856";
        CompareImgItem comparePhoto1 = new CompareImgItem(SESSION_ID, IDCARD_PORTRAIT_PHOTO, sessionId);
        body.put("photo1", comparePhoto1);

        //photo2参数
        String base64Photo = TestCaseUtil.getFileBase64Str("living.jpg");
        CompareImgItem comparePhoto2 = new CompareImgItem(BASE_64, LIVING_PHOTO, base64Photo);
        body.put("photo2", comparePhoto2);


        System.out.println("人脸比对接口-输入参数：" + JSON.toJSONString(reqJson, true));
        JSONObject resp_front = TestCaseUtil.doHttpRequest(FACE_COMPARE, reqJson);
        System.out.println("人脸比对接口-输出结果：" + JSON.toJSONString(resp_front, true));
        return resp_front;
    }

    /**
     * 活体检测接口
     */
    JSONObject LivingDetection(String fileName) throws IOException {
        JSONObject reqJson = new JSONObject();
        //调用活体检测
        JSONObject jsonObject = LivingValidateData();
        JSONObject json = jsonObject.getJSONObject("data");
        String session_id = json.getString("session_id");
        reqJson.put("header", getRequestHeader(session_id));

        JSONObject body = new JSONObject();
        body.put("living_video", TestCaseUtil.getFileBase64Str(fileName));
        reqJson.put("body", body);

        System.out.println("活体检测接口-输入参数：" + JSON.toJSONString(reqJson, true));
        JSONObject resp_front = TestCaseUtil.doHttpRequest(LVING_DETECTION, reqJson);
        System.out.println("活体检测接口-输出结果：" + JSON.toJSONString(resp_front, true));
        return resp_front;
    }

    /**
     * 活体检测唇语验证接口
     */
    JSONObject LivingValidateData() throws IOException {
        JSONObject reqJson = new JSONObject();
        //调用活体检测唇语
        reqJson.put("header", getRequestHeader(""));
        System.out.println("活体检测唇语验证接口-输入参数：" + JSON.toJSONString(reqJson, true));
        JSONObject res = TestCaseUtil.doHttpRequest(VALIDATEDATA, reqJson);
        System.out.println("活体检测唇语验证接口-输出结果：" + JSON.toJSONString(res, true));
        return res;
    }

    @After
    public void teardown() throws IOException {
        System.out.println("teardown...");
    }

}
