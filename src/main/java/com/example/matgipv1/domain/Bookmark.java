package com.example.matgipv1.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "bookmark",
        uniqueConstraints = {@UniqueConstraint(name = "uk_bookmark_member_restaurant", columnNames = {"member_id", "restaurant_id"})},
        indexes = {@Index(name = "idx_bookmark_member", columnList = "member_id")}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;
}
