package com.lazy.msgtx.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
@Data
@Entity
@Table(name = "order_detail")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String orderNo;

    private String goodsCode;

    private Integer goodsQuantity;

    private BigDecimal goodsUnitPrice;

}
