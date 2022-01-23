package com.example.point.common;

public class CommonCode {
    public enum PointServiceType{
        CAHRGE  ("P01" , "포인트 충전"),
        USE     ("P02" , "포인트 사용"),
        CANCEL  ("P03" , "포인트 사용 취소");

        private String serviceCode;
        private String serviceDesc;

        PointServiceType(String serviceCode, String serviceDesc){
            this.serviceCode = serviceCode;
            this.serviceDesc = serviceDesc;
        }

        public String serviceCode() {return serviceCode; }
        public String serviceDesc() {return serviceDesc; }
    }
}
