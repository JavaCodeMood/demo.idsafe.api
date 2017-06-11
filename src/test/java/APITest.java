import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Date;
import java.util.UUID;

import demo.idsafe.api.util.MD5Utils;
import demo.idsafe.api.util.TestCaseUtil;


/**
 * 云慧眼接口调用测试用例
 *
 * @author geosmart
 * @date 2016-10-05
 */
public class APITest {
    //TODO 线上地址见对接文档中的服务描述，商户公钥，商户私钥，套餐编号商户开户时会下发到商户邮件
    //商户公钥
    static final String pub_key = "4ad2c7c4-f9fa-456b-92cd-056d5e5bcd59";

    //身份证正面OCR接口
    static final String IDCARD_FRONT_OCR = "http://10.1.30.51:8000/idsafe-front/front/4.3/api/idcard_front_photo_ocr/pub_key/" + pub_key;
    //身份证背面OCR接口
    static final String IDCARD_BACK_OCR = "http://10.1.30.51:8000/idsafe-front/front/4.3/api/idcard_back_photo_ocr/pub_key/" + pub_key;
    //实名验证接口
    static final String IDCARD_VERIFY = "http://10.1.30.51:8000/idsafe-front/front/4.3/api/idcard_verify/pub_key/" + pub_key;
    //人脸比对接口
    static final String IDCARD_VERIFY_AND_COMPARE = "http://10.1.30.51:8000/idsafe-front/front/4.3/api/face_compare/pub_key/" + pub_key;
    //OCR识别更新接口
    static final String UPDATE_OCR_INFO = "http://10.1.30.51:8000/front/4.3/api/update_ocr_info/pub_key/" + pub_key;
    //商户私钥
    static final String security_key = "2e6b6da8-77b9-4268-a8ba-8ff47ca7e6b6";


    //base64加密


    @Test
    public void test() throws IOException {
        //身份证正面OCR
//        IDcardFrontOCR("front.jpg");
        //身份证背面OCR
//        IDcardBackOCR("back.jpg");
//        UPDATEOCRINFO("王良仁","33032619930824413");
//        IDcardVerify("冯国良","13032219841008915X");
        IDcardVerifyAndLiving();
    }

    /**
     * 人脸比对接口-测试用例
     */
//    @Test
    public void test_idcard_verify() throws IOException, InterruptedException {

    }

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
     * 调用身份证正面OCR接口+调用身份证背面OCR接口+调用身份验证、人脸比对组合接口
     */
//    @Test
//    public void test_idcard_auth() throws IOException, InterruptedException {
//        //调用身份证正面OCR接口
//        JSONObject resp_front = IDcardFrontOCR("front.jpg");
//        if (resp_front.getJSONObject("result").getBoolean("success")) {
//            JSONObject frontOcrRes = resp_front.getJSONObject("data");
//            //获取套餐会话ID
//            String package_session_id = frontOcrRes.getString("package_session_id");
//            //调用身份证背面OCR接口
//            JSONObject resp_back = IDcardBackOCR("back.jpg", package_session_id);
//
//            if (resp_back.getJSONObject("result").getBoolean("success")) {
//                // 调用身份验证、人脸比对组合接口
//                JSONObject resp_living = IDcardVerifyAndLiving("living.jpg", package_session_id);
//            }
//        }
//    }

    /**
     * 身份验证和人脸对比接口-测试用例
     */
//    @Test
//    public void test_idcard_verify_and_compare() throws IOException, InterruptedException {
//        String session_id = "137250769479925760";
//        JSONObject resp_living = IDcardVerifyAndLiving("living.jpg", package_session_id);
//    }



    /**
     * 身份证正面OCR
     */
    JSONObject IDcardFrontOCR(String fileName) throws IOException {
        JSONObject reqJson = new JSONObject();
        //签名时间
        String sign_time = TestCaseUtil.getStringDate(new Date());
        //商户订单号
        String partner_order_id = UUID.randomUUID().toString();
        //加签串
        String sign = getMD5Sign(pub_key, partner_order_id, sign_time, security_key);
        //会话id()
        String session_id = "";
        //设置传入参数
        JSONObject header = new JSONObject();
        header.put("partner_order_id",partner_order_id);
        header.put("sign",sign);
        header.put("sign_time",sign_time);
        header.put("session_id",session_id);
        reqJson.put("header",header);
        JSONObject body = new JSONObject();
        body.put("idcard_front_photo",TestCaseUtil.getFileBase64Str(fileName));
        reqJson.put("body",body);

        System.out.println("身份证正面OCR识别-输入参数：" + JSON.toJSONString(reqJson, true));
        reqJson = TestCaseUtil.doHttpRequest(IDCARD_FRONT_OCR, reqJson);
        System.out.println("身份证正面OCR识别-输出结果：" + JSON.toJSONString(reqJson, true));
        return reqJson;
    }


