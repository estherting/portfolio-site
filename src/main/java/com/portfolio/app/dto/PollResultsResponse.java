package com.portfolio.app.dto;

public class PollResultsResponse {

    private String optionAText;
    private String optionBText;
    private long countA;
    private long countB;
    private double percentA;
    private double percentB;
    private long total;

    public PollResultsResponse() {}

    public PollResultsResponse(String optionAText, String optionBText, long countA, long countB,
                                double percentA, double percentB, long total) {
        this.optionAText = optionAText;
        this.optionBText = optionBText;
        this.countA = countA;
        this.countB = countB;
        this.percentA = percentA;
        this.percentB = percentB;
        this.total = total;
    }

    public String getOptionAText() { return optionAText; }
    public void setOptionAText(String optionAText) { this.optionAText = optionAText; }

    public String getOptionBText() { return optionBText; }
    public void setOptionBText(String optionBText) { this.optionBText = optionBText; }

    public long getCountA() { return countA; }
    public void setCountA(long countA) { this.countA = countA; }

    public long getCountB() { return countB; }
    public void setCountB(long countB) { this.countB = countB; }

    public double getPercentA() { return percentA; }
    public void setPercentA(double percentA) { this.percentA = percentA; }

    public double getPercentB() { return percentB; }
    public void setPercentB(double percentB) { this.percentB = percentB; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
}
