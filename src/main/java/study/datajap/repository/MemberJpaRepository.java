package study.datajap.repository;

import org.springframework.stereotype.Repository;
import study.datajap.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public Member find(long id){
        return em.find(Member.class, id);
    }

}
