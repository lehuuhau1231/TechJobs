package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.request.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayService {

    @Value("${vnpay.TmnCode}")
    private String tmnCode;

    @Value("${vnpay.HashSecret}")
    private String hashSecret;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    @Value("${vnpay.payUrl}")
    private String payUrl;


    public String createPaymentUrl(PaymentRequest request) throws Exception {
        log.info("Hash secret: '{}'", hashSecret);
        log.info("tmnCode: '{}'", tmnCode);
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(request.getAmount() * 100)); // VNPay yêu cầu *100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_IpAddr","127.0.0.1");

        vnp_Params.put("vnp_TxnRef", String.valueOf(request.getBillId()));
        vnp_Params.put("vnp_OrderInfo", "Test" + request.getBillId());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", createDate);

        cld.add(Calendar.MINUTE, 15); // Hết hạn sau 15p
        String expireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", expireDate);

        // build query
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Iterator<String> it = fieldNames.iterator(); it.hasNext(); ) {
            String fieldName = it.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Mã hóa URL cho cả tên và giá trị trường
                String encodedFieldName = URLEncoder.encode(fieldName, StandardCharsets.UTF_8);
                String encodedFieldValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);

                hashData.append(fieldName).append('=').append(encodedFieldValue);

                // Nối vào chuỗi hashData
//                hashData.append(encodedFieldName).append('=').append(encodedFieldValue);

                // Nối vào chuỗi query
                query.append(encodedFieldName).append('=').append(encodedFieldValue);

                if (it.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);
        log.info("===== VNPay Request =====");
        log.info("HashData (request): {}", hashData.toString());
        log.info("SecureHash (request): {}", secureHash);
        String paymentUrl = payUrl + "?" + query.toString();
        log.info("Payment URL generated: {}", paymentUrl);
        return paymentUrl;
    }

    public static String hashAllFields(Map<String, String> fields, String secretKey) throws Exception {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder sb = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                // Mã hóa URL cho cả tên và giá trị trường
                String encodedFieldValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);

                sb.append(fieldName).append("=").append(encodedFieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }


        return hmacSHA512(secretKey, sb.toString());
    }


    public static String hmacSHA512(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac.init(secretKey);
        byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // Convert to hex string
        StringBuilder sb = new StringBuilder(2 * hashBytes.length);
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

}