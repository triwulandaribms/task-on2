package jawa.sinaukoding.sk.model;

import java.util.List;

public record Page<T>(long totalData, long totalPage, int page, int saze, List<T> data) {
}