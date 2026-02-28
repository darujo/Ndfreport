package ru.daru_jo.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.CouponNKD;
import ru.daru_jo.entity.Order;
import ru.daru_jo.repository.CouponNKDRepository;
import ru.daru_jo.specifications.Specifications;

import java.util.List;

@Service
public class CouponNKDService {
    private CouponNKDRepository couponNKDRepository;

    @Autowired
    public void setCouponNKDRepository(CouponNKDRepository couponNKDRepository) {
        this.couponNKDRepository = couponNKDRepository;
    }

    public List<CouponNKD> findAll(Order order, Sort sort) {
        Specification<CouponNKD> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"order",order);
        return couponNKDRepository.findAll(specification,sort);
    }

    public void save(CouponNKD coupon) {
        couponNKDRepository.save(coupon);
    }
}
