package com.sl.sdn.repository;

import com.sl.sdn.dto.OrganDTO;

import java.util.List;
/**
 * 通用机构查询
 */
public interface OrganRepository {
    /**
     * 无需指定type，根据id查询
     *
     * @param bid 业务id
     * @return 机构数据
     */
    OrganDTO findByBid(Long bid);
    /**
     * 查询所有的机构
     *
     * @param label 代理类型
     * @return 机构列表
     */
    List<OrganDTO> findAllByLabel(String label);

    List<OrganDTO> findALl(String name);
}