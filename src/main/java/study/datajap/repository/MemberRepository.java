package study.datajap.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajap.dto.MemberDto;
import study.datajap.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member  m where m.username=:username and m.age =:age")
    List<Member> findByUsernameQueryAnnotation(@Param("username") String username, @Param("age") int age);

    @Query("select new study.datajap.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t  ")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names")Collection<String> names);

    List<Member> findListByUsername(String name);

    Member findMemberByUsername(String username);

    Optional<Member> findOptionalByUsername(String username);

    Page<Member> findByAge(int age , Pageable pageable);

}
