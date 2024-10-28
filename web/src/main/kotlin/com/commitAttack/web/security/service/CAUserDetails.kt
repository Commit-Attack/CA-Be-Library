package com.commitAttack.web.security.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CAUserDetails(
    private val id: String,
    private val username: String,
    private val role: String?,
    val userJwt: String? = null
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return if (role == "Admin")
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        else{
            listOf(SimpleGrantedAuthority("ROLE_MEMBER"))
        }
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return this.username
    }

    override fun isAccountNonExpired(): Boolean {
        return false
    }

    override fun isAccountNonLocked(): Boolean {
        return false
    }

    override fun isCredentialsNonExpired(): Boolean {
        return false
    }

    override fun isEnabled(): Boolean {
        return false
    }

    fun getId(): String {
        return this.id
    }

    fun getRole(): String? {
        return this.role
    }
}