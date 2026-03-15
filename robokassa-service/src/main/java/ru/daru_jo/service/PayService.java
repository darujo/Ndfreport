package ru.daru_jo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.repository.PayRepository;
import ru.daru_jo.entity.Pay;
import ru.daru_jo.exceptions.ResourceNotFoundRunTime;
import ru.daru_jo.specifications.Specifications;

import java.util.List;

@Service
public class PayService {

    private PayRepository payRepository;

    @Autowired
    public void setPayRepository(PayRepository payRepository) {
        this.payRepository = payRepository;
    }

    public  Pay sendPay(Pay pay){
        getHash(pay);
        if(pay.getMagCode() == null){
            pay.setMagCode(merchantLogin);
        }

        return save(pay);

    }
    public Pay save (Pay pay){
        return payRepository.save(pay);
    }

    @Value("${robokassa.password1}")
    private String password1;

    @Value("${robokassa.merchantLogin}")
    private String merchantLogin;

    public void getHash(Pay pay){
        if (pay.getHash() == null){
            String string = merchantLogin + ":" + pay.getAmount() + ":" + pay.getOrderId() + ":" + password1;
            pay.setHash(GFG2.getMD5(string));
        }
    }

    public Pay getPayList(Long id) {
        return payRepository.findById(id).orElseThrow(() ->  new ResourceNotFoundRunTime("Не найден запрос платежа с id = " + id));
    }

    public Page<Pay> getPayList(List<Long> oderIdList) {
        Specification<Pay> sp = Specification.unrestricted();
        sp = Specifications.in(sp,"orderId", oderIdList);
        return new PageImpl<>(payRepository.findAll(sp));
    }

    public Pay getPay(Long orderId) {
        return payRepository.findByOrderId(orderId).orElseThrow(() ->  new ResourceNotFoundRunTime("Не найден запрос платежа с id = " + orderId));
    }
}
