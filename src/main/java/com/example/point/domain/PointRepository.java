package com.example.point.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<PointTrade,Object> {
    Optional<PointTrade> findFirstByUserNoOrderByModDateDesc(long userNo);
    Optional<PointTrade> findByTradeSeq(long tradeSeq);
    Page<PointTrade>     findByUserNoAndCancelYn(long userNo, boolean cancelYn,Pageable pageable);
}
