package com.seoulorigin.OJK.domain.major.repository;

import com.seoulorigin.OJK.domain.major.entity.Major;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface MajorRepository extends Neo4jRepository<Major, Long> {
    Optional<Major> findByMajorName(String majorName);
}
