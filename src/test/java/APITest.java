import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

import demo.idsafe.api.util.MD5Utils;
import demo.idsafe.api.util.TestCaseUtil;

/**
 * 测试异步通知接收接口
 *
 * @author geosmart
 * @date 2016-10-05
 */
public class APITest {
    //TODO 线上地址见对接文档中的服务描述，商户公钥，商户私钥，套餐编号商户开户时会下发到商户邮件
    //商户公钥
    final static String pub_key = "4ad2c7c4-f9fa-456b-92cd-056d5e5bcd59";

    static final String IDCARD_FRONT_OCR = "http://10.10.1.20:8080/idsafe-front/frontserver/4.2/api/idcard_front_photo_ocr/pubkey/" + pub_key;
    static final String IDCARD_BACK_OCR = "http://10.10.1.20:8080/idsafe-front/frontserver/4.2/api/idcard_back_photo_ocr/pubkey/" + pub_key;
    static final String IDCARDVERIFY_AND_COMPARE = "http://10.10.1.20:8080/idsafe-front/frontserver/4.2/api/idcard_verify_and_compare/pubkey/" + pub_key;
    static final String CHASET_UTF_8 = "UTF-8";
    //商户私钥
    final static String security_key = "2e6b6da8-77b9-4268-a8ba-8ff47ca7e6b6";
    //套餐编号
    final static String package_code = "";

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
    @Test
    public void test_idcard_auth() throws IOException, InterruptedException {
        //调用身份证正面OCR接口
        JSONObject resp_front = IDcardFrontOCR("front.jpg");
        if (resp_front.getJSONObject("result").getBoolean("success")) {
            JSONObject frontOcrRes = resp_front.getJSONObject("data");
            //获取套餐会话ID
            String package_session_id = frontOcrRes.getString("package_session_id");
            //调用身份证背面OCR接口
            JSONObject resp_back = IDcardBackOCR("back.jpg", package_session_id);

            if (resp_back.getJSONObject("result").getBoolean("success")) {
                // 调用身份验证、人脸比对组合接口
                JSONObject resp_living = IDcardVerifyAndLiving("living.jpg", package_session_id);
            }
        }
    }

    @Test
    public void test_idcard_verify_and_compare() throws IOException, InterruptedException {
        String package_session_id = "137250769479925760";
        JSONObject resp_living = IDcardVerifyAndLiving("living.jpg", package_session_id);
    }

    /**
     * 身份证正面OCR
     */
    JSONObject IDcardFrontOCR(String fileName) throws IOException {
        JSONObject reqJson = new JSONObject();
        //调用OCR识别-正面
        String sign_time = TestCaseUtil.getStringDate(new Date());
        String partner_order_id = UUID.randomUUID().toString();
        String sign = getMD5Sign(pub_key, partner_order_id, sign_time, security_key);
        reqJson.put("pub_key", pub_key);
        reqJson.put("package_code", package_code);
        reqJson.put("partner_order_id", partner_order_id);
        reqJson.put("extention_info", null);
        reqJson.put("sign", sign);
        reqJson.put("sign_time", sign_time);
        reqJson.put("idcard_front_photo", TestCaseUtil.getFileBase64Str(fileName));

        System.out.println("身份证正面OCR识别参数：" + JSON.toJSONString(reqJson, true));
        JSONObject resp_front = TestCaseUtil.doHttpRequest(IDCARD_FRONT_OCR, reqJson);
        System.out.println("身份证正面OCR识别结果：" + JSON.toJSONString(resp_front, true));
        return resp_front;
    }


    /**
     * 身份证背面OCR
     */
    JSONObject IDcardBackOCR(String fileName, String package_session_id) throws IOException {
        JSONObject reqJson = new JSONObject();
        //调用OCR识别-正面
        String sign_time = TestCaseUtil.getStringDate(new Date());
        String partner_order_id = UUID.randomUUID().toString();
        String sign = getMD5Sign(pub_key, partner_order_id, sign_time, security_key);
        reqJson.put("pub_key", pub_key);
        reqJson.put("package_code", package_code);
        reqJson.put("package_session_id", package_session_id);
        reqJson.put("partner_order_id", partner_order_id);
        reqJson.put("extention_info", null);
        reqJson.put("sign", sign);
        reqJson.put("sign_time", sign_time);
        reqJson.put("idcard_back_photo", TestCaseUtil.getFileBase64Str(fileName));

        System.out.println("身份证背面OCR识别参数：" + JSON.toJSONString(reqJson, true));
        JSONObject resp_front = TestCaseUtil.doHttpRequest(IDCARD_BACK_OCR, reqJson);
        System.out.println("身份证背面OCR识别结果：" + JSON.toJSONString(resp_front, true));
        return resp_front;
    }

    /**
     * 身份验证、人脸比对组合接口
     */
    JSONObject IDcardVerifyAndLiving(String fileName, String package_session_id) throws IOException {
        JSONObject reqJson = new JSONObject();
        //调用 身份验证、人脸比对组合接口
        String sign_time = TestCaseUtil.getStringDate(new Date());
        String partner_order_id = UUID.randomUUID().toString();
        String sign = getMD5Sign(pub_key, partner_order_id, sign_time, security_key);
        reqJson.put("pub_key", pub_key);
        reqJson.put("package_code", package_code);
        reqJson.put("package_session_id", package_session_id);
        reqJson.put("partner_order_id", partner_order_id);
        reqJson.put("extention_info", null);
        reqJson.put("sign", sign);
        reqJson.put("sign_time", sign_time);
        reqJson.put("living_photo", TestCaseUtil.getFileBase64Str(fileName));

        System.out.println("身份验证、人脸比对组合接口识别参数：" + JSON.toJSONString(reqJson, true));
        JSONObject resp_front = TestCaseUtil.doHttpRequest(IDCARDVERIFY_AND_COMPARE, reqJson);
        System.out.println("身份验证、人脸比对组合接口识别结果：" + JSON.toJSONString(resp_front, true));
        return resp_front;
    }

    @After
    public void teardown() throws IOException {
        System.out.println("teardown...");
    }

}
