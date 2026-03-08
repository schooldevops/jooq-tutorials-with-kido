package com.example.demo.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.domain.Persistable

@Entity
@Table(name = "author")
class AuthorEntity(
    @Id
    @get:JvmName("getIdentifier")
    var id: Int? = null,

    @Column(name = "first_name")
    var firstName: String? = null,

    @Column(name = "last_name")
    var lastName: String? = null
) : Persistable<Int> {

    override fun getId(): Int? = id

    override fun isNew(): Boolean = true
}
