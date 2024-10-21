package com.sl.sdn;
import org.springframework.data.geo.Point;

import com.sl.sdn.entity.node.OLTEntity;
import com.sl.sdn.repository.OLTRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class OLTRepositoryTest {
    @Resource
    private OLTRepository oltRepository;
    @Test
    public void testFindByBid() {

    }
    @Test
    public void testSave() {
        OLTEntity oltEntity = new OLTEntity();
        oltEntity.setBid(10010L);
        oltEntity.setName("测试一级转运中心");
        oltEntity.setPhone("13131313131");
        oltEntity.setAddress("地址");
        oltEntity.setLocation(new Point(12.12,12.12));
        OLTEntity save = this.oltRepository.save(oltEntity);
        System.out.println("save = " + save);

    }
    @Test
    public void testUpdate() {

    }
    @Test
    public void testDeleteByBid() {

    }
    /**
     * 查询全部
     */
    @Test
    public void testFindAll() {

    }
    /**
     * 分页查询
     */
    @Test
    public void testPage() {

    }
}