package com.cms.dto.central.tenant;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Pagination response adapted to frontend shape used in the project. */
@Getter
@AllArgsConstructor
public class PaginatedResponse<T> {
  private final List<T> data;
  private final int currentPage;
  private final int from;
  private final int lastPage;
  private final int perPage;
  private final int to;
  private final long total;
  private final String firstPageUrl;
  private final String lastPageUrl;
  private final String nextPageUrl;
  private final String prevPageUrl;
}
