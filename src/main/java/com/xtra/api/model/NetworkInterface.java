package com.xtra.api.model;

import lombok.Data;


@Data
public class NetworkInterface {

    private String name;
    private Long BytesSent;
    private Long BytesRecv;

    public NetworkInterface(String name, Long bytesSent, Long bytesRecv) {
        this.name = name;
        BytesSent = bytesSent;
        BytesRecv = bytesRecv;
    }
    public NetworkInterface(){}
}
