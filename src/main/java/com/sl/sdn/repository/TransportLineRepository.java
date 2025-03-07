package com.sl.sdn.repository;

import com.sl.sdn.dto.TransportLineNodeDTO;
import com.sl.sdn.entity.node.AgencyEntity;

/**
 * 运输路线相关操作
 */
public interface TransportLineRepository {

    /**
     * 查询两个网点之间最短的路线，查询深度为：10
     *
     * @param start 开始网点
     * @param end   结束网点
     * @return 路线
     */
    TransportLineNodeDTO findShortestPath(AgencyEntity start, AgencyEntity end);

    /**
     * 查询两个网点之间成本最少路径，如果成本相同，返回转运节点最少路径
     * @param start 开始网点
     * @param end 结束网点
     * @return 路线
     */
    TransportLineNodeDTO findCostLeastPath(AgencyEntity start, AgencyEntity end);

}
