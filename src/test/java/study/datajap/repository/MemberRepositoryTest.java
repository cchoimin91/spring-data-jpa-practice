package study.datajap.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajap.dto.MemberDto;
import study.datajap.entity.Member;
import study.datajap.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@Rollback(false)
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;


    @Test
    public void test(){
        List<Member> members = memberRepository.findByUsername("민0");
        assertThat(members).extracting("username").containsExactly("민0");
    }

    @Test
    public void testQueryAnnotation(){
        List<Member> members = memberRepository.findByUsernameQueryAnnotation("민0", 0);
        assertThat(members.size()).isEqualTo(1);
    }

    /**
     * DTO로 반환하기
     */
    @Test
    public void testGetDto(){
        List<MemberDto> members = memberRepository.findMemberDto();
        for (MemberDto member : members) {
            System.out.println("member = " + member);
        }
        assertThat(members.size()).isEqualTo(30);
    }

    @Test
    public void testQueryIn(){

        Member m1 = new Member("민", 10);
        Member m2 = new Member("두", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> userNames = memberRepository.findByNames(Arrays.asList("민","두"));
        for (Member userName : userNames) {
            System.out.println("userName = " + userName);
        }

    }

    @Test
    public void returnType(){
        System.out.println("===========");

        Member m1 = new Member("민", 10);
        Member m2 = new Member("두", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        /**
         * 값이 없으면 null 반환이 아님
         */
        List<Member> list = memberRepository.findListByUsername("없는값");
        System.out.println("userNames = " + list);

        /**
         *  데이터없으면 NoResultException 발생하지만, data jpa에서는 감싸서 NULL로 반환해버림
         */
        Member member = memberRepository.findMemberByUsername("없는값");
        System.out.println("oneResult = " + member);

        /**
         * 결과 2개면 Exeption 발생함
         * 원래는 NonUniqueResultExeption 발생하지만
         * data jpa는 spring Exception으로 변환해서 반환한다
         */
        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("없는값");
        System.out.println("optionalMember = " + optionalMember);

    }

    @Test
    public void dataJpaPaging(){
        System.out.println("===========");

        memberRepository.save(new Member("111", 10));
        memberRepository.save(new Member("222", 10));
        memberRepository.save( new Member("333", 10));
        memberRepository.save( new Member("444", 10));
        memberRepository.save( new Member("555", 10));
        memberRepository.save( new Member("666", 10));

        System.out.println("================ PAGE REQUEST");
        PageRequest pageRequest = PageRequest.of(1,3, Sort.by(Sort.Direction.ASC,"username"));

        int age =10;
        /**
         * - 반환타입 Page면 total count 쿼리도 같이 날아감, sort도 안함 (최적함)
         */
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member = " + member);
        }

        long totalCount = page.getTotalElements();
        System.out.println("totalCount = " + totalCount);

        /**
         * 모바일 디바이스에 많이씀 (더보기..)
         * total count 쿼리 안날림
         * size+1로 가져옴 =
         */
        Slice<Member> page2 = memberRepository.findByAge(age, pageRequest);

        // entity그대로 반환하지말고 DTO로 반환할 것
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(6);
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.isFirst()).isFalse();
        assertThat(page.hasNext()).isFalse();
    }


    @Test
    public void bulkQuery(){
        System.out.println("=======================");
        memberRepository.save(new Member("a", 10));
        memberRepository.save(new Member("b", 11));
        memberRepository.save(new Member("c", 12));
        memberRepository.save(new Member("d", 13));

        //em.flush();
        //em.clear();
        //List<Member> members = memberRepository.findByUsername("a");

        int resultCount = memberRepository.bulkAgeplus(12);

        assertThat(resultCount).isEqualTo(2);
    }

    @Test
    public void entityGraph(){
        System.out.println("=======================");

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("a", 10));
        memberRepository.save(new Member("b", 11));

        em.flush();
        em.clear();

        List<Member> members  = memberRepository.findMemberEntityGraph();
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member>team>name = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint(){
        System.out.println("========================================");
        Member member1 = new Member("aaa", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findReadOnlyByUsername("aaa");
        findMember.setUsername("bbb"); //update 안됨
        em.flush();
    }

    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void JpaEventBaseEntity() throws Exception{
        System.out.println("#############################");

        Member member = new Member("민");
        memberRepository.save(member); // @Prepersist 작동
        LocalDateTime before = member.getUpdateDate();

        Thread.sleep(3000);

        member.setAge(1);

        em.flush(); //@PreUpdate 작동
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        System.out.println(">>>>> findMember.getRegDate = " + findMember.getRegDate());
        System.out.println(">>>>> findMember.getUpdateDate = " + findMember.getUpdateDate());
        System.out.println(">>>>> beforeUpdateDate= " + before);
        System.out.println(">>>>> beforeUpdateDate= " + before);
        System.out.println(">>>>> findMember.regUserId= " + findMember.getRegUserId());
        System.out.println(">>>>> findMember.updateUserId= " + findMember.getUpdateUserId());
    }

    @Test
    public void projections(){
        System.out.println("=======================");

        memberRepository.save(new Member("a", 10));
        memberRepository.save(new Member("a", 10));
        memberRepository.save(new Member("b", 11));

        em.flush();
        em.clear();

        //proxy
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("a");

        for (UsernameOnly usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly.getUsername());
        }

        //proxy
        List<UsernameOnly> result2 = memberRepository.findProjections2ByUsername("a");

        for (UsernameOnly usernameOnly : result2) {
            System.out.println("usernameOnly.getUsernameAndAge() = " + usernameOnly.getUsernameAndAge());
        }
    }

    @Test
    public void nativeQuery() {
        System.out.println("=======================");

        memberRepository.save(new Member("a", 10));

        em.flush();
        em.clear();

        Member member  = memberRepository.findByNativeQuery("a");
        System.out.println("member = " + member);
    }

}