package com.example.urop_application;

import java.util.StringTokenizer;

public class TCP {
//    private static final String TAG = "TCP";
    private String local_address;


    public TCP(String tcpUnparsed) {
        StringTokenizer tcpParsedElements = new StringTokenizer(tcpUnparsed);

        /* Get the second element after sl - this is local_address */
        tcpParsedElements.nextToken();
        this.local_address = tcpParsedElements.nextToken();
    }

    public String getFirstPartOfLocalAddress() {
        StringTokenizer tokenizer = new StringTokenizer(local_address, ":");
        return tokenizer.nextToken();
    }






}
