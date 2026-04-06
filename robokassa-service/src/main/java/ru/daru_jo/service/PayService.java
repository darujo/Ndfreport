package ru.daru_jo.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.hash.HashService;
import ru.daru_jo.repository.PayRepository;
import ru.daru_jo.entity.Pay;
import ru.daru_jo.exceptions.ResourceNotFoundRunTime;
import ru.daru_jo.specifications.Specifications;

import java.text.DecimalFormat;
import java.util.List;

@Slf4j
@Service
public class PayService {

    private PayRepository payRepository;

    @Autowired
    public void setPayRepository(PayRepository payRepository) {
        this.payRepository = payRepository;
    }

    public Pay sendPay(Pay pay) {
        setHash(pay);
        if (pay.getMagCode() == null) {
            pay.setMagCode(merchantLogin);
        }
        if(pay.getId() == null) {
            try {
                Pay savePay = getPay(pay.getOrderId());
                if (savePay != null) {
                    pay.setId(savePay.getId());
                }
            } catch (ResourceNotFoundRunTime ignored){

            }
        }
        return save(pay);

    }

    public Pay save(Pay pay) {
        return payRepository.save(pay);
    }

    @Value("${robokassa.password1}")
    private String password1;

    @Value("${robokassa.password2}")
    private String password2;
    @Value("${robokassa.merchantLogin}")
    private String merchantLogin;

    public void setHash(Pay pay) {
//        if (pay.getHash() == null) {
            pay.setHash(getHash(pay));
//        }
    }

    public String getHash(Pay pay) {
//        if (pay.getHash() == null) {
        String string = merchantLogin + ":" + delZero(pay.getAmount()) + ":" + pay.getOrderId() + ":" + password1;
        return getHash(string);
//        }
    }

    private String delZero(Double amount) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(amount).replace(",",".");
    }

    public Pay getPayList(Long id) {
        return payRepository.findById(id).orElseThrow(() -> new ResourceNotFoundRunTime("Не найден запрос платежа с id = " + id));
    }

    public Page<Pay> getPayList(List<Long> oderIdList) {
        Specification<Pay> sp = Specification.unrestricted();
        sp = Specifications.in(sp, "orderId", oderIdList);
        return new PageImpl<>(payRepository.findAll(sp));
    }

    public Pay getPay(Long orderId) {
        Pay pay = payRepository.findByOrderId(orderId).orElseThrow(() -> new ResourceNotFoundRunTime("Не найден запрос платежа с id = " + orderId));
        if(!pay.getHash().equals(getHash(pay))){
            setHash(pay);
            payRepository.save(pay);
        }
        return pay;
    }

    @PostConstruct
    public void init() {
        log.info(getHash("1990.9:189:" + password1));
        log.info(getHash("1990.9:189:" + password2));
    }

    public boolean payOk(String outSum, String invId, String fee, String eMail, String signatureValue, String paymentMethod, String incCurrLabel) {

        String string = outSum + ":" + invId + ":" + password2;
        if (!signatureValue.equalsIgnoreCase(getHash(string))) {
            return false;
        }

        Pay pay = getPay(Long.parseLong(invId));
        if (!pay.getAmount().equals(Double.parseDouble(outSum))){
            return false;
        }
        pay.setFee(Double.parseDouble(fee));
        pay.setEmail(eMail);
        pay.setHashOk(signatureValue);
        pay.setIncCurrLabel(incCurrLabel);
        pay.setPaymentMethod(paymentMethod);
        pay.setCompleted(true);
        payRepository.save(pay);
        return true;
    }

    private String getHash(String text) {
        return HashService.getSHA256(text).toUpperCase();
    }
}
