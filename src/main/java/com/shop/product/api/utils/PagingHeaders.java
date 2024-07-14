package com.shop.product.api.utils;


import lombok.Getter;


@Getter
public enum PagingHeaders {
    PAGE_SIZE("Page-Size"),
    PAGE_NUMBER("Page-Number"),
    PAGE_OFFSET("Page-Offset" ),
    PAGE_TOTAL("Page-Total" ),
    COUNT("Count" );

    private final String name;

    PagingHeaders(String name) {
        this.name = name;
    }
}