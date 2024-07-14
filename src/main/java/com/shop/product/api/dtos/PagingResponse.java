package com.shop.product.api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO using for pagination
 */
@Getter
@Setter
@Builder
public class PagingResponse<T> {

    /** entity count*/
    private long total_results;

    /** page number, 0 indicate the first page.*/
    private int page;

    /** size of page, 0 indicate infinite-sized. */
    private int size;

    /** Offset from the of pagination. */
    @JsonProperty("page_offset")
    private long pageOffset;

    /** the number total of pages. */
    @JsonProperty("total_pages")
    private int totalPages;

    /** elements of page. */
    private List<T> results;

    public PagingResponse(long total_results, int page, int size, long pageOffset, int totalPages, List<T> results) {
        this.total_results = total_results;
        this.page = page;
        this.size = size;
        this.pageOffset = pageOffset;
        this.totalPages = totalPages;
        this.results = results;
    }


}