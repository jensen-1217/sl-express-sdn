package com.sl.sdn.repository.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.util.StrUtil;
import com.sl.sdn.dto.OrganDTO;
import com.sl.sdn.dto.TransportLineNodeDTO;
import com.sl.sdn.entity.node.AgencyEntity;
import com.sl.sdn.enums.OrganTypeEnum;
import com.sl.sdn.repository.TransportLineRepository;
import org.neo4j.driver.internal.InternalPoint2D;
import org.neo4j.driver.internal.value.PathValue;
import org.neo4j.driver.types.Path;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TransportLineRepositoryImpl implements TransportLineRepository {

    @Resource
    private Neo4jClient neo4jClient;

    @Override
    public TransportLineNodeDTO findShortestPath(AgencyEntity start, AgencyEntity end) {
        //1. 定义执行的Cypher语句
        String label = AgencyEntity.class.getAnnotation(Node.class).value()[0];
        String cypher = StrUtil.format("MATCH path = shortestPath((n:{}) -[*..10]->(m:{}))\n" +
                "WHERE n.bid = $startBid AND m.bid = $endBid\n" +
                "RETURN path", label, label);

        //2. 执行语句
        Optional<TransportLineNodeDTO> optional = this.neo4jClient.query(cypher)
                .bind(start.getBid()).to("startBid") //绑定参数
                .bind(end.getBid()).to("endBid") //绑定参数
                .fetchAs(TransportLineNodeDTO.class) //返回值映射的对象类型
                .mappedBy((typeSystem, record) -> { //手动结果映射
                    //System.out.println("record = " + record);
                    PathValue pathValue = (PathValue) record.get(0);
                    Path path = pathValue.asPath();
                    TransportLineNodeDTO dto = new TransportLineNodeDTO();
                    //获取路线中的所有的节点
                    List<OrganDTO> nodeList = StreamUtil.of(path.nodes())
                            .map(node -> {
                                Map<String, Object> map = node.asMap();
                                OrganDTO organDTO = BeanUtil.toBeanIgnoreError(map, OrganDTO.class);
                                Object location = map.get("location");

                                //设置经度
                                organDTO.setLongitude(BeanUtil.getProperty(location, "x"));
                                //设置纬度
                                organDTO.setLatitude(BeanUtil.getProperty(location, "y"));

                                //取第一个标签作为类型
                                OrganTypeEnum organTypeEnum = OrganTypeEnum.valueOf(CollUtil.getFirst(node.labels()));
                                organDTO.setType(organTypeEnum.getCode());

                                return organDTO;
                            })
                            .collect(Collectors.toList());
                    dto.setNodeList(nodeList);

                    //计算路线的总成本
                    double cost = StreamUtil.of(path.relationships())
                            .mapToDouble(relationship -> Convert.toDouble(relationship.asMap().get("cost")))
                            .sum();
                    dto.setCost(cost);
                    return dto;
                })
                .one();
        //
        ////3. 返回数据
        return optional.orElse(null);
    }

    @Override
    public TransportLineNodeDTO findCostLeastPath(AgencyEntity start, AgencyEntity end) {
        //1. 定义执行的Cypher语句
        String label = AgencyEntity.class.getAnnotation(Node.class).value()[0];
        String cypher = StrUtil.format("MATCH path = (n:{}) -[*..10]->(m:{})\n" +
                "WHERE n.bid = $startBid AND m.bid = $endBid\n" +
                "UNWIND relationships(path) AS r\n" +
                "WITH sum(r.cost) AS cost, path\n" +
                "RETURN path ORDER BY cost ASC, LENGTH(path) ASC LIMIT 1",label,label);
        Optional<TransportLineNodeDTO> one = this.neo4jClient.query(cypher)
                .bind(start.getBid()).to("startBid")
                .bind(end.getBid()).to("endBid")
                .fetchAs(TransportLineNodeDTO.class)
                .mappedBy(((typeSystem, record) -> {
                    Path path = record.get(0).asPath();
                    TransportLineNodeDTO transportLineNodeDTO = new TransportLineNodeDTO();
                    path.nodes().forEach(node -> {
                        Map<String, Object> map = node.asMap();
                        OrganDTO organDTO = BeanUtil.toBeanIgnoreError(map, OrganDTO.class);
                        // 获得标签的名称 OLT,TLT,
                        String first = CollUtil.getFirst(node.labels());
                        // 根据OLT获得枚举类型 OLT(1, "一级转运中心"),
                        OrganTypeEnum organTypeEnum = OrganTypeEnum.valueOf(first);
                        // 再获得枚举类型的 code ：1、2、3
                        organDTO.setType(organTypeEnum.getCode()); // 设置类型的映射
                        // 经纬度 "location": point({srid:4326, x:121.59815370294322, y:31.132409729356993})
                        InternalPoint2D location = MapUtil.get(map, "location", InternalPoint2D.class); // 经纬度 BeanUtil.getProperty(map.get("location"),"x");
                        organDTO.setLatitude(location.x());  // 设置经纬度映射
                        organDTO.setLongitude(location.y()); // 经纬度映射
                        transportLineNodeDTO.getNodeList().add(organDTO);
                    });
                    path.relationships().forEach(relationship -> {
                        // 路径下面的关系
                        Map<String, Object> map = relationship.asMap();
                        Double cost = MapUtil.get(map, "cost", Double.class);
                        transportLineNodeDTO.setCost(cost + transportLineNodeDTO.getCost());
                    });
                    return transportLineNodeDTO;
                })).one();
        return one.orElse(null);
    }
}
