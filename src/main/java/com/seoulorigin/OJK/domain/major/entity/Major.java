package com.seoulorigin.OJK.domain.major.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Major")
@Setter @Getter
@NoArgsConstructor
public class Major {
    @Id @GeneratedValue
    private Long id;

    private String majorName;
    private String college;

    public Major(String majorName, String college) {
        this.majorName = majorName;
        this.college = college;
    }
}
