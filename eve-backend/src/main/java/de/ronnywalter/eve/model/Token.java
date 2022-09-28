package de.ronnywalter.eve.model;

import de.ronnywalter.eve.util.AttributeEncryptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "eve_character_tokens")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Token {

    @Id
    private int characterId;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    @EqualsAndHashCode.Exclude
    private Instant createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    @EqualsAndHashCode.Exclude
    private Instant modifiedDate;

    @Column(columnDefinition="TEXT")
    @Convert(converter = AttributeEncryptor.class)
    private String accessToken;
    @Column(columnDefinition="TEXT")
    @Convert(converter = AttributeEncryptor.class)
    private String refreshToken;

    private LocalDateTime expiryDate;
    @Convert(converter = AttributeEncryptor.class)
    private String clientId;
    @Convert(converter = AttributeEncryptor.class)
    private String clientSecret;

}
