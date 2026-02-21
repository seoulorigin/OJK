package com.seoulorigin.OJK.domain.member.repository;

import com.seoulorigin.OJK.domain.member.entity.Member;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends Neo4jRepository<Member, Long> {
    /**
     * 통합 검색 기능
     * @param keyword 검색어 (이름 혹은 인스타 아이디)
     * @param admissionYear 학번 필터 (null일 경우 전체 학번)
     * @param majorName 전공 필터 (null일 경우 전체 전공)
     */
    @Query("MATCH (m:Member)-[r:BELONGS_TO]->(mj:Major) " +
            "WHERE " +
            "(m.name CONTAINS $keyword OR m.instagramId CONTAINS $keyword) " +
            "AND ($admissionYear IS NULL OR m.admissionYear = $admissionYear) " +
            "AND ($majorName IS NULL OR mj.majorName = $majorName) " +
            "RETURN m, r, mj")
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
            "UNWIND nodes(path) AS n " +
            "RETURN n")
    List<Member> findPathById(@Param("startId") Long startId,
                              @Param("endId") Long endId);

    @Query("MATCH (me:Member)<-[r:FOLLOWS]-(follower:Member) " +
            "WHERE id(me) = $memberId " +
            "MATCH (follower)-[b:BELONGS_TO]->(mj:Major) " +
            "RETURN follower, b, r, mj")
    List<Member> findFollowersById(@Param("memberId") Long memberId);

    @Query("MATCH (me:Member)-[:FOLLOWS]->(following:Member) " +
            "WHERE id(me) = $memberId " +
            "RETURN following")
    List<Member> findFollowingsById(@Param("memberId") Long memberId);

    @Query("MATCH (from:Member)-[r:FOLLOWS]->(to:Member) " +
            "WHERE id(from) = $fromId AND id(to) = $toId " +
            "RETURN count(r) > 0")
    boolean existsFollowRelation(@Param("fromId") Long fromId, @Param("toId") Long toId);

    @Query("MATCH (from:Member)-[r:FOLLOW_REQUEST]->(to:Member) " +
            "WHERE id(from) = $fromId AND id(to) = $toId " +
            "RETURN count(r) > 0")
    boolean existsFollowRequest(@Param("fromId") Long fromId, @Param("toId") Long toId);

    @Query("MATCH (from:Member), (to:Member) " +
            "WHERE id(from) = $fromId AND id(to) = $toId " +
            "MERGE (from)-[:FOLLOW_REQUEST {requestedAt: datetime()}]->(to)")
    void createFollowRequest(@Param("fromId") Long fromId, @Param("toId") Long toId);

    @Query("MATCH (from:Member)-[r:FOLLOW_REQUEST]->(to:Member) " +
            "WHERE id(from) = $fromId AND id(to) = $toId " +
            "DELETE r " +
            "RETURN count(r)")
    long rejectFollowRequest(@Param("fromId") Long fromId, @Param("toId") Long toId);

    @Query("MATCH (from:Member)-[r:FOLLOW_REQUEST]->(to:Member) " +
            "WHERE id(from) = $fromId AND id(to) = $toId " +
            "DELETE r " +
            "MERGE (from)-[:FOLLOWS]->(to) " +
            "RETURN count(r)")
    long approveFollowRequest(@Param("fromId") Long fromId, @Param("toId") Long toId);

    @Query("MATCH (from:Member)-[r:FOLLOWS]->(to:Member) " +
            "WHERE id(from) = $fromId AND id(to) = $toId " +
            "DELETE r " +
            "RETURN count(r)")
    long unfollow(@Param("fromId") Long fromId, @Param("toId") Long toId);

    @Query("MATCH (requester:Member)-[r:FOLLOW_REQUEST]->(me:Member) " +
            "WHERE id(me) = $memberId " +
            "MATCH (requester)-[b:BELONGS_TO]->(mj:Major) " +
            "RETURN requester, b, r, mj")
    List<Member> findPendingFollowRequestsByMemberId(@Param("memberId") Long memberId);

    Optional<Member> findByEmail(String email);
}
