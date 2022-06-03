package com.lazy.msgtx.example.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

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
@Table(name = "order_main")
public class OrderMain {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String orderNo;

    private String customerCode;

    private String customerName;

    private BigDecimal totalAmount;

    private String address;

    @Transient
    private List<OrderDetail> orderDetails;

}
