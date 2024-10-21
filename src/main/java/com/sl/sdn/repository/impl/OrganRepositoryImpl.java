package com.sl.sdn.repository.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.sl.sdn.dto.OrganDTO;
import com.sl.sdn.enums.OrganTypeEnum;
import com.sl.sdn.repository.OrganRepository;
import org.neo4j.driver.types.Node;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author jensen
 * @date 2024-10-21 19:07
 */
@Component
public class OrganRepositoryImpl implements OrganRepository {
    @Resource
    private Neo4jClient neo4jClient;
    @Override
    public OrganDTO findByBid(Long bid) {
        String cypher = "MATCH (n) WHERE n.bid =$bid  RETURN n";
        Optional<OrganDTO> one = this.neo4jClient.query(cypher)
                .bind(bid).to("bid")
                .fetchAs(OrganDTO.class)
                .mappedBy(((typeSystem, record) -> {
                    Node node = record.get(0).asNode();
                    return getOrganDTO(node);
                }))
                .one();
        //System.out.println("one = " + one);
        return one.orElse(null);
    }

    @Override
    public List<OrganDTO> findAllByLabel(String label) {
        String cypher = StrUtil.format("MATCH (n:{}) RETURN n", label);
        Collection<OrganDTO> all = this.neo4jClient.query(cypher)
                .fetchAs(OrganDTO.class)
                .mappedBy(((typeSystem, record) -> {
                    //System.out.println("record = " + record);
                    Node node = record.get("n").asNode();
                    return getOrganDTO(node);
                }))
                .all();
        return new ArrayList<>(all);
    }

    @Override
    public List<OrganDTO> findALl(String name) {
        //String cypher = StrUtil.format("MATCH (n) WHERE n.name CONTAINS '{}' RETURN n", name);
        String cypher = "MATCH (n) WHERE n.name CONTAINS $name RETURN n";
        Collection<OrganDTO> all = this.neo4jClient.query(cypher)
                .bind(name).to("name")
                .fetchAs(OrganDTO.class)
                .mappedBy(((typeSystem, record) -> {
                    //System.out.println("record = " + record);
                    Node node = record.get("n").asNode();
                    return getOrganDTO(node);
                }))
                .all();
        return new ArrayList<>(all);
    }

    private OrganDTO getOrganDTO(Node node) {
        Map<String, Object> map = node.asMap();
        OrganDTO dto = BeanUtil.toBeanIgnoreError(map, OrganDTO.class);
        ////取第一个标签作为类型
        OrganTypeEnum organTypeEnum = OrganTypeEnum.valueOf(CollUtil.getFirst(node.labels()));
        dto.setType(organTypeEnum.getCode());
        Object location = map.get("location");
        //设置经度
        dto.setLongitude(BeanUtil.getProperty(location, "x"));
        //设置纬度
        dto.setLatitude(BeanUtil.getProperty(location, "y"));
        return dto;
    }
}
