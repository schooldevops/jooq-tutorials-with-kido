package com.example.demo.config

object TenantContext {
    private val tenantHolder = ThreadLocal<String>()

    fun getCurrentTenant(): String? {
        return tenantHolder.get()
    }

    fun setCurrentTenant(tenant: String?) {
        tenantHolder.set(tenant)
    }

    fun clear() {
        tenantHolder.remove()
    }
}
