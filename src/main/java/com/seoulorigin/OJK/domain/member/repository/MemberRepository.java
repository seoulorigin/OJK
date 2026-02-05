package com.seoulorigin.OJK.domain.member.repository;

import com.seoulorigin.OJK.domain.member.entity.Member;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends Neo4jRepository<Member, Long> {
    /**
     * 통합 검색 기능
     * @param keyword 검색어 (이름 혹은 인스타 아이디)
     * @param admissionYear 학번 필터 (null일 경우 전체 학번)
     * @param majorName 전공 필터 (null일 경우 전체 전공)
     */
    @Query("MATCH (m:Member)-[:BELONGS_TO]->(mj:Major) " +
            "WHERE " +
            "(m.name CONTAINS $keyword OR m.instagramId CONTAINS $keyword) " +
            "AND ($admissionYear IS NULL OR m.admissionYear = $admissionYear) " +
            "AND ($majorName IS NULL OR mj.majorName = $majorName) " +
            "RETURN m, mj")
    List<Member> searchMembers(@Param("keyword") String keyword,
                                      @Param("admissionYear") Integer admissionYear,
                                      @Param("majorName") String majorName);

    /**
     * 최단 경로 탐색
     * @param startId 본인
     * @param endId 특정 인물
     * @return 경로상에 있는 Member들의 List (순서대로)
     */
    @Query("MATCH (start:Member), (end:Member) " +
            "WHERE id(start) = $startId AND id(end) = $endId " +
            "MATCH path = shortestPath((start)-[:FOLLOWS*..6]->(end)) " +
            "UNWIND nodes(path) AS n " +  // 리스트를 낱개로 풉니다
            "RETURN n")
    List<Member> findPathById(@Param("startId") Long startId,
                              @Param("endId") Long endId);
}
