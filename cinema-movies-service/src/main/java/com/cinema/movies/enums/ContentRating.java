package com.cinema.movies.enums;

public enum ContentRating {
    EXCELLENT,
    GOOD,
    AVERAGE,
    POOR,
    UNRATED;

    public static ContentRating fromNumericRating(Double rating) {
        if (rating == null) {
            return UNRATED;
        }
        if (rating >= 8.0) {
            return EXCELLENT;
        }
        if (rating >= 6.0) {
            return GOOD;
        }
        if (rating >= 4.0) {
            return AVERAGE;
        }
        return POOR;
    }
}
