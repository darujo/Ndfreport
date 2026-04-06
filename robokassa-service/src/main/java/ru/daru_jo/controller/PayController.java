package ru.daru_jo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import ru.daru_jo.converter.PayConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.daru_jo.dto.PayDTO;
import ru.daru_jo.service.PayService;

import java.util.List;

@Slf4j
@RestController()
@RequestMapping("/v1/pay")
public class PayController {

    private PayService payService;

    @Autowired
    public void setPayService(PayService payService) {
        this.payService = payService;
    }

    @PostMapping("/send")
    public PayDTO sendPay(@RequestBody(required = false) PayDTO payDTO) {
        return PayConverter.getPayDto(payService.sendPay(PayConverter.getPay(payDTO)));
    }

    @GetMapping("/{id}")
    public PayDTO sendPay(@PathVariable Long id) {
        return PayConverter.getPayDto(payService.getPayList(id));
    }

    @GetMapping("/list")
    public PagedModel<?> getListPay(@RequestParam List<Long> orderId,
                                    PagedResourcesAssembler<PayDTO> pagedAssembler) {
        return pagedAssembler.toModel(payService.getPayList(orderId).map(PayConverter::getPayDto));
    }

    @GetMapping("/order")
    public PayDTO getListPay(@RequestParam Long orderId) {
        return PayConverter.getPayDto(payService.getPay(orderId));
    }

    @GetMapping("/pay")
    public String getListPay3(@RequestParam(required = false, name = "OutSum") String outSum,
                              @RequestParam(required = false, name = "InvId") String invId,
                              @RequestParam(required = false, name = "Fee") String fee,
                              @RequestParam(required = false, name = "EMail") String eMail,
                              @RequestParam(required = false, name = "SignatureValue") String signatureValue,
                              @RequestParam(required = false, name = "PaymentMethod") String paymentMethod,
                              @RequestParam(required = false, name = "IncCurrLabel") String incCurrLabel


    ) {
        if (payService.payOk(outSum, invId, fee, eMail, signatureValue, paymentMethod, incCurrLabel)) {
            return "OK" + invId;
        } else {
            return "SignatureValue not Correct";
        }
    }
}
