package ru.daru_jo.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Deal {
    @Getter
    private final String companyCode;
    private final List<RevenueModel> revenueModelList;
    @Getter
    private final List<ExpenditureModel> expenditureModelNotRevenueList = new ArrayList<>();

    public Deal(String currencyCode, List<RevenueModel> revenueModelList) {
        this.companyCode = currencyCode;
        this.revenueModelList = revenueModelList;
    }

    public List<RevenueModel> getRevenueList() {
        return revenueModelList;
    }

    public  void addExpenditureModelNotRevenue(ExpenditureModel expenditureModel){
        expenditureModelNotRevenueList.add(expenditureModel);
    }

}
