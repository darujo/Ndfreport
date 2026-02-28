package ru.daru_jo.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.CouponNKDMinus;
import ru.daru_jo.entity.Order;
import ru.daru_jo.repository.CouponNKDMinusRepository;
import ru.daru_jo.specifications.Specifications;

import java.util.List;

@Service
public class CouponNKDMinusService {
    private CouponNKDMinusRepository couponNKDMinusRepository;

    @Autowired
    public void setCouponNKDMinusRepository(CouponNKDMinusRepository couponNKDMinusRepository) {
        this.couponNKDMinusRepository = couponNKDMinusRepository;
    }

    public List<CouponNKDMinus> findAll(Order order, Sort sort) {
        Specification<CouponNKDMinus> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"order",order);
        return couponNKDMinusRepository.findAll(specification,sort);
    }

    public void save(CouponNKDMinus coupon) {
        couponNKDMinusRepository.save(coupon);
    }
}
