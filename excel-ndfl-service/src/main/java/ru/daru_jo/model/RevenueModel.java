package ru.daru_jo.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.daru_jo.type.OperationType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
@Slf4j
public class RevenueModel extends MovementModel {
    @Getter
    private final List<ExpenditureModel> expenditureModelList = new ArrayList<>();
    public RevenueModel(OperationType operationType, Timestamp timestamp, Double price, Double quantity, String currencyName, String code, String typeOf) {
        super(operationType, timestamp, price, quantity, currencyName, code, typeOf, null);
    }
    public RevenueModel(OperationType operationType, Timestamp timestamp, Double price, Double quantity, String currencyName, String code, String typeOf, Double amountInCur) {
        super(operationType, timestamp, price, quantity, currencyName, code, typeOf, amountInCur);
    }
    public void addExpenditureAndMinus(ExpenditureModel  expenditureModel)
    {
        if (getQuantityDistributed() != null && getQuantityDistributed() > 0 ){
            if( expenditureModel.getQuantity() != null) {
                Double quantity = useQuantity(expenditureModel.getQuantity());
                expenditureModel.useQuantity(quantity);
                if (expenditureModel.getQuantityDistributed() > 0){
                    if(expenditureModel.getPrice()== null){
                        log.error("Цена пустая", Thread.currentThread());
                    }
                    expenditureModel = new ExpenditureModel(expenditureModel.getOperationType(),
                            expenditureModel.getTimestamp(),
                            expenditureModel.getPrice(),
                            expenditureModel.getQuantity() - expenditureModel.getQuantityDistributed(),
                            expenditureModel.getCurrencyName(),
                            expenditureModel.getCode(),
                            expenditureModel.getTypeOf(),
                            null);
                    expenditureModel.useQuantity(expenditureModel.getQuantity());
                }
            }
            expenditureModelList.add(expenditureModel);
        }

    }
    public void addExpenditure (ExpenditureModel  expenditureModel){
        expenditureModelList.add(expenditureModel);
    }
}
