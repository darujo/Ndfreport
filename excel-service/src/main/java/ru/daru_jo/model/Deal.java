package ru.daru_jo.model;

import java.util.List;

public class Deal {
    private final String companyCode;
    private final List<RevenueModel> revenueModelList;
    private final List<ExpenditureModel> expenditureModelList;

    public Deal(String currencyCode, List<RevenueModel> revenueModelList, List<ExpenditureModel> expenditureModelList) {
        this.companyCode = currencyCode;
        this.revenueModelList = revenueModelList;
        this.expenditureModelList = expenditureModelList;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public List<RevenueModel> getRevenueList() {
        return revenueModelList;
    }

    public List<ExpenditureModel> getExpenditureList() {
        return expenditureModelList;
    }
}
