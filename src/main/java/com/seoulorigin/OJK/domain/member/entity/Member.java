package com.seoulorigin.OJK.domain.member.entity;

import com.seoulorigin.OJK.domain.major.entity.Major;
import com.seoulorigin.OJK.domain.common.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Node("Member")
@Getter @Setter
@NoArgsConstructor
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue
    private Long id;

    // [필수]
    private String name;
    private int admissionYear;
    private String email;
    private String password;

    // [선택]
    private String instagramId;
    private String bio;

    private LocalDateTime verifiedAt;

    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private Major major;

    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    private Set<Member> followings = new HashSet<>();
    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.INCOMING)
    private Set<Member> followers = new HashSet<>();

    public void follow(Member target) {
        if (target != null) {
            this.followings.add(target);
        }
    }
}
