package com.seoulorigin.OJK.domain.major;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Major")
@Setter @Getter
public class Major {
    @Id @GeneratedValue
    private Long id;

    private String majorName;
    private String college;

    // @Builder
    public Major(String majorName, String college) {
        this.majorName = majorName;
        this.college = college;
    }
}
