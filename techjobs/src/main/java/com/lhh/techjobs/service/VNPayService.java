package com.lhh.techjobs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhh.techjobs.dto.request.PaymentRequest;
import com.lhh.techjobs.entity.Bill;
import com.lhh.techjobs.entity.Job;
import com.lhh.techjobs.repository.BillRepository;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private final RestTemplate restTemplate;
    private final HttpHeaders headers;
    private final BillRepository billRepository;
    private static final String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";


    public String createPaymentUrl(PaymentRequest request) throws Exception {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(request.getAmount() * 100)); // VNPay yêu cầu *100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");

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
        log.info("Hash Data for Payment: {}", hashData.toString());
        String paymentUrl = payUrl + "?" + query.toString();
        log.info("Payment URL generated: {}", paymentUrl);
        return paymentUrl;
    }

//    public String refundVNPay(String billId, String txnRef, String transNo, Integer amount, String transactionDate) throws Exception {
//        log.info("secret key: {}", hashSecret);
//        log.info("tmn code: {}", tmnCode);
//        String vnp_RequestId = "300" + billId;
//        String vnp_Version = "2.1.0";
//        String vnp_Command = "refund";
//        String vnp_TmnCode = tmnCode;
//        String vnp_CreateBy = "admin";
//        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//        String vnp_IpAddr = "127.0.0.1";
//
//        Map<String, String> vnp_Params = new HashMap<>();
//        vnp_Params.put("vnp_RequestId", vnp_RequestId);
//        vnp_Params.put("vnp_Version", vnp_Version);
//        vnp_Params.put("vnp_Command", vnp_Command);
//        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
//        vnp_Params.put("vnp_TransactionType", "02"); // 02 = full refund, 03 = partial
//        vnp_Params.put("vnp_TxnRef", txnRef);
//        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
//        vnp_Params.put("vnp_OrderInfo", "Test" + txnRef);
//        vnp_Params.put("vnp_TransactionNo", transNo);
//        vnp_Params.put("vnp_TransactionDate", transactionDate);
//        vnp_Params.put("vnp_CreateBy", vnp_CreateBy);
//        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
//        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
//
//        // Tạo chữ ký
//        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
//        Collections.sort(fieldNames);
//        StringBuilder hashData = new StringBuilder();
//        for (String fieldName : fieldNames) {
//            String fieldValue = vnp_Params.get(fieldName);
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
////                String encodedFieldName = URLEncoder.encode(fieldName, StandardCharsets.UTF_8);
//                String encodedFieldValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);
//                hashData.append(fieldName).append('=').append(fieldValue).append('&');
//            }
//        }
//        hashData.deleteCharAt(hashData.length() - 1);
//        log.info("Hash Data for Refund: {}", hashData.toString());
//
//        String vnp_SecureHash = hmacSHA512(hashSecret, hashData.toString());
//        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);
//
//        ObjectMapper mapper = new ObjectMapper();
//        String jsonBody = mapper.writeValueAsString(vnp_Params);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
//        log.info("request body: {}", requestEntity);

    /// /
    /// /        ResponseEntity<String> response = restTemplate.exchange(
    /// /                vnp_ApiUrl,
    /// /                HttpMethod.POST,
    /// /                requestEntity,
    /// /                String.class
    /// /        );
    /// /
    /// /        return response.getBody();
//        return null;
//    }
    public String refundVNPay(String billId, String txnRef, String transNo, Integer amount, String transactionDate) throws Exception {
        log.info("secret key: {}", hashSecret);
        log.info("tmn code: {}", tmnCode);
        String vnp_RequestId = "300" + billId;
        String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = tmnCode;
        String vnp_CreateBy = "admin";
        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String vnp_IpAddr = "127.0.0.1";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_RequestId", vnp_RequestId);
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TransactionType", "02"); // 02 = full refund, 03 = partial
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
        vnp_Params.put("vnp_OrderInfo", "Test" + txnRef);
        vnp_Params.put("vnp_TransactionNo", transNo);
        vnp_Params.put("vnp_TransactionDate", transactionDate);
        vnp_Params.put("vnp_CreateBy", vnp_CreateBy);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Tạo chữ ký
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName).append('=').append(fieldValue).append('&');
            }
        }
        hashData.deleteCharAt(hashData.length() - 1);
        log.info("Hash Data for Refund: {}", hashData.toString());

        String vnp_SecureHash = hmacSHA512(hashSecret, hashData.toString());
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL(vnp_ApiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonRequest = new Gson().toJson(vnp_Params);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(jsonRequest);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
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


    //    public static String hmacSHA512(String key, String data) throws Exception {
//        Mac hmac = Mac.getInstance("HmacSHA512");
//        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
//        hmac.init(secretKey);
//        byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
//
//        // Convert to hex string
//        StringBuilder sb = new StringBuilder(2 * hashBytes.length);
//        for (byte b : hashBytes) {
//            sb.append(String.format("%02x", b & 0xff));
//        }
//        return sb.toString();
//    }
    public static String hmacSHA512(String key, String data) throws Exception {
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac512.init(secretKeySpec);
        byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // convert to hex
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

}