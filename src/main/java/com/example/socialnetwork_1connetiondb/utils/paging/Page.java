package com.example.socialnetwork_1connetiondb.utils.paging;

public class Page<E> {
    private Iterable<E> elementsOnPage;
    private int totalNumbersOfElements;

    public Page(Iterable<E> elementsOnPage, int totalNumbersOfElements) {
        this.elementsOnPage = elementsOnPage;
        this.totalNumbersOfElements = totalNumbersOfElements;
    }

    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public void setElementsOnPage(Iterable<E> elementsOnPage) {
        this.elementsOnPage = elementsOnPage;
    }

    public int getTotalNumbersOfElements() {
        return totalNumbersOfElements;
    }

    public void setTotalNumbersOfElements(int totalNumbersOfElements) {
        this.totalNumbersOfElements = totalNumbersOfElements;
    }
}
