package com.sl.sdn;
import org.springframework.data.geo.Point;

import com.sl.sdn.entity.node.TLTEntity;
import com.sl.sdn.repository.TLTRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class TLTRepositoryTest {
    @Resource
    private TLTRepository tltRepository;
    @Test
    public void testFindByBid() {

    }
    @Test
    public void testSave() {
        TLTEntity tltEntity = new TLTEntity();
        tltEntity.setBid(10086L);
        tltEntity.setName("测试二级转运中心");
        tltEntity.setPhone("13131313131");
        tltEntity.setAddress("地址");
        tltEntity.setLocation(new Point(12.12,12.12));
        TLTEntity save = this.tltRepository.save(tltEntity);
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