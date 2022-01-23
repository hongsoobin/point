package com.example.point.controller;

import com.example.point.domain.PointTrade;
import com.example.point.service.PointService;
import com.google.gson.JsonObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Log4j2
public class PointContoroller {

    private final PointService pointService;

    @ApiOperation(value="1. 잔여포인트 조회", notes="사용자의 최근 거래를 조회하여 잔여 포인트를 조회한다.")
    @GetMapping(value = "point/userTotal")
    public ResponseEntity getUserTotalPoint(@ApiParam(value="사용자 고유 번호", required = true)@RequestParam long userNo) {
        JsonObject resultJson =new JsonObject();

        Optional<PointTrade> pointTrade = pointService.getUserRecentPointInfo(userNo);
        if (pointTrade.isEmpty()) {
            resultJson.addProperty("remainPoint", 0);
        } else {
            resultJson.addProperty("remainPoint", pointTrade.get().getRemainPoint());
        }

        resultJson.addProperty("isSuccess"  , true);
        resultJson.addProperty("message"    , "조회 성공");

        return new ResponseEntity<>(resultJson.toString(), new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value="2. 포인트 이력 조회", notes="사용자의 포인트 이력을 조회한다. (취소된 거래는 조회하지 않음)")
    @GetMapping(value = "point/pointList")
    public Page<PointTrade> getPointListNotIncludeCancel(
            @ApiParam(value = "페이징 정보", required = true)@PageableDefault(size=10, sort="tradeSeq", direction = Sort.Direction.DESC) Pageable pageable,
            @ApiParam(value = "사용자 고유 번호", required = true)@RequestParam  long userNo) {
        log.debug("{}", "!!!!!!!!!!!!!!!!");
        return pointService.getPointListNotIncludeCancel(userNo, pageable);
    }

    @ApiOperation(value="3. 포인트 충전", notes="사용자가 요청(승인)한 포인트 금액을 충전한다.")
    @PostMapping(value = "point/charge")
    public ResponseEntity chargePoint(
            @ApiParam(value = "사용자 고유 번호", required = true)@RequestParam long userNo,
            @ApiParam(value = "충전 포인트 금액", required = true)@RequestParam long point,
            @ApiParam(value = "승인한 은행 코드", required = true)@RequestParam String bankCode) {
        JsonObject resultJson = new JsonObject();
        // save
        PointTrade pointTrade = pointService.chargePoint(userNo, point, bankCode);

        resultJson.addProperty("isSuccess"  , true            );
        resultJson.addProperty("message"    , "포인트 충전 완료" );
        resultJson.addProperty("data"       , pointTrade.toString() );

        return new ResponseEntity<>(resultJson.toString(),new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value="4. 포인트 사용", notes="사용자의 포인트 사용한다. (사용자 잔여 포인트는 마이너스가 될 수 없다.)")
    @PostMapping(value = "point/use")
    public ResponseEntity usePoint(
            @ApiParam(value = "사용자 고유 번호")@RequestParam long userNo,
            @ApiParam(value = "사용 포인트 금액")@RequestParam long point,
            @ApiParam(value = "거래 고유번호")@RequestParam String tradeNo) {
        JsonObject resultJson = new JsonObject();

        try {
            // save
            PointTrade pointTrade = pointService.usePoint(userNo, point, tradeNo);

            resultJson.addProperty("isSuccess"  , true            );
            resultJson.addProperty("message"    , "포인트 사용 완료" );
            resultJson.addProperty("data"       , pointTrade.toString() );
        } catch (Exception e) {
            resultJson.addProperty("isSuccess"  , false            );
            resultJson.addProperty("message"    , "포인트 사용 오류" );

            return new ResponseEntity<>(resultJson.toString(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR );
        }


        return new ResponseEntity<>(resultJson.toString(),new HttpHeaders(), HttpStatus.OK);
    }

    @ApiOperation(value="5. 포인트 사용 취소", notes="사용된 포인트를 취소한다.")
    @PostMapping(value = "point/cancel")
    public ResponseEntity cancelPoint(@ApiParam(value = "사용자 고유 번호")@RequestParam long userNo,
                                      @ApiParam(value = "포인트 고유 번호")@RequestParam long tradeSeq) {
        JsonObject resultJson = new JsonObject();

        try {
            //update
            PointTrade pointTrade = pointService.cancelPoint(userNo, tradeSeq);

            resultJson.addProperty("isSuccess"  , true            );
            resultJson.addProperty("message"    , "포인트 취소 완료" );
            resultJson.addProperty("data"       , pointTrade.toString() );
        } catch (Exception e) {
            resultJson.addProperty("isSuccess"  , false            );
            resultJson.addProperty("message"    , "포인트 취소 오류" + e.getMessage() );

            return new ResponseEntity<>(resultJson.toString(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


        return new ResponseEntity<>(resultJson.toString(),new HttpHeaders(), HttpStatus.OK);
    }
}
