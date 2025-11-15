package com.github.ajharry69.testsoap.locations;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "locations",
        indexes = {
                @Index(columnList = "date_created", name = "idx_locations_date_created"),
                @Index(columnList = "date_updated", name = "idx_locations_date_updated")
        }
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    private String name;
    @CreatedDate
    @Column(updatable = false)
    private OffsetDateTime dateCreated;
    @LastModifiedDate
    @Column(insertable = false)
    private OffsetDateTime dateUpdated;
    private Double fahrenheit;
    private Double celsius;
    private int retriesCount = 0;
}