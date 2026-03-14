package Converter;

import ru.daru_jo.dto.PayDTO;
import ru.daru_jo.entity.Pay;

public class PayConverter {
    public static PayDTO getPayDto(Pay pay){
        return new PayDTO(pay.getId(), pay.getMagCode(), pay.getOrderId(), pay.getAmount(), pay.getDescription(), pay.getHash(), pay.getEmail(), pay.isTest() ? 1 : null,pay.isCompleted());
    }
    public static Pay getPay(PayDTO pay){
        return new Pay(pay.getId(), pay.getMerchantLogin(), pay.getInvId(), pay.getOutSum(), pay.getDescription(), pay.getSignatureValue(), pay.getEmail(),pay.getIsTest()== 1, pay.getCompleted() != null && pay.getCompleted());
    }
}
