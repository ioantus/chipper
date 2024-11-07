package com.itiutiaiev.chipper.service

import org.codehaus.groovy.runtime.InvokerHelper

interface BaseService<D,E> {

    D toDto(E e)

    E fromDto(D d)

    default void copyPropertiesFromDto(D d, E e) {
        if (d && e) {
            InvokerHelper.setProperties(e, d.properties)
        }
    }
    default void copyPropertiesFromEntity(E e, D d) {
        if (e && d) {
            // No need to set/update `password` field for data object
            InvokerHelper.setProperties(d, e.properties.findAll {it.key != 'password'})
        }
    }
}