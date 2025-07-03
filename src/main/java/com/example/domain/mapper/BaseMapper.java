package com.example.domain.mapper;

/**
 * Generic base mapper interface for converting between Entity (E) and DTO/VO (D).
 *
 * @param <E> the entity type
 * @param <D> the DTO/VO type
 */
public interface BaseMapper<E, D> {

    D toVo(E en);

    E toEntity(D vo);
}