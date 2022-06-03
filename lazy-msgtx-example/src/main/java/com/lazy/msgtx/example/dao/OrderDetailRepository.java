package com.lazy.msgtx.example.dao;

import com.lazy.msgtx.example.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
@Repository
@Transactional(rollbackFor = Exception.class)
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

}
