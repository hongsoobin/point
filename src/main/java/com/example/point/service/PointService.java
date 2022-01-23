package com.example.point.service;

import com.example.point.common.CommonCode;
import com.example.point.domain.PointRepository;
import com.example.point.domain.PointTrade;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@RequiredArgsConstructor
@Service
@Log4j2
public class PointService {
    private final PointRepository pointRepository;

    public Optional<PointTrade> getUserRecentPointInfo(long userNo) {
        return pointRepository.findFirstByUserNoOrderByModDateDesc(userNo);
    }

    public Page<PointTrade> getPointListNotIncludeCancel(long userNo,Pageable pageable) {
        return pointRepository.findByUserNoAndCancelYn(userNo, false, pageable);
    }

    public PointTrade chargePoint(long userNo, long point, String bankCode) {
        long recentReaminPoint = 0;
        // 최근 거래 조회
        Optional<PointTrade> recentPointTrade = pointRepository.findFirstByUserNoOrderByModDateDesc(userNo);

        if (!recentPointTrade.isEmpty())
            recentReaminPoint = recentPointTrade.get().getRemainPoint();

        // PointTrade 객체 만들기
        PointTrade saveTrade = PointTrade.builder()
                .serviceType(CommonCode.PointServiceType.CAHRGE.serviceCode())
                .cancelYn(false)
                .userNo(userNo)
                .bankCode(bankCode)
                .point(point)
                .remainPoint(recentReaminPoint + point)
                .build();


        return pointRepository.save(saveTrade);
    }

    public PointTrade usePoint(long userNo, long point, String tradeNo) throws Exception{
        long recentRemainPoint = 0;

        // 최근 거래 조회
        Optional<PointTrade> recentPointTrade = pointRepository.findFirstByUserNoOrderByModDateDesc(userNo);

        if (!recentPointTrade.isEmpty())
            recentRemainPoint = recentPointTrade.get().getRemainPoint();

        // 사용 가능 여부
        if ((recentRemainPoint - point ) < 0 ) {
            throw new Exception("사용 불가");
        }

        // PointTrade 객체 만들기
        PointTrade saveTrade = PointTrade.builder()
                .serviceType(CommonCode.PointServiceType.USE.serviceCode())
                .cancelYn(false)
                .userNo(userNo)
                .tradeNo(tradeNo)
                .point(point)
                .remainPoint(recentRemainPoint - point)
                .build();

        return pointRepository.save(saveTrade);
    }

    public PointTrade cancelPoint(long userNo, long tradeSeq) throws Exception {
        // 취소 거래 검증
        Optional<PointTrade> cancelPointTrade = pointRepository.findByTradeSeq(tradeSeq);

        if (cancelPointTrade.isEmpty()){
            throw new Exception("거래 조회 오류");
        }

        // 최근 거래 조회
        Optional<PointTrade> recentPointTrade = pointRepository.findFirstByUserNoOrderByModDateDesc(userNo);

        // PointTrade 객체 만들기
        PointTrade saveTrade = PointTrade.builder()
                .tradeSeq(cancelPointTrade.get().getTradeSeq())
                .cancelYn(true)
                .userNo(userNo)
                .remainPoint(recentPointTrade.get().getRemainPoint() + cancelPointTrade.get().getPoint())
                .build();

        return pointRepository.save(saveTrade);
    }
}