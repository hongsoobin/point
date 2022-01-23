package com.example.point.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Getter
@Entity
@Data
@Table(name="TB_POINT_TRADE")
public class PointTrade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(value = "포인트 고유번호")
    private long tradeSeq;

    @ApiModelProperty(value = "등록일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @CreatedDate
    private LocalDateTime regDate;

    @ApiModelProperty(value = "수정일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @CreatedDate
    private LocalDateTime modDate;

    @Column
    @ApiModelProperty(value = "서비스 타입")
    private String serviceType;

    @Column
    @ApiModelProperty(value = "취소 여부")
    private boolean cancelYn;

    @Column
    @ApiModelProperty(value = "사용자 고유번호")
    private long userNo;

    @Column
    @ApiModelProperty(value = "은행 코드")
    private String bankCode;

    @Column
    @ApiModelProperty(value = "거래 고유번호")
    private String tradeNo;

    @Column
    @ApiModelProperty(value = "포인트")
    private long point;

    @Column
    @ApiModelProperty(value = "잔여 포인트")
    private long remainPoint;

}


