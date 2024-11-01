package com.commitAttack.libraries

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.time.OffsetDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity : BasicBaseEntity() {

    @LastModifiedDate
    @Column(name = "\"updatedAt\"", nullable = true)
    var updatedAt: LocalDateTime? = null
        protected set

    @Column(name = "\"deletedAt\"", nullable = true)
    var deletedAt: LocalDateTime? = null
        protected set

    fun delete() {
        deletedAt = LocalDateTime.now()
    }

    fun update() {
        updatedAt = LocalDateTime.now()
    }
}
