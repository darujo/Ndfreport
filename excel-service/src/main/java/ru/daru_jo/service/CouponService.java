package ru.daru_jo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Coupon;
import ru.daru_jo.entity.Order;
import ru.daru_jo.repository.CouponRepository;
import ru.daru_jo.specifications.Specifications;

import java.util.List;

@Service
public class CouponService {
    private CouponRepository couponRepository;

    @Autowired
    public void setCouponRepository(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<Coupon> findAll(Order order, Sort sort) {
        Specification<Coupon> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"order",order);
        return couponRepository.findAll(specification,sort);
    }

    public void save(Coupon coupon) {
        couponRepository.save(coupon);
    }
}
