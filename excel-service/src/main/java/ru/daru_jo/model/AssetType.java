package ru.daru_jo.model;

public enum AssetType {
    //Фондовый рынок: ценные бумаги, обращающиеся на рынке
    Stocks(DealType.FOND,
            "Stocks",
            "Акции",
            1530,
            201,
            201,
            201,
            201),
    Bonds(DealType.FOND,
            "Bonds",
            "Облигации",
            1530,
            201,
            201,
            201,
            201),
    Funds(DealType.FOND,
            "Exchange-Traded Funds",
            "Биржевые фонды",
            1530,
            201,
            201,
            201,
            201),
    MutualFunds(DealType.FOND,
            "Mutual Funds",
            "Паевые инвестиционные фонды",
            1530,
            201,
            201,
            201,
            201),
    //    Валюта, металлы, товары (Interactive Brokers + номер счета)
    Forex(DealType.CURRENCY,
            "Forex",
            "Forex",
            1520,
            903,
            903,
            903,
            903),
    //Производные финансовые инструменты, обращающиеся на рынке (ПФИ на акции и индексы)
    Options(DealType.INDEX,
            "Equity and Index Options",
            "опционы на акции и индексы",
            1532,
            206,
            206,
            206,
            206),
    //ПФИ, не обращающиеся на рынке
    Warrants(DealType.PIF,
            "Warrants",
            "Варранты",
            1533,
            220,
            220,
            220,
            220);
    private final DealType type;
    private final String assetCategory;
    private final String name;
    private final Integer revenueCode;
    private final Integer buy;
    private final Integer commissionBuy;
    private final Integer commissionSale;
    private final Integer generalExpenses;

    AssetType(DealType type, String assetCategory, String name, Integer revenueCode, Integer buy, Integer commissionBuy, Integer commissionSale, Integer generalExpenses) {
        this.type = type;
        this.assetCategory = assetCategory;
        this.name = name;
        this.revenueCode = revenueCode;
        this.buy = buy;
        this.commissionBuy = commissionBuy;
        this.commissionSale = commissionSale;
        this.generalExpenses = generalExpenses;
    }

    public DealType getType() {
        return type;
    }

    public String getAssetCategory() {
        return assetCategory;
    }

    public String getName() {
        return name;
    }

    public Integer getRevenueCode() {
        return revenueCode;
    }

    public Integer getBuy() {
        return buy;
    }

    public Integer getCommissionBuy() {
        return commissionBuy;
    }

    public Integer getCommissionSale() {
        return commissionSale;
    }

    public Integer getGeneralExpenses() {
        return generalExpenses;
    }
}
