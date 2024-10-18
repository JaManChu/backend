package com.recipe.jamanchu.model.dto.response.mypage;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@AllArgsConstructor
public class PageResponse<T> {

  private int pageNumber;
  private int totalPage;
  private Long totalData;
  private List<T> dataList;

  public static <T> PageResponse<T> pagination(List<T> list, int pageNumber) {
    // pagination 처리

    Pageable pageable = PageRequest.of(pageNumber - 1, 4);
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), list.size());

    List<T> pagedList = list.subList(start, end);
    Page<T> page = new PageImpl<>(pagedList, pageable, list.size());

    // Page를 PageResponse로 변환
    return new PageResponse<>(
        page.getNumber() + 1,   // 페이지는 1부터 시작하게 설정
        page.getTotalPages(),   // 총 페이지 수
        page.getTotalElements(),// 전체 데이터 개수
        page.getContent()       // 현재 페이지 데이터 리스트
    );
  }
}
