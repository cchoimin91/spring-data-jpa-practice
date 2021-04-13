package study.datajap.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import study.datajap.dto.MemberDto;
import study.datajap.entity.Member;

import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member  m where m.username=:username and m.age =:age")
    List<Member> findByUsernameQueryAnnotation(@Param("username") String username, @Param("age") int age);

    @Query("select new study.datajap.dto.MemberDto(m.id, m.username, t.name) from Member m left join  m.team t  ")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names")Collection<String> names);

    List<Member> findListByUsername(String name);

    Member findMemberByUsername(String username);

    Optional<Member> findOptionalByUsername(String username);

    Page<Member> findByAge(int age , Pageable pageable);

    /**
     * /카운트쿼리는 조인 불필요 -> 성능 최적화
     */
    @Query(value = "select m from Member m left join m.team"  , countQuery = "select count(m) from Member m")
    Page<Member> findByAge2(int age , Pageable pageable);

    /**
     * excuteUpdate() 호출함, 영속성에 주의한다.  clearAutomatically=true-> clear()를 자동으로 해줌
     */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age=m.age+1 where m.age>= :age")
    int bulkAgeplus(@Param("age") int age);

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m ")
    List<Member> findMemberEntityGraph();

    /**
     * -내부적으로 성능최적화
     * -스냅샷만들지 않음
     * -데이터가 변경되지않음 = 변경감지x
     * 조심할점:다 readOnly해야지! 해봐야 성능최적화 얼마되지않음, 진짜중요한거만..
     *          성능 테스트해보고 적용하는걸 권장
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);

    List<UsernameOnly> findProjections2ByUsername(@Param("username") String username);

    @Query(value = "select * from member  where username=?", nativeQuery = true)
    Member findByNativeQuery(String name);

}
