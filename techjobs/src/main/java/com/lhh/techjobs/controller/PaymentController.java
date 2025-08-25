package com.lhh.techjobs.controller;

import com.lhh.techjobs.dto.request.PaymentRequest;
import com.lhh.techjobs.dto.response.PaymentResponse;
import com.lhh.techjobs.dto.response.PaymentResultResponse;
import com.lhh.techjobs.service.BillService;
import com.lhh.techjobs.service.VNPayService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final VNPayService vnPayService;
    @Value("${vnpay.HashSecret}")
    private String hashSecret;

    @PostMapping("/create-payment")
    public ResponseEntity<Map<String, String>> createPayment(@Valid @RequestBody PaymentRequest request) throws Exception {

        String paymentUrl = vnPayService.createPaymentUrl(request);
        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        log.info("Payment URL: {}", paymentUrl);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<Map<String, String>> vnpayReturn(@RequestParam Map<String, String> vnp_Params) throws Exception {
        Map<String, String> response = new HashMap<>();

        try {

            String secureHash = vnp_Params.get("vnp_SecureHash");
            String responseCode = vnp_Params.get("vnp_ResponseCode");
            String txnRef = vnp_Params.get("vnp_TxnRef");
            String vnpAmount = vnp_Params.get("vnp_Amount");


            System.out.println("--- VNPAY RETURN LOG ---");
            System.out.println("vnp_Params received: " + vnp_Params);
            System.out.println("vnp_SecureHash: " + secureHash);
            System.out.println("vnp_ResponseCode: " + responseCode);
            System.out.println("vnp_TxnRef: " + txnRef);
            System.out.println("-------------------------");

            if (secureHash == null || responseCode == null || txnRef == null) {
                System.err.println("Lỗi: Thiếu tham số quan trọng từ VNPAY.");
                response.put("status", "error");
                response.put("message", "Thiếu tham số quan trọng (Missing required parameters).");
                return ResponseEntity.badRequest().body(response);
            }


            Map<String, String> fieldsForVerification = new HashMap<>(vnp_Params);
            fieldsForVerification.remove("vnp_SecureHash");


            String newSecureHash = VNPayService.hashAllFields(fieldsForVerification, hashSecret);

            if (newSecureHash.equals(secureHash)) {

                if ("00".equals(responseCode)) {
                    try {
                        response.put("status", "success");
                    } catch (NumberFormatException e) {
                        System.err.println("Lỗi: Mã đơn hàng không hợp lệ. " + e.getMessage());
                        response.put("status", "error");
                        response.put("message", "Thanh toán thành công nhưng mã đơn hàng không hợp lệ.");
                    } catch (Exception e) {

                        System.err.println("Lỗi khi cập nhật đơn hàng: " + e.getMessage());
                        e.printStackTrace();
                        response.put("status", "error");
                        response.put("message", "Thanh toán thành công nhưng cập nhật đơn hàng thất bại.");
                    }
                } else {
                    response.put("status", "error");
                    response.put("message", "Thanh toán thất bại. Mã lỗi: " + responseCode);
                }
            } else {

                response.put("status", "error");
                response.put("message", "Dữ liệu không hợp lệ (Invalid SecureHash).");
            }
        } catch (Exception e) {

            System.err.println("Lỗi không xác định xảy ra: " + e.getMessage());
            e.printStackTrace(); // In toàn bộ stack trace để tìm ra nguyên nhân
            response.put("status", "error");
            response.put("message", "Có lỗi hệ thống xảy ra.");
        }

        return ResponseEntity.ok(response);
    }
}
