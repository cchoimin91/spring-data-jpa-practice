package study.datajap.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajap.entity.Member;

import javax.persistence.Temporal;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void bulkQuery(){
        System.out.println("=======================");
        memberJpaRepository.save(new Member("a", 10));
        memberJpaRepository.save(new Member("b", 11));
        memberJpaRepository.save(new Member("c", 12));
        memberJpaRepository.save(new Member("d", 13));

        int resultCount = memberJpaRepository.bulkAgePlus(12);
        assertThat(resultCount).isEqualTo(2);
    }


}