package study.datajap.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import study.datajap.dto.MemberDto;
import study.datajap.entity.Member;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    public void test(){
        System.out.println("===========");
        List<Member> members = memberRepository.findByUsername("aaaa");
    }

    @Test
    public void testQueryAnnotation(){
        System.out.println("===========");
        List<Member> members = memberRepository.findByUsernameQueryAnnotation("member1", 10);
    }

    @Test
    public void testGetDto(){
        System.out.println("===========");
        List<MemberDto> members = memberRepository.findMemberDto();
        for (MemberDto member : members) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void testQueryIn(){
        System.out.println("===========");

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


}