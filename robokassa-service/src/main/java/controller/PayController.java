package controller;

import Converter.PayConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.daru_jo.dto.PayDTO;
import service.PayService;

import java.util.List;

@RestController("/v1/pay")
public class PayController {

    private PayService payService;

    @Autowired
    public void setPayService(PayService payService) {
        this.payService = payService;
    }

    @PostMapping("/send")
    public void sendPay(@RequestParam PayDTO payDTO){
        payService.sendPay(PayConverter.getPay(payDTO));
    }
    @GetMapping("/{id}")
    public PayDTO sendPay(@PathVariable Long id){
        return PayConverter.getPayDto(payService.getPayList(id));
    }

    @GetMapping("/list")
    public List<PayDTO> getListPay(@RequestParam List<Long> orderId){
        return payService.getPayList(orderId).stream().map(PayConverter::getPayDto).toList();


    }

}
