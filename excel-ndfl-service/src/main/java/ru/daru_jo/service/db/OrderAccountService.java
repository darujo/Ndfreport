package ru.daru_jo.service.db;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Order;
import ru.daru_jo.entity.OrderAccount;
import ru.daru_jo.repository.OrderAccountRepository;
import ru.daru_jo.specifications.Specifications;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderAccountService {
    private OrderAccountRepository orderAccountRepository;
    private BondService bondService;
    private OrderService orderService;
    private DividendService dividendService;
    private DividendTaxService dividendTaxService;
    private ExpenditureService expenditureService;
    private ExpensesService expensesService;
    private PercentService percentService;
    private RevenueService revenueService;

    @Autowired
    public void setOrderRepository(OrderAccountRepository orderAccountRepository) {
        this.orderAccountRepository = orderAccountRepository;
    }

    public OrderAccount save(OrderAccount orderAccount) {
        return orderAccountRepository.save(orderAccount);
    }
    public List<OrderAccount> findAll(Order order, String year) {
        List<Order> orderList = new ArrayList<>();
        orderList.add(order);
        return findAll(orderList,year);
    }
    public List<OrderAccount> findAll(List<Order> order, String year) {
        Specification<OrderAccount> specification = Specification.unrestricted();
        specification = Specifications.in(specification, "order", order);
        specification = Specifications.eq(specification, "year", year);
        return orderAccountRepository.findAll(specification, Sort.by("order", "year", "account"));
    }

    @Transactional
    public void delete(OrderAccount orderAccount){
        Order order = orderAccount.getOrder();
        String year = orderAccount.getYear();
        bondService.delete(orderAccount);
        dividendService.delete(orderAccount);
        dividendTaxService.delete(orderAccount);
        expenditureService.delete(orderAccount);
        expensesService.delete(orderAccount);
        percentService.delete(orderAccount);
        revenueService.delete(orderAccount);
        orderAccountRepository.delete(orderAccount);
        if(findAll(order,year).isEmpty()) {
            order.getYearList().remove(year);
            orderService.save(order);
        }
    }

    @Autowired
    public void setBondService(BondService bondService) {
        this.bondService = bondService;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setDividendService(DividendService dividendService) {
        this.dividendService = dividendService;
    }

    @Autowired
    public void setDividendTaxService(DividendTaxService dividendTaxService) {
        this.dividendTaxService = dividendTaxService;
    }

    @Autowired
    public void setExpenditureService(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    @Autowired
    public void setExpensesService(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @Autowired
    public void setPercentService(PercentService percentService) {
        this.percentService = percentService;
    }

    @Autowired
    public void setRevenueService(RevenueService revenueService) {
        this.revenueService = revenueService;
    }
}
