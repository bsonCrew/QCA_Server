package com.example.QCA.QualityControlAutomation.control.domain.repository;

import com.example.QCA.QualityControlAutomation.control.domain.ControlResult;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ControlRepositoryCustomImpl implements ControlRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<ControlResult> findTop5List() {
        List<ControlResult> tmpList = findUniqueLabelList();

        if (tmpList.isEmpty()) return Collections.emptyList();

        List<ControlResult> results = new ArrayList<>();

        // 내림차순 정렬이므로 가장 최근 진단한 웹사이트부터 탐색
        for (ControlResult controlResult : tmpList) {
            // label이 도메인이면 리스트에 추가
            if (controlResult.checkDomain()) {
                results.add(controlResult);
                // 5개가 채워지면 중단
                if (results.size() == 5) break;
            }
        }

        return results;
    }

    // DB에서 date가 null이 아닌 데이터들을 날짜 기준 내림차순으로 정렬해서 반환
    private List<ControlResult> findUniqueLabelList() {
        return em.createQuery("select c from ControlResult c where c.recentRequestedDate is not null order by c.recentRequestedDate")
                .getResultList();
    }
}
