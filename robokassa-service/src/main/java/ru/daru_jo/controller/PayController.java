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

    @PostMapping("/pay")
    public String getListPay(@RequestPart(required = false) String OutSum,
                             @RequestPart(required = false) String InvId,
                             @RequestPart(required = false) String Fee,
                             @RequestPart(required = false) String EMail,
                             @RequestPart(required = false) String SignatureValue,
                             @RequestPart(required = false) String PaymentMethod,
                             @RequestPart(required = false) String IncCurrLabel


    ) {
        log.info(OutSum);
        log.info(InvId);
        log.info(Fee);
        log.info(EMail);
        log.info(SignatureValue);
        log.info(PaymentMethod);
        log.info(IncCurrLabel);
        return "OK" + InvId;
    }

    @PostMapping("/pay2")
    public String getListPay(@RequestBody String text) {
        log.info(text);
        return "ok101";
    }

}
