package com.fink.fooddelivery.payment;

import com.fink.fooddelivery.shared.contract.ChargeRequest;
import com.fink.fooddelivery.shared.contract.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse charge(@Valid @RequestBody ChargeRequest request) {
        return paymentService.charge(request);
    }
}
