package com.docment.fetch.dto.response;


import java.io.Serializable;

public class PaginationDetails implements Serializable
{
    private int totalPages;
    private boolean last;
    private int totalElements;
    private int size;
    private int pageNumber;
    private int numberOfElements;
    private boolean empty;
    private boolean first;

    public int getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(final int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return this.last;
    }

    public void setLast(final boolean last) {
        this.last = last;
    }

    public int getTotalElements() {
        return this.totalElements;
    }

    public void setTotalElements(final int totalElements) {
        this.totalElements = totalElements;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getNumberOfElements() {
        return this.numberOfElements;
    }

    public void setNumberOfElements(final int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public void setEmpty(final boolean empty) {
        this.empty = empty;
    }

    public boolean isFirst() {
        return this.first;
    }

    public void setFirst(final boolean first) {
        this.first = first;
    }
}