    /**
     * 身份证背面OCR
     */
    JSONObject IDcardBackOCR(String fileName) throws IOException {
        JSONObject reqJson = new JSONObject();

        //调用OCR识别-反面
        String sign_time = TestCaseUtil.getStringDate(new Date());
        String partner_order_id = UUID.randomUUID().toString();
        String sign = getMD5Sign(pub_key, partner_order_id, sign_time, security_key);
        String session_id = "";
        JSONObject header = new JSONObject();
        header.put("partner_order_id",partner_order_id);
        header.put("sign",sign);
        header.put("sign_time",sign_time);
//        header.put("session_id",session_id);
        reqJson.put("header",header);
        JSONObject body = new JSONObject();
        body.put("idcard_back_photo",TestCaseUtil.getFileBase64Str(fileName));
        reqJson.put("body",body);

        System.out.println("身份证背面OCR识别-输入参数：" + JSON.toJSONString(reqJson, true));
        JSONObject resp_front = TestCaseUtil.doHttpRequest(IDCARD_BACK_OCR, reqJson);
        System.out.println("身份证背面OCR识别-输出结果：" + JSON.toJSONString(resp_front, true));
        return resp_front;
    }
    //OCR识别更新接口
    JSONObject UPDATEOCRINFO (String id_name,String id_number) throws IOException{
        JSONObject reqJson = new JSONObject();
        //调用OCR识别更新接口
        String sign_time = TestCaseUtil.getStringDate(new Date());
        String partner_order_id = UUID.randomUUID().toString();
        String sign = getMD5Sign(pub_key,partner_order_id,sign_time,security_key);
        JSONObject header = new JSONObject();
        header.put("partner_order_id",partner_order_id);
        header.put("sign",sign);
        header.put("sign_time",sign_time);
        reqJson.put("header",header);
        JSONObject body = new JSONObject();
        body.put("id_name",id_name);
        body.put("id_number",id_number);
        reqJson.put("body",body);
        System.out.println("OCR身份信息更新-输入参数：" + JSON.toJSONString(reqJson, true));
        JSONObject resp_front = TestCaseUtil.doHttpRequest(UPDATE_OCR_INFO, reqJson);
        System.out.println("OCR身份信息更新-输出结果：" + JSON.toJSONString(resp_front, true));
        return reqJson;
    }

    /**
     * 实名验证接口
     */
    JSONObject IDcardVerify(String id_number, String id_name) throws IOException {
        JSONObject reqJson = new JSONObject();
        //调用 身份验证、人脸比对组合接口
        String sign_time = TestCaseUtil.getStringDate(new Date());
        String partner_order_id = UUID.randomUUID().toString();
        String sign = getMD5Sign(pub_key, partner_order_id, sign_time, security_key);
        String verify_type = "1";
        JSONObject header = new JSONObject();
        header.put("partner_order_id", partner_order_id);
        header.put("sign", sign);
        header.put("sign_time", sign_time);
        reqJson.put("header",header);
        JSONObject body = new JSONObject();
        body.put("id_number", id_number);
        body.put("id_name", id_name);
        body.put("verify_type",verify_type);
        reqJson.put("body",body);

        System.out.println("实名验证接口-输入参数：" + JSON.toJSONString(reqJson, true));
        JSONObject resp_front = TestCaseUtil.doHttpRequest(IDCARD_VERIFY, reqJson);
        System.out.println("实名验证接口-识别结果：" + JSON.toJSONString(resp_front, true));
        return resp_front;
    }

    /**
     * 人脸比对接口
     */

    JSONObject  IDcardVerifyAndLiving() throws IOException {
        JSONObject reqJson = new JSONObject();
        //调用 身份验证、人脸比对组合接口
        String sign_time = TestCaseUtil.getStringDate(new Date());
        String partner_order_id = UUID.randomUUID().toString();
        String sign = getMD5Sign(pub_key, partner_order_id, sign_time, security_key);
        String img_file_source = "2";
        String img_file_type = "2";
        String img_file = TestCaseUtil.getFileBase64Str("front.jpg");

        JSONObject header = new JSONObject();
        JSONObject body = new JSONObject();
            header.put("partner_order_id",partner_order_id);
            header.put("sign", sign);
            header.put("sign_time", sign_time);
        reqJson.put("header",header);
        JSONObject photo1 = new JSONObject();
        photo1.put("img_file_source",img_file_source);
        photo1.put("img_file_type",img_file_type);
        photo1.put("img_file",img_file);
        body.put("photo1",photo1);

        JSONObject photo2 = new JSONObject();
        photo2.put("img_file_source",2);
        photo2.put("img_file_type",2);
        photo2.put("img_file",TestCaseUtil.getFileBase64Str("back.jpg"));
        body.put("photo2",photo2);
        reqJson.put("body",body);

        System.out.println("人脸比对接口-输入参数：" + JSON.toJSONString(reqJson, true));
        JSONObject resp_front = TestCaseUtil.doHttpRequest(IDCARD_VERIFY_AND_COMPARE, reqJson);
        System.out.println("人脸比对接口-输出结果：" + JSON.toJSONString(resp_front, true));
        return resp_front;
    }



    @After
    public void teardown() throws IOException {
        System.out.println("teardown...");
    }

}
